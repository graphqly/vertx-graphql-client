package io.github.graphqly.client;

import io.github.graphqly.client.impl.VertxGraphqlClient;
import io.vertx.core.Future;

import java.util.HashMap;
import java.util.Map;

public abstract class GraphqlClient {

  /**
   * Invoke an operation
   *
   * @param service Service
   * @param operation Operation name
   * @param variables A map of names and values for variables
   * @return If everything's ok, returns JsonObject which is parsed from field `data`. Otherwise,
   *     returns exceptions described in field `error`
   */
  public abstract Future<Object> invoke(
      Class service, String operation, Map<String, Object> variables);

  /**
   * Call an operation Just like `invoke` but this method automatically parse and map the output
   * data
   *
   * @param service Service
   * @param operation Operation name
   * @param variables A map of names and values for variables
   * @param dataClass Data class to map to
   * @return If everything's ok, returns an instance of Data class. Otherwise, returns exceptions
   *     described in field `error`
   */
  public abstract <T> Future<T> call(
      Class service, String operation, Map<String, Object> variables, Class<T> dataClass);

  public Future<Object> invokeDefault(Class service, String operation) {
    return invokeDefault(service, operation, null);
  }

  public Future<Object> invokeDefault(Class service, String operation, Object request) {
    Map<String, Object> namedParams = new HashMap<>();
    if (request != null) namedParams.put("request", request);
    return invoke(service, operation, namedParams);
  }

  public <T> Future<T> callDefault(Class service, String operation, Class<T> dataClass) {
    return callDefault(service, operation, null);
  }

  public <T> Future<T> callDefault(
      Class service, String operation, Object request, Class<T> dataClass) {
    Map<String, Object> namedParams = new HashMap<>();
    if (request != null) namedParams.put("request", request);
    return call(service, operation, namedParams, dataClass);
  }

  /**
   * Create a default builder
   *
   * @return Our current implementation
   */
  public static VertxGraphqlClient.VertxGraphqlClientBuilder newBuilder() {
    return VertxGraphqlClient.builder();
  }
}
