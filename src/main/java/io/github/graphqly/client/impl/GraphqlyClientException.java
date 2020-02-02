package io.github.graphqly.client.impl;

public class GraphqlyClientException extends Throwable {
  public final Object reason;
  public final String message;

  public GraphqlyClientException(Object reason) {
    this("", reason);
  }

  public GraphqlyClientException(String message, Object reason) {
    this.message = message;
    this.reason = reason;
  }
}
