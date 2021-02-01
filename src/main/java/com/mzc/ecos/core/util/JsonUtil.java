package com.mzc.ecos.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonUtil {

  public static Gson getGsonWithTypeAdapter() {
    return new GsonBuilder()
        .registerTypeAdapter(Date.class, new GsonDateConverter())
        .registerTypeAdapter(LocalDateTime.class, new GsonDateTimeConverter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .create();
  }

  public static Gson getGsonWithTypeAdapterPretty() {
    return new GsonBuilder()
        .registerTypeAdapter(Date.class, new GsonDateConverter())
        .registerTypeAdapter(LocalDateTime.class, new GsonDateTimeConverter())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .setPrettyPrinting()
        .create();
  }


  public static String printPrettyJsonFromString(String jsonString) {
    return convertObjectToJsonString(convertJsonStringToObject(jsonString), true);
  }

  // Object를 json String 형태로 변환
  public static String convertObjectToJsonString(Object object) {
    return convertObjectToJsonString(object, false);
  }

  // Object를 json String 형태로 이쁘게 변환
  public static String convertObjectToJsonString(Object object, boolean pretty) {
    Gson gson;
    if (pretty) {
      gson = getGsonWithTypeAdapterPretty();
    } else {
      gson = getGsonWithTypeAdapter();
    }
    return gson.toJson(object);
  }

  public static Object convertJsonStringToObject(String jsonString) {
    return convertJsonStringToObject(jsonString, Object.class);
  }

  // json string to object
  public static <T> T convertJsonStringToObject(String jsonString, Class<T> valueType) {

    T obj = null;
    try {

      if (jsonString == null || jsonString.isEmpty()) {
        return null;
      }
      Gson gson = getGsonWithTypeAdapter();
      obj = gson.fromJson(jsonString, valueType);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return (T) obj;
  }

}
