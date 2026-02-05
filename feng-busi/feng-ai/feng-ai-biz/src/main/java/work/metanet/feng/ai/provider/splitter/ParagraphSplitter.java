package work.metanet.feng.ai.provider.splitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 段落切割器
 * @author EdisonFeng
 * @since 2025/5/16
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public class ParagraphSplitter implements DocumentSplitter {

    private static final int MAX_PARAGRAPH_LENGTH = 500;
    private static final Pattern PARAGRAPH_END_PATTERN = Pattern.compile("[\\n\\r]");
    private static final Pattern FALLBACK_SPLIT_PATTERN = Pattern.compile("[。！!？?]");

    @Override
    public List<TextSegment> split(Document document) {
        List<TextSegment> segments = new ArrayList<>();
        String text = document.text();
        
        if (text == null || text.isEmpty()) {
            return segments;
        }

        int start = 0;
        Matcher matcher = PARAGRAPH_END_PATTERN.matcher(text);

        while (start < text.length()) {
            boolean found = matcher.find(start);
            int end = found ? matcher.end() : text.length();

            // 检查段落长度
            if (end - start > MAX_PARAGRAPH_LENGTH) {
                // 向前搜索句子结束符号作为分割点
                String subText = text.substring(start, Math.min(start + MAX_PARAGRAPH_LENGTH, text.length()));
                Matcher fallbackMatcher = FALLBACK_SPLIT_PATTERN.matcher(subText);
                if (fallbackMatcher.find()) {
                    end = start + fallbackMatcher.end();
                } else {
                    end = start + MAX_PARAGRAPH_LENGTH;
                }
            }

            String paragraph = text.substring(start, end).trim();
            if (!paragraph.isEmpty()) {
                segments.add(TextSegment.from(paragraph, document.metadata()));
            }

            start = end;
            if (found && start < text.length() && Character.isWhitespace(text.charAt(start))) {
                start++; // 跳过分割符后的空白字符
            }
        }

        return segments;
    }
}
