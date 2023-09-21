package fr.sdis83.remocra.http;

public interface HttpServer {
  void start() throws Exception;

  void waitTillInterrupt() throws InterruptedException;

  void stop() throws Exception;
}
