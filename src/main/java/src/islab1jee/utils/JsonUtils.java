package src.islab1jee.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class JsonUtils {

    private static final Logger logger = Logger.getLogger(JsonUtils.class.getName());

    @Inject
    ObjectMapper mapper;

    public List<JsonNode> parseJsonArray(InputStream input) throws Exception {
        List<JsonNode> result = new ArrayList<>();

        JsonNode root = mapper.readTree(input);
        logger.info("JSON корневой тип: " + root.getNodeType());

        if (root.isArray()) {
            root.forEach(result::add);
        } else if (root.isObject()) {
            if (root.has("objects") && root.get("objects").isArray()) {
                root.get("objects").forEach(result::add);
            } else {
                result.add(root);
            }
        } else {
            logger.warning("Неизвестный формат JSON: " + root);
        }

        logger.info("JSON объектов распознано: " + result.size());
        return result;
    }
}
