package ltd.huntinginfo.feng.ai.service;

import java.util.List;

import ltd.huntinginfo.feng.ai.api.entity.AigcDocs;
import ltd.huntinginfo.feng.ai.api.entity.AigcOss;

public interface DocPreSplitService {
	List<AigcDocs> preSplitAndStore(AigcOss oss, Integer knowledgeId);
}
