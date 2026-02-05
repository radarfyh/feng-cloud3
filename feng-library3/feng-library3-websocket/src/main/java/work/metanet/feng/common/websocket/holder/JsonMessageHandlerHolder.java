package work.metanet.feng.common.websocket.holder;

import work.metanet.feng.common.websocket.handler.JsonMessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author edison 2022/09/15
 * @version 1.0
 */
public final class JsonMessageHandlerHolder {

	private JsonMessageHandlerHolder() {
	}

	private static final Map<String, JsonMessageHandler> MESSAGE_HANDLER_MAP = new ConcurrentHashMap<>();

	public static JsonMessageHandler getHandler(String type) {
		return MESSAGE_HANDLER_MAP.get(type);
	}

	public static void addHandler(JsonMessageHandler jsonMessageHandler) {
		MESSAGE_HANDLER_MAP.put(jsonMessageHandler.type(), jsonMessageHandler);
	}

}
