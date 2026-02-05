package work.metanet.feng.ai.service;

import java.util.List;

import work.metanet.feng.ai.api.entity.AigcDocs;
import work.metanet.feng.ai.api.entity.AigcOss;

public interface DocPreSplitService {
	List<AigcDocs> preSplitAndStore(AigcOss oss, Integer knowledgeId);
}
