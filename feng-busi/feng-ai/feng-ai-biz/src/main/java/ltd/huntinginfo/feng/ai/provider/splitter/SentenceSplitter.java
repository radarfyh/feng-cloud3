package ltd.huntinginfo.feng.ai.provider.splitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import ltd.huntinginfo.feng.ai.utils.FileParseUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.xiaoymin.knife4j.core.util.StrUtil;

/**
 * 句子切割器
 * @author EdisonFeng
 * @since 2025/5/16
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public class SentenceSplitter implements DocumentSplitter {

	private static final int DEFAULT_MAX_LENGTH = 200;
	private static final int DEFAULT_OVERLAP_SIZE = 20;
//    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("[。！!？?]");
    // 中文句子结束符：。！？
    private static final Pattern CHINESE_SENTENCE_END = Pattern.compile("[。！？]");
    private static final Pattern FALLBACK_SPLIT_PATTERN = Pattern.compile("[；，]");
    
    private final int maxLength;
    private final int overlapSize;
    
    public SentenceSplitter(int maxLength, int overlapSize) {
        this.maxLength = maxLength;
        this.overlapSize = overlapSize;
    }
    
    public SentenceSplitter() {
        this(DEFAULT_MAX_LENGTH, DEFAULT_OVERLAP_SIZE);
    }
    
    @Override
    public List<TextSegment> split(Document document) {
    	if (StrUtil.isBlank(document.text())) {
    		return new ArrayList<>();
    	}
    	if (overlapSize > 0) {
    		return split2(document);
    	} else {
    		return split1(document);
    	}
    }
    
    /**
     * 拆分方法1：按照maxLength拆分，未实现overlapSize
     * @param document
     * @return 分段列表
     */
    public List<TextSegment> split1(Document document) {
        List<TextSegment> segments = new ArrayList<>();
        String text = document.text();
        
        if (text == null || text.isEmpty()) {
            return segments;
        }

        int start = 0;
        Matcher matcher = CHINESE_SENTENCE_END.matcher(text);

        while (start < text.length()) {
            boolean found = matcher.find(start);
            int end = found ? matcher.end() : text.length();

            // 检查句子长度
            if (end - start > maxLength) {
                // 向前搜索逗号作为分割点
                String subText = text.substring(start, Math.min(start + maxLength, text.length()));
                Matcher fallbackMatcher = FALLBACK_SPLIT_PATTERN.matcher(subText);
                if (fallbackMatcher.find()) {
                    end = start + fallbackMatcher.end();
                } else {
                    end = start + maxLength;
                }
            }

            String sentence = text.substring(start, end).trim();
            if (!sentence.isEmpty()) {
                segments.add(TextSegment.from(sentence, document.metadata()));
            }

            start = end;
            if (found && start < text.length() && Character.isWhitespace(text.charAt(start))) {
                start++; // 跳过分割符后的空白字符
            }
        }

        return segments;
    }
    
    /**
     * 拆分方法2：实现按句子拆分，并实现交叉覆盖
     * @param document
     * @return 分段列表
     */
    public List<TextSegment> split2(Document document) {
        List<TextSegment> segments = new ArrayList<>();
        String text = document.text();
        if (text == null || text.isEmpty()) {
            return segments;
        }

        // 第一步：按中文句子结束符分割
        List<String> sentences = new ArrayList<>();
        Matcher matcher = CHINESE_SENTENCE_END.matcher(text);
        int lastEnd = 0;
        
        while (matcher.find()) {
            int end = matcher.end();
            String sentence = text.substring(lastEnd, end).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            lastEnd = end;
        }
        
        // 添加最后未结束的句子
        if (lastEnd < text.length()) {
            String lastPart = text.substring(lastEnd).trim();
            if (!lastPart.isEmpty()) {
                sentences.add(lastPart);
            }
        }

        // 第二步：处理每个句子
        StringBuilder currentSegment = new StringBuilder();
        String lastSubSentence = "";
        
        for (String sentence : sentences) {
            // 检查句子长度
            if (sentence.length() > maxLength) {
                // 句子过长，按逗号分号等二次分割
                List<String> subSentences = new ArrayList<>();
                Matcher fallbackMatcher = FALLBACK_SPLIT_PATTERN.matcher(sentence);
                int subLastEnd = 0;
                
                while (fallbackMatcher.find()) {
                    int end = fallbackMatcher.end();
                    subSentences.add(sentence.substring(subLastEnd, end).trim());
                    subLastEnd = end;
                }
                
                if (subLastEnd < sentence.length()) {
                    subSentences.add(sentence.substring(subLastEnd).trim());
                }
                
                // 处理子分句
                for (String subSentence : subSentences) {
                    processSubSentence(subSentence, currentSegment, lastSubSentence, segments, document.metadata());
                    lastSubSentence = subSentence;
                }
            } else {
                processSubSentence(sentence, currentSegment, lastSubSentence, segments, document.metadata());
                lastSubSentence = sentence;
            }
        }
        
        // 添加最后一个segment
        if (currentSegment.length() > 0) {
            segments.add(TextSegment.from(currentSegment.toString(), document.metadata()));
        }

        return segments;
    }

    /**
     * 重叠部分(overlap)：从上一句结尾开始找最后一个逗号(分号、顿号)，取逗号(分号、顿号)之后的内容
     * 分句处理：当前句子如果太长，从尾部找逗号(分号、顿号)进行分割
     * 分段存储：确保每个segment不超过maxLength，并正确处理重叠
     * @param currentSentence
     * @param currentSegment
     * @param lastSentence
     * @param segments
     * @param metadata
     */
    private void processSubSentence(String currentSentence, StringBuilder currentSegment,  String lastSentence, List<TextSegment> segments, Metadata metadata) {
        // 1. 计算重叠部分：从上一句找最后一个逗号后的内容
        String overlap = "";
        if (!lastSentence.isEmpty()) {
            int lastCommaIndex = lastSentence.lastIndexOf('，');
            if (lastCommaIndex == -1) lastCommaIndex = lastSentence.lastIndexOf(';');
            if (lastCommaIndex == -1) lastCommaIndex = lastSentence.lastIndexOf(',');
            if (lastCommaIndex == -1) lastCommaIndex = lastSentence.lastIndexOf('、');
            if (lastCommaIndex == -1) lastCommaIndex = lastSentence.lastIndexOf('：');
            if (lastCommaIndex != -1) {
                overlap = lastSentence.substring(lastCommaIndex + 1).trim();
                if (overlap.length() > overlapSize) {
                    overlap = overlap.substring(overlap.length() - overlapSize);
                }
            }
        }

        // 2. 构建候选文本（当前内容+重叠部分）
        String candidate = overlap + currentSentence;
        
        // 3. 处理长度限制
        if (candidate.length() <= maxLength) {
            // 如果长度合适，直接添加
            if (currentSegment.length() > 0) {
                segments.add(TextSegment.from(currentSegment.toString(), metadata));
                currentSegment.setLength(0);
            }
            currentSegment.append(candidate);
        } else {
            // 如果太长，从尾部找分割点
            int splitPos = findSplitPosition(currentSentence);
            String firstPart = currentSentence.substring(0, splitPos).trim();
            String remaining = currentSentence.substring(splitPos).trim();
            
            // 添加第一部分到segment
            if (currentSegment.length() > 0) {
                segments.add(TextSegment.from(currentSegment.toString(), metadata));
                currentSegment.setLength(0);
            }
            currentSegment.append(overlap).append(firstPart);
            segments.add(TextSegment.from(currentSegment.toString(), metadata));
            currentSegment.setLength(0);
            
            // 递归处理剩余部分
            if (!remaining.isEmpty()) {
                processSubSentence(remaining, currentSegment, firstPart, segments, metadata);
            }
        }
    }

    // 从句子尾部找合适的分割点（逗号、分号等）
    private int findSplitPosition(String sentence) {
        // 优先从maxLength处往前找分割符
        int maxPos = Math.min(maxLength, sentence.length());
        for (int i = maxPos; i > 0; i--) {
            char c = sentence.charAt(i);
            if (c == '，' || c == ';' || c == ',') {
                return i + 1; // 返回分割符后的位置
            }
        }
        
        // 找不到则按maxLength硬分割
        return maxPos;
    }

}
