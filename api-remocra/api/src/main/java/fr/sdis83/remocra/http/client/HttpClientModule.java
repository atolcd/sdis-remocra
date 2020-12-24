package fr.sdis83.remocra.http.client;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.immutables.value.Value;

@Value.Enclosing
public class HttpClientModule extends AbstractModule {

@Value.Immutable
public interface Settings {
int timeout();

boolean useSystemProperties();
}

public static HttpClientModule create(Config config) {
        return new HttpClientModule(ImmutableHttpClientModule.Settings.builder()
                                    .timeout(config.getInt("timeout"))
                                    .useSystemProperties(config.getBoolean("useSystemProperties"))
                                    .build());
}

private final Settings settings;
private final HttpClientBuilder clientBuilder;

public HttpClientModule(Settings settings) {
        this.settings = settings;

        HttpClientBuilder builder = HttpClientBuilder.create();
        if (this.settings.useSystemProperties()) {
                builder.useSystemProperties();
        }
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(this.settings.timeout());
        requestBuilder = requestBuilder.setConnectionRequestTimeout(this.settings.timeout());
        builder.setDefaultRequestConfig(requestBuilder.build());
        this.clientBuilder = builder;
}

@Override
protected void configure() {
        bind(Settings.class).toInstance(settings);
        bind(HttpClientBuilder.class).toInstance(this.clientBuilder);
}
}
