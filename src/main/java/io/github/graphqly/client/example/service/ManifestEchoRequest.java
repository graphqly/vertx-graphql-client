package io.github.graphqly.client.example.service;

public class ManifestEchoRequest {
  public String name;

  public static ManifestEchoRequest of(String name) {
    ManifestEchoRequest request = new ManifestEchoRequest();
    request.name = name;
    return request;
  }
}
