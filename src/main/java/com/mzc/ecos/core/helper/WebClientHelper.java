package com.mzc.ecos.core.helper;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Log4j2
public class WebClientHelper {

  private static volatile WebClientHelper webClientHelper;
  private final WebClient webClient;

  @Getter
  @Setter
  private long connectTimeout = 4000;
  @Getter
  @Setter
  private long readTimeout = 4000;

  private static volatile HttpHeaders httpHeaders = new HttpHeaders();

  private WebClientHelper() {

    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // to unlimited memory size
        .build();

    webClient = WebClient.builder().exchangeStrategies(exchangeStrategies).build();
  }

  public static WebClientHelper getInstance() {
    if (webClientHelper == null) {
      synchronized (WebClientHelper.class) {
        if (webClientHelper == null) {
          webClientHelper = new WebClientHelper();
          httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
          httpHeaders.add(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
        }
      }
    }

    return webClientHelper;
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @return valueType model (class)
   */
  public String get(URI uri) {
    return get(uri, String.class, httpHeaders);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param headers HttpHeaders
   * @return valueType model (class)
   */
  public String get(URI uri, HttpHeaders headers) {
    return get(uri, String.class, headers);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param paramObject Object
   * @return valueType model (class)
   */
  public String get(URI uri, Object paramObject) {
        return get(uri, paramObject, httpHeaders);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param paramObject Object
   * @param headers HttpHeaders
   * @return valueType model (class)
   */
  public String get(URI uri, Object paramObject, HttpHeaders headers) {
    return get(uri, paramObject, String.class, headers);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param <T>       (class) valueType
   * @return <T>      (class)
   */
  public <T> T get(URI uri, Class<T> valueType) {
    return syncHttpClient(HttpMethod.GET, uri, "", valueType, httpHeaders);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param <T>       (class) valueType
   * @param headers   HttpHeaders
   * @return <T>      (class)
   */
  public <T> T get(URI uri, Class<T> valueType, HttpHeaders headers) {
    return syncHttpClient(HttpMethod.GET, uri, "", valueType, headers);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param paramObject Object
   * @param <T>       (class) valueType
   * @return <T>      (class)
   */
  public <T> T get(URI uri, Object paramObject, Class<T> valueType) {
    return syncHttpClient(HttpMethod.GET, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http get
   *
   * @param uri       request uri
   * @param paramObject Object
   * @param <T>       (class) valueType
   * @param headers   HttpHeaders
   * @return <T>      (class)
   */
  public <T> T get(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return syncHttpClient(HttpMethod.GET, uri, paramObject, valueType, headers);
  }

  /**
   * http post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return String
   */
  public String post(URI uri, Object paramObject) {
    return post(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return String
   */
  public String post(URI uri, Object paramObject, HttpHeaders headers) {
    return post(uri, paramObject, String.class, headers);
  }

  /**
   * http post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param <T>         (class) valueType
   * @param valueType   response model
   * @return <T>        (class) valueType
   */
  public <T> T post(URI uri, Object paramObject, Class<T> valueType) {
    return syncHttpClient(HttpMethod.POST, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param <T>         (class) valueType
   * @param valueType   response model
   * @param headers     HttpHeaders
   * @return <T>        (class) valueType
   */
  public <T> T post(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return syncHttpClient(HttpMethod.POST, uri, paramObject, valueType, headers);
  }

  /**
   * http delete request
   *
   * @param uri         request uri
   * @param valueType   (class) response model
   * @return <T>        (class) valueType
   */
  public <T> T delete(URI uri, Class<T> valueType) {
    return delete(uri, new HashMap<>(), valueType, httpHeaders);
  }

  /**
   * http delete request
   *
   * @param uri         request uri
   * @param valueType   (class) response model
   * @param headers     HttpHeaders
   * @return <T>        (class) valueType
   */
  public <T> T delete(URI uri, Class<T> valueType, HttpHeaders headers) {
    return delete(uri, new HashMap<>(), valueType, headers);
  }

  /**
   * http delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return String
   */
  public String delete(URI uri, Object paramObject) {
    return delete(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return String
   */
  public String delete(URI uri, Object paramObject, HttpHeaders headers) {
    return delete(uri, paramObject, String.class, headers);
  }

  /**
   * http delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   (class) response model
   * @return <T>        (class) valueType
   */
  public <T> T delete(URI uri, Object paramObject, Class<T> valueType) {
    return syncHttpClient(HttpMethod.DELETE, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   (class) response model
   * @param headers     HttpHeaders
   * @return <T>        (class) valueType
   */
  public <T> T delete(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return syncHttpClient(HttpMethod.DELETE, uri, paramObject, valueType, headers);
  }

  /**
   * http put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return valueType model (class)
   */
  public String put(URI uri, Object paramObject) {
    return put(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return valueType model (class)
   */
  public String put(URI uri, Object paramObject, HttpHeaders headers) {
    return put(uri, paramObject, String.class, headers);
  }

  /**
   * http put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @return <T> (class) valueType model
   */
  public <T> T put(URI uri, Object paramObject, Class<T> valueType) {
    return syncHttpClient(HttpMethod.PUT, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @param headers     HttpHeaders
   * @return <T> (class) valueType model
   */
  public <T> T put(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return syncHttpClient(HttpMethod.PUT, uri, paramObject, valueType, headers);
  }

  /**
   * http patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return String
   */
  public String patch(URI uri, Object paramObject) {
    return patch(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return String
   */
  public String patch(URI uri, Object paramObject, HttpHeaders headers) {
    return patch(uri, paramObject, String.class, headers);
  }

  /**
   * http patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @return <T> (class) valueType model
   */
  public <T> T patch(URI uri, Object paramObject, Class<T> valueType) {
    return syncHttpClient(HttpMethod.PATCH, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @param headers     HttpHeaders
   * @return <T> (class) valueType model
   */
  public <T> T patch(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return syncHttpClient(HttpMethod.PATCH, uri, paramObject, valueType, headers);
  }

  /**
   * http patch request
   *
   * @param uri       request uri
   * @param valueType <T> (class) response model
   * @return          <T> (class) valueType model
   */
  public <T> T patch(URI uri, Class<T> valueType) {
    return patch(uri, new HashMap<>(), valueType, httpHeaders);
  }

  /**
   * http async get
   *
   * @param uri       request uri
   * @return <T> Mono<T> valueType model
   */
  public Mono<String> asyncGet(URI uri) {
    return asyncGet(uri, String.class, httpHeaders);
  }

  /**
   * http async get
   *
   * @param uri       request uri
   * @param headers   HttpHeaders
   * @return <T> Mono<T> valueType model
   */
  public Mono<String> asyncGet(URI uri, HttpHeaders headers) {
    return asyncGet(uri, String.class, headers);
  }

  /**
   * http async get
   *
   * @param uri       request uri
   * @param valueType <T> (class) response model
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncGet(URI uri, Class<T> valueType) {
    return asyncHttpClient(HttpMethod.GET, uri, "", valueType, httpHeaders);
  }

  /**
   * http async get
   *
   * @param uri       request uri
   * @param valueType <T> (class) response model
   * @param headers   HttpHeaders
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncGet(URI uri, Class<T> valueType, HttpHeaders headers) {
    return asyncHttpClient(HttpMethod.GET, uri, "", valueType, headers);
  }

  /**
   * http async post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncPost(URI uri, Object paramObject) {
    return asyncPost(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http async post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncPost(URI uri, Object paramObject, HttpHeaders headers) {
    return asyncPost(uri, paramObject, String.class, headers);
  }

  /**
   * http async post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncPost(URI uri, Object paramObject, Class<T> valueType) {
    return asyncHttpClient(HttpMethod.POST, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http async post request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @param headers     HttpHeaders
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncPost(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return asyncHttpClient(HttpMethod.POST, uri, paramObject, valueType, headers);
  }

  /**
   * http async delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncDelete(URI uri, Object paramObject) {
    return asyncDelete(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http async delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncDelete(URI uri, Object paramObject, HttpHeaders headers) {
    return asyncDelete(uri, paramObject, String.class, headers);
  }

  /**
   * http async delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncDelete(URI uri, Object paramObject, Class<T> valueType) {
    return asyncHttpClient(HttpMethod.DELETE, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http async delete request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> (class) response model
   * @param headers     HttpHeaders
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncDelete(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return asyncHttpClient(HttpMethod.DELETE, uri, paramObject, valueType, headers);
  }

  /**
   * http async put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncPut(URI uri, Object paramObject) {
    return asyncPut(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http async put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncPut(URI uri, Object paramObject, HttpHeaders headers) {
    return asyncPut(uri, paramObject, String.class, headers);
  }

  /**
   * http async put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> Mono<T> response model
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncPut(URI uri, Object paramObject, Class<T> valueType) {
    return asyncHttpClient(HttpMethod.PUT, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http async put request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> Mono<T> response model
   * @param headers     HttpHeaders
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncPut(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return asyncHttpClient(HttpMethod.PUT, uri, paramObject, valueType, headers);
  }

  /**
   * http async patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncPatch(URI uri, Object paramObject) {
    return asyncPatch(uri, paramObject, String.class, httpHeaders);
  }

  /**
   * http async patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param headers     HttpHeaders
   * @return Mono<String> valueType model
   */
  public Mono<String> asyncPatch(URI uri, Object paramObject, HttpHeaders headers) {
    return asyncPatch(uri, paramObject, String.class, headers);
  }

  /**
   * http async patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> Mono<T> response model
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncPatch(URI uri, Object paramObject, Class<T> valueType) {
    return asyncHttpClient(HttpMethod.PATCH, uri, paramObject, valueType, httpHeaders);
  }

  /**
   * http async patch request
   *
   * @param uri         request uri
   * @param paramObject param object (converted json)
   * @param valueType   <T> Mono<T> response model
   * @param headers     HttpHeaders
   * @return <T> Mono<T> valueType model
   */
  public <T> Mono<T> asyncPatch(URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers) {
    return asyncHttpClient(HttpMethod.PATCH, uri, paramObject, valueType, headers);
  }

  private <T> T syncHttpClient(
    HttpMethod httpMethod, URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers
  ) {

    uri = URI.create(uri.toASCIIString());

    return webClient.method(httpMethod)
      .uri(uri)
      .headers(httpHeaders -> {
        httpHeaders.addAll(headers);
      })
      .body(BodyInserters.fromValue(paramObject))
      .exchange()
      .timeout(Duration.ofMillis(connectTimeout))
      .doOnError(e -> log.error("Connection Error : ", e))
      .onErrorReturn(null)
      .block(Duration.ofMillis(readTimeout))
      .bodyToMono(valueType)
      .doOnError(e -> log.error("Mapping Error : ", e))
      .onErrorReturn(null)
      .block();
  }

  private <T> Mono<T> asyncHttpClient(
    HttpMethod httpMethod, URI uri, Object paramObject, Class<T> valueType, HttpHeaders headers
  ) {

    uri = URI.create(uri.toASCIIString());

    return webClient.method(httpMethod)
      .uri(uri)
      .headers(httpHeaders -> {
        httpHeaders.addAll(headers);
      })
      .body(BodyInserters.fromValue(paramObject))
      .exchange()
      .timeout(Duration.ofMillis(connectTimeout))
      .flatMap(clientResponse -> clientResponse.bodyToMono(valueType));
  }
}
