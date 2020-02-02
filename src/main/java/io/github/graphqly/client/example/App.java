package io.github.graphqly.client.example;

import io.github.graphqly.client.GraphqlClient;
import io.github.graphqly.client.example.service.ManifestEchoRequest;
import io.github.graphqly.client.example.service.ManifestEchoResponse;
import io.github.graphqly.client.example.service.ManifestService;
import io.vertx.core.Future;

import java.io.IOException;

public class App {
  static void run() throws IOException {
    GraphqlClient client =
        GraphqlClient.newBuilder()
            // Optionally set the endpoint
            // Default: http://localhost:4000/graphql
            .endpoint("http://localhost:4000/graphql")

            // Optionally set keep-alive for our http connection
            // Default: true
            .keepAlive(true)

            // Optionally set if we need to use SSL
            // Default: false
            .useSSL(false)

            // Optionally set User-Agent string
            // Default: graphqly/0.1.0
            .userAgent("demo")
            .build();

    Future<ManifestEchoResponse> response =
        client.callDefault(
            ManifestService.class,
            "manifestEcho",
            ManifestEchoRequest.of("Andy"),
            ManifestEchoResponse.class);

    response.setHandler(
        ar -> {
          if (ar.succeeded()) {
            System.out.println(ar.result().message);
          }
        });
    System.out.println("Press any key to continue...");
    System.in.read();
  }

  public static void main(String[] args) throws IOException {
    run();
  }
}
