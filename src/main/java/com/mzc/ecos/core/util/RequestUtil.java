package com.mzc.ecos.core.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;

@Log4j2
public class RequestUtil {

  public static String urlEncoding(Object string) {
    try {
      return URLEncoder.encode(String.valueOf(string), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  public static String mapToQueryParam(Map<String, ?> queryParams) {
    StringBuilder queryStringBuilder = makeParams(queryParams);

    final String queryString = queryStringBuilder.toString();
    log.debug("Query string : " + queryString.substring(0, queryString.length() - 1));
    return queryString.substring(0, queryString.length() - 1);

  }

  public static String objectToQueryParam(Object o) {
    StringBuilder queryStringBuilder = new StringBuilder();
    final Map<String, String> queryParams = new LinkedHashMap<>();

    try {
      for (Field f : o.getClass().getDeclaredFields()) {
        f.setAccessible(true);
        queryParams.put(f.getName(), ObjectUtils.isEmpty(f.get(o))?"": String.valueOf(f.get(o)));
      }
      queryStringBuilder = makeParams(queryParams);

      log.debug("Map: " + queryParams);
    } catch (Exception e) {
      log.error(e);
    }
    final String queryString = queryStringBuilder.toString();
    log.debug("Query string : " + queryString.substring(0, queryString.length() - 1));
    return queryString.substring(0, queryString.length() - 1);
  }

  private static StringBuilder makeParams(Map<String, ?> queryParams) {
    StringBuilder queryStringBuilder = new StringBuilder();

    for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
      if (entry.getValue() == null) {
        continue;
      }
      queryStringBuilder.append(urlEncoding(entry.getKey()));
      queryStringBuilder.append("=");
      queryStringBuilder.append(urlEncoding(entry.getValue()));
      queryStringBuilder.append("&");
    }
    return queryStringBuilder;
  }
}
