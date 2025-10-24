package src.islab1jee.utils;

import jakarta.json.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<JsonObject> parseJsonArray(InputStream input) throws Exception {
        String content = new String(input.readAllBytes()).trim();
        List<JsonObject> list = new ArrayList<>();
        if (content.startsWith("[")) {
            try (JsonReader reader = Json.createReader(new java.io.StringReader(content))) {
                JsonArray jsonArray = reader.readArray();
                for (JsonValue value : jsonArray) {
                    if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                        list.add(value.asJsonObject());
                    }
                }
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        try (JsonReader reader = Json.createReader(new java.io.StringReader(line))) {
                            JsonObject obj = reader.readObject();
                            list.add(obj);
                        }
                    }
                }
            }
        }
        return list;
    }
}
