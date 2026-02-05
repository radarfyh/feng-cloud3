//package work.metanet.feng.admin.api.handler;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import java.io.IOException;
//
//public class JsonObjectDeserializer extends JsonDeserializer<JsonNode> {
//    @Override
//    public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        JsonNode node = p.readValueAsTree();
//        if (!node.isObject()) {
//            throw new IOException("varParameter必须是JSON对象类型");
//        }
//        return (ObjectNode) node;
//    }
//}
