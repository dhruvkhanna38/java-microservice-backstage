package {{cookiecutter.group_id}}.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Convertor {
    private Convertor() { }
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode toNode(final Object obj) {
        return MAPPER.convertValue(obj, JsonNode.class);

    }

}
