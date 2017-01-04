package com.caregiving.services.android.caregiver.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by aloys on 5/10/2016.
 */
public class GsonUtils {

    private static  final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static <T> T fromJson(Reader reader, Type type) {
        return GSON.fromJson(reader, type);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return GSON.fromJson(jsonString, clazz);
    }
}
