package work.metanet.feng.common.core.util;

import cn.hutool.core.bean.BeanUtil;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import work.metanet.feng.common.core.constant.PromptConst;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tycoding
 * @since 2024/3/1
 */
public class PromptUtil {

    public static Prompt build(String message) {
        return new Prompt(message);
    }

    public static Prompt build(String message, String promptText) {
    	Map<String, String> mapMsg = new HashMap<>();
    	mapMsg.put(PromptConst.QUESTION, message);
    	
        return new PromptTemplate(promptText + PromptConst.EMPTY).apply(mapMsg);
    }

    public static Prompt build(String message, String promptText, Object param) {
        Map<String, Object> params = BeanUtil.beanToMap(param, false, true);
        params.put(PromptConst.QUESTION, message);
        return new PromptTemplate(promptText).apply(params);
    }

    public static Prompt buildDocs(String message) {
    	Map<String, String> mapMsg = new HashMap<>();
    	mapMsg.put(PromptConst.QUESTION, message);
    	
        return new PromptTemplate(PromptConst.DOCUMENT).apply(mapMsg);
    }
}
