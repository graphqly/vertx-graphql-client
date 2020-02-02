package io.github.graphqly.client.impl;

import io.github.graphqly.client.GraphqlyClient;
import io.github.graphqly.reflector.GraphQLReflector;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
public class VertxGraphqlyClient extends GraphqlyClient {

  @Setter @Getter @Default public boolean useSSL = false;
  @Setter @Getter @Default public boolean keepAlive = true;
  @Setter @Getter @Default public String endpoint = "http://localhost:4000/graphql";
  @Setter @Getter @Default public String userAgent = "graphqly/0.1.0";
  @Setter @Getter public Vertx vertx;

  public Future<Object> invoke(Class service, String operation, Map<String, Object> variables) {
    GraphQLReflector provider = GraphQLReflector.builder().rootType(service).build();
    String query = provider.inspectOperation(operation);

    WebClient client = getWebClient();
    HttpRequest<Buffer> request = createRequest(client);

    JsonObject body =
        new JsonObject()
            .put("query", query)
            .put("operationName", operation)
            .put("variables", variables);

    Promise<Object> promise = Promise.promise();
    request.sendJsonObject(
        body,
        ar -> {
          if (ar.failed()) {
            promise.fail(ar.cause());
            return;
          }

          JsonObject jsonResult = ar.result().bodyAsJsonObject();
          if (jsonResult.containsKey("data")) {
            promise.complete(jsonResult.getJsonObject("data").getJsonObject(operation));
          } else {
            promise.fail(new GraphqlyClientException(jsonResult.getJsonArray("errors")));
          }
        });

    return promise.future();
  }

  @Override
  public <T> Future<T> call(
      Class service, String operation, Map<String, Object> variables, Class<T> dataClass) {
    Promise<T> promise = Promise.promise();
    invoke(service, operation, variables)
        .setHandler(
            ar -> {
              if (ar.failed()) {
                promise.fail(ar.cause());
                return;
              }
              promise.complete(JsonObject.mapFrom(ar.result()).mapTo(dataClass));
            });

    return promise.future();
  }

  private HttpRequest<Buffer> createRequest(WebClient client) {
    HttpRequest<Buffer> request = client.postAbs(endpoint);
    request.ssl(useSSL);
    MultiMap headers = request.headers();
    headers.set("content-type", "application/json");
    headers.set("accept", "application/json");
    return request;
  }

  private WebClient getWebClient() {
    if (vertx == null) {
      synchronized (this) {
        if (vertx == null) {
          vertx = Vertx.vertx();
        }
      }
    }
    WebClientOptions options = new WebClientOptions().setUserAgent(userAgent);
    options.setKeepAlive(keepAlive);
    return WebClient.create(vertx, options);
  }
}
