package src.islab1jee.utils;

import jakarta.json.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonUtils {

    private static final Logger logger = Logger.getLogger(JsonUtils.class.getName());

    public static List<JsonObject> parseJsonArray(InputStream input) throws Exception {
        String content = new String(input.readAllBytes()).trim();
        List<JsonObject> list = new ArrayList<>();

        logger.info("Начало разбора JSON. Длина входного потока: " + content.length());

        if (content.startsWith("[")) {
            logger.info("Определён формат: JSON массив.");
            try (JsonReader reader = Json.createReader(new StringReader(content))) {
                JsonArray arr = reader.readArray();
                for (JsonValue val : arr) {
                    if (val.getValueType() == JsonValue.ValueType.OBJECT) {
                        list.add(val.asJsonObject());
                    } else {
                        logger.warning("Пропущено значение не-объект: " + val);
                    }
                }
            }
        } else if (content.startsWith("{")) {
            logger.info("Определён формат: JSON объект.");
            try (JsonReader reader = Json.createReader(new StringReader(content))) {
                JsonObject obj = reader.readObject();
                if (obj.containsKey("objects") && obj.get("objects").getValueType() == JsonValue.ValueType.ARRAY) {
                    logger.info("Найден массив 'objects', размер: " + obj.getJsonArray("objects").size());
                    for (JsonValue val : obj.getJsonArray("objects")) {
                        if (val.getValueType() == JsonValue.ValueType.OBJECT) {
                            list.add(val.asJsonObject());
                        } else {
                            logger.warning("Пропущено значение не-объект в 'objects': " + val);
                        }
                    }
                } else {
                    list.add(obj);
                }
            }
        } else {
            logger.warning("Неизвестный формат JSON: " + content.substring(0, Math.min(50, content.length())));
        }

        logger.info("Разбор JSON завершён. Найдено объектов: " + list.size());
        return list;
    }
}
