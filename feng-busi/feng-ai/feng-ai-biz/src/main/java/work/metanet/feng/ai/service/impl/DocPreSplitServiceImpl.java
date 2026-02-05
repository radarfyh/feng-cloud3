package work.metanet.feng.ai.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import work.metanet.feng.ai.api.entity.AigcDocs;
import work.metanet.feng.ai.api.entity.AigcOss;
import work.metanet.feng.ai.mapper.AigcDocsMapper;
import work.metanet.feng.ai.service.DocPreSplitService;
import work.metanet.feng.ai.utils.FileParseUtil;

@Service
@RequiredArgsConstructor
public class DocPreSplitServiceImpl implements DocPreSplitService {
    private final AigcDocsMapper docsMapper;
    
    private static final int MAX_CHUNK_SIZE = 1000;
    private static final int OVERLAP_SIZE = 100;
    private static final int MIN_SENTENCE_LENGTH = 50;
    private static final int MIN_FIND_LENGTH = 1;
    
    // 匹配中文句子结束符
    private static final Pattern SENTENCE_END_PATTERN = 
        Pattern.compile("[。！？!?]");
    
    // 用于寻找合适的分割点
    private static final Pattern FALLBACK_SPLIT_PATTERN = 
        Pattern.compile("[，；,;]");

    @Override
    @Transactional
    public List<AigcDocs> preSplitAndStore(AigcOss oss, Integer knowledgeId) {
        List<AigcDocs> result = new ArrayList<>();
        String content = FileParseUtil.extractFileContent(oss.getPath(), oss.getExt());
        
        if (content == null || content.isEmpty()) {
            return result;
        }
        
        if (content.length() <= MAX_CHUNK_SIZE) {
            AigcDocs doc = createDocument(oss, knowledgeId, content, "小文件解析");
            docsMapper.insert(doc);
            result.add(doc);
            return result;
        }
        
        // 1. 先按最大长度切分成初始块
        List<String> initialChunks = splitIntoInitialChunks(content);
        
        // 2. 处理交叉覆盖
        List<AigcDocs> chunks = processOverlapChunks(initialChunks);
        
        // 3. 存储到数据库
        chunks.forEach(chunk -> {
            AigcDocs doc = createDocument(oss, knowledgeId, chunk.getContent(), "大文件拆分");
            if (docsMapper.insert(doc) > 0) {
            	result.add(doc);
            }
        });
        
        return result;
    }

    /**
     * 将内容按MAX_CHUNK_SIZE切分成初始块
     */
    private List<String> splitIntoInitialChunks(String content) {
        List<String> chunks = new ArrayList<>();
        int length = content.length();
        int start = 0;
        
        while (start < length) {
            int end = Math.min(start + MAX_CHUNK_SIZE, length);
            String chunk = content.substring(start, end);
            chunks.add(chunk);
            start = end;
        }
        
        return chunks;
    }

    /**
     * 处理交叉覆盖逻辑
     */
    private List<AigcDocs> processOverlapChunks(List<String> initialChunks) {
        List<AigcDocs> result = new ArrayList<>();
        if (initialChunks.isEmpty()) return result;

        String previousChunk = initialChunks.get(0);
        
        for (int i = 1; i < initialChunks.size(); i++) {
            String currentChunk = initialChunks.get(i);
            
            // 获取前后重叠部分
            OverlapInfo previousOverlap = findOverlapAtEnd(previousChunk);
            OverlapInfo currentOverlap = findOverlapAtStart(currentChunk);
            
            // 合并并调整重叠内容
            currentOverlap.overlap = previousOverlap.overlap + currentOverlap.overlap;
            currentOverlap = adjustOverlap(currentOverlap);
            
            // 添加前一块内容和重叠内容
            addNonEmptyContent(result, previousOverlap.remaining + currentOverlap.overlap);
            
            previousChunk = currentOverlap.overlap + currentOverlap.remaining;
            
        }
        
        addNonEmptyContent(result, previousChunk);
        return result;
    }

    private OverlapInfo adjustOverlap(OverlapInfo overlapInfo) {
        int overlapLength = overlapInfo.overlap.length();
        if (overlapLength > OVERLAP_SIZE) {
            return adjustOverlapSize(overlapInfo, false);
        } else if (overlapLength < MIN_SENTENCE_LENGTH) {
            return adjustOverlapSize(overlapInfo, true);
        }
        return overlapInfo;
    }

    private void addNonEmptyContent(List<AigcDocs> result, String content) {
        if (!content.isEmpty()) {
            result.add(new AigcDocs().setContent(content));
        }
    }

    /**
     * 从文本末尾向前搜索句子结束点
     */
    private OverlapInfo findOverlapAtEnd(String text) {
        Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
        int lastPos = -1;
        
        // 找到最后一个句子结束点
        while (matcher.find()) {
            lastPos = matcher.start();
        }
        
        if (lastPos == -1) {
            // 找不到句子结束点，尝试找逗号分号
            matcher = FALLBACK_SPLIT_PATTERN.matcher(text);
            while (matcher.find()) {
                lastPos = matcher.start();
            }
        }
        
        if (lastPos == -1 || text.length() - lastPos < MIN_FIND_LENGTH) {
            // 没有合适的分割点或分割后太小，取最后OVERLAP_SIZE个字符
            int start = Math.max(0, text.length() - OVERLAP_SIZE);
            return new OverlapInfo(
                text.substring(start),
                text.substring(0, start)
            );
        }
        
        return new OverlapInfo(
            text.substring(lastPos + 1),
            text.substring(0, lastPos + 1)
        );
    }

    /**
     * 从文本开头向后搜索句子结束点
     */
    private OverlapInfo findOverlapAtStart(String text) {
        Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
        
        if (matcher.find()) {
            int endPos = matcher.end();
            if (endPos >= MIN_FIND_LENGTH) {
                return new OverlapInfo(
                    text.substring(0, endPos),
                    text.substring(endPos)
                );
            }
        }
        
        // 找不到句子结束点，尝试找逗号分号
        matcher = FALLBACK_SPLIT_PATTERN.matcher(text);
        if (matcher.find()) {
            int endPos = matcher.end();
            return new OverlapInfo(
                text.substring(0, endPos),
                text.substring(endPos)
            );
        }
        
        // 没有合适的分割点，取前OVERLAP_SIZE个字符
        int end = Math.min(OVERLAP_SIZE, text.length());
        return new OverlapInfo(
            text.substring(0, end),
            text.substring(end)
        );
    }

    /**
     * 调整重叠部分大小
     */
    private OverlapInfo adjustOverlapSize(OverlapInfo currentOverlap, boolean expand) {
        String overlap = currentOverlap.overlap;
        String remaining = currentOverlap.remaining;
        
        if (expand) {
            // 扩大重叠部分逻辑
            if (overlap.length() >= MIN_SENTENCE_LENGTH) {
                return currentOverlap;
            }
            
            int neededLength = MIN_SENTENCE_LENGTH - overlap.length();
            if (neededLength <= 0 || remaining.isEmpty()) {
                return currentOverlap;
            }
            
            // 查找最佳扩展点
            int bestPos = findBestExpansionPosition(remaining, neededLength);
            currentOverlap.overlap = overlap + remaining.substring(0, bestPos);
            currentOverlap.remaining = remaining.substring(bestPos);
        } else {
            // 缩小重叠部分逻辑
            if (overlap.length() <= OVERLAP_SIZE) {
                return currentOverlap;
            }
            
            // 查找最佳缩减点
            int bestPos = findBestReductionPosition(overlap);
            currentOverlap.overlap = overlap.substring(0, bestPos);
            currentOverlap.remaining = overlap.substring(bestPos) + remaining;
        }
        
        return currentOverlap;
    }

    private int findBestExpansionPosition(String text, int neededLength) {
        Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
        int bestPos = Math.min(neededLength, text.length());
        
        while (matcher.find()) {
            if (matcher.start() <= neededLength * 2 && matcher.start() >= MIN_FIND_LENGTH) {
                bestPos = matcher.end();
                break;
            }
        }
        
        // 确保不超过剩余文本长度
        return Math.min(bestPos, text.length());
    }

    private int findBestReductionPosition(String text) {
        int idealPos = OVERLAP_SIZE;
        Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
        int bestPos = idealPos;
        int minDiff = Integer.MAX_VALUE;
        
        while (matcher.find()) {
            int diff = Math.abs(matcher.start() - idealPos);
            if (diff < minDiff) {
                minDiff = diff;
                bestPos = matcher.end();
            }
        }
        
        // 如果没有找到合适位置，使用理想位置
        return minDiff < Integer.MAX_VALUE ? bestPos : idealPos;
    }
    
    private AigcDocs createDocument(AigcOss oss, Integer knowledgeId, String content, String suffix) {
        return new AigcDocs()
            .setOssId(oss.getId())
            .setName(getFileName(oss.getUrl()) + "-" + suffix)
            .setContent(content)
            .setKnowledgeId(knowledgeId);
    }

    private String getFileName(String url) {
        int lastSlash = url.lastIndexOf('/');
        int lastDot = url.lastIndexOf('.');
        if (lastDot > lastSlash) {
            return url.substring(lastSlash + 1, lastDot);
        }
        return url.substring(lastSlash + 1);
    }

    /**
     * 用于存储重叠信息的内部类
     */
    private static class OverlapInfo {
        String overlap;
        String remaining;

        OverlapInfo(String overlap, String remaining) {
            this.overlap = overlap;
            this.remaining = remaining;
        }
    }
}