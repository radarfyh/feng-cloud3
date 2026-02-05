package work.metanet.feng.ai.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentChunk {
    private String text;
    private boolean isOverlap; // 标记是否为重叠内容
    
    public int length() {
        return text.length();
    }
}
