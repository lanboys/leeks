package utils;

import com.google.common.base.Strings;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtil {

    private static final Gson DEFAULT_INSTANCE = GsonInstanceHolder.INSTANCE;

    public static Gson newGson(Map<Type, Object> adapters, ExclusionStrategy... strategies) {
        GsonBuilder gb = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .disableHtmlEscaping();
        if (strategies != null && strategies.length > 0) {
            gb.setExclusionStrategies(strategies);
        }
        if (adapters != null) {
            for (Map.Entry<Type, Object> entry : adapters.entrySet()) {
                gb.registerTypeAdapter(entry.getKey(), entry.getValue());
            }
        }
        return gb.create();
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, DEFAULT_INSTANCE);
    }

    public static <T> T fromJson(String json, Class<T> clazz, Gson gson) {
        if (Strings.isNullOrEmpty(json)) {
            return null;
        }
        if (gson == null) {
            return DEFAULT_INSTANCE.fromJson(json, clazz == null ? Object.class : clazz);
        }
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeOfT) {
        return fromJson(json, typeOfT, DEFAULT_INSTANCE);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeOfT, Gson gson) {
        if (Strings.isNullOrEmpty(json)) {
            return null;
        }
        if (gson == null) {
            return DEFAULT_INSTANCE.fromJson(json, typeOfT);
        }
        return gson.fromJson(json, typeOfT);
    }

    public static String toJson(Object obj) {
        return toJson(obj, DEFAULT_INSTANCE);
    }

    public static String toJson(Object obj, Gson gson) {
        if (obj == null) {
            return null;
        }
        if (gson == null) {
            return DEFAULT_INSTANCE.toJson(obj);
        }
        return gson.toJson(obj);
    }

    private static class GsonInstanceHolder {

        private static final Gson INSTANCE = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz.isAssignableFrom(ThreadLocal.class)
                        || clazz.isAssignableFrom(Runnable.class)
                        || clazz.isAssignableFrom(Thread.class);
                }
            })
            .create();
    }
}
