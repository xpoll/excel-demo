package cn.blmdz.excel.util;

import java.io.IOException;
import java.text.DateFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.base.Strings;

public class JsonMapper {

    public static final JsonMapper JSON_NON_EMPTY_MAPPER;
    public static final JsonMapper JSON_NON_DEFAULT_MAPPER;

    private ObjectMapper mapper = new ObjectMapper();

    static {
        JSON_NON_EMPTY_MAPPER = new JsonMapper(Include.NON_EMPTY);
        JSON_NON_DEFAULT_MAPPER = new JsonMapper(Include.NON_DEFAULT);
    }

    public JsonMapper(Include include) {
        this.mapper.setSerializationInclusion(include);
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static JsonMapper nonEmptyMapper() {
        return JSON_NON_EMPTY_MAPPER;
    }

    public static JsonMapper nonDefaultMapper() {
        return JSON_NON_DEFAULT_MAPPER;
    }

    public String toJson(Object object) {
        try {
            return this.mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (Strings.isNullOrEmpty(jsonString)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(jsonString, clazz);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (Strings.isNullOrEmpty(jsonString)) {
            return null;
        } else {
            try {
                return this.mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public JsonNode treeFromJson(String jsonString) throws IOException {
        return this.mapper.readTree(jsonString);
    }

    public <T> T treeToValue(JsonNode node, Class<T> clazz) throws JsonProcessingException {
        return this.mapper.treeToValue(node, clazz);
    }

    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return this.mapper.getTypeFactory().constructParametrizedType(collectionClass, collectionClass, elementClasses);
    }

    public <T> T update(String jsonString, T object) {
        try {
            return this.mapper.readerForUpdating(object).readValue(jsonString);
        } catch (IOException var5) {
        }
        return null;
    }

    public String toJsonP(String functionName, Object object) {
        return this.toJson(new JSONPObject(functionName, object));
    }

    public void enableEnumUseToString() {
        this.mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public void setDateFormat(DateFormat format) {
        this.mapper.setDateFormat(format);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }
}
