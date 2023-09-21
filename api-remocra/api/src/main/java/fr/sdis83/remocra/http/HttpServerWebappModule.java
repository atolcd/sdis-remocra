package fr.sdis83.remocra.http;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.immutables.value.Value;

@Value.Enclosing
public class HttpServerWebappModule extends AbstractModule {

  @Value.Immutable
  interface Settings {
    int port();

    int gracefulStopTime();

    String docPath();

    Set<String> documentationAuthorizedIp();

    ImmutableMap<String, String> defaultServletConfig();

    String clientPath();
  }

  public static HttpServerWebappModule create(Config config) {
    return new HttpServerWebappModule(
        ImmutableHttpServerWebappModule.Settings.builder()
            .port(config.getInt("port"))
            .gracefulStopTime(config.getInt("graceful-stop-time"))
            .docPath(config.getString("doc-path"))
            .documentationAuthorizedIp(
                new HashSet<String>(config.getStringList("documentation-authorizedIp")))
            .defaultServletConfig(toMap(config.getConfig("default-servlet")))
            .clientPath(config.getString("client-path"))
            .build());
  }

  private final Settings settings;

  public HttpServerWebappModule(Settings settings) {
    this.settings = settings;
  }

  @Override
  protected void configure() {
    bind(HttpServer.class).to(HttpServerWebapp.class);
    bind(Settings.class).toInstance(settings);
  }

  private static Map<String, String> toMap(Config config) {
    Map<String, String> res = new HashMap<String, String>();
    for (Map.Entry<String, ConfigValue> entry : config.entrySet()) {
      res.put(entry.getKey(), entry.getValue().unwrapped().toString());
    }
    return res;
  }
}
