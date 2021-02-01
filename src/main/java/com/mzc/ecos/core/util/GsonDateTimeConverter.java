package com.mzc.ecos.core.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class GsonDateTimeConverter implements JsonSerializer<LocalDateTime>,
    JsonDeserializer<LocalDateTime> {

  public static final String JSONFY_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  @Override
  public JsonElement serialize(LocalDateTime localDateTime, Type srcType,
                               JsonSerializationContext context) {

    ZoneId zoneId = TimeZone.getDefault().toZoneId();        //Zone information
    ZonedDateTime zdtAtAsia = localDateTime.atZone( zoneId );     //Local time in Asia timezone

    return new JsonPrimitive(
        DateTimeFormatter.ofPattern(JSONFY_DATETIME_FORMAT).format(zdtAtAsia));
  }

  @Override
  public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {

    return LocalDateTime
        .parse(json.getAsString(), DateTimeFormatter.ofPattern(JSONFY_DATETIME_FORMAT));
  }

}
