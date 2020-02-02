package io.github.graphqly.client.example.service;


import io.github.graphqly.reflector.annotations.GraphQLArgument;
import io.github.graphqly.reflector.annotations.GraphQLQuery;

public abstract class ManifestService {
  @GraphQLQuery
  public abstract ManifestEchoResponse manifestEcho(
      @GraphQLArgument(name = "request") ManifestEchoRequest request);
}
