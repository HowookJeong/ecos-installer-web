package com.mzc.ecos.core.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GsonDateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

  public static final String JSONFY_DATE_FORMAT = "yyyy-MM-dd";

  @Override
  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSONFY_DATE_FORMAT);
    return src == null ? null : new JsonPrimitive(simpleDateFormat.format(src));
  }

  @Override
  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSONFY_DATE_FORMAT);
    try {
      return json == null ? null : simpleDateFormat.parse(json.getAsString());
    } catch (ParseException e) {
      throw new JsonParseException(e);
    }
  }
}
