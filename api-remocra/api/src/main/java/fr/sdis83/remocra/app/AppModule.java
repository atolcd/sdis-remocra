package fr.sdis83.remocra.app;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import fr.sdis83.remocra.web.Application;
import org.immutables.value.Value;

@Value.Enclosing
public class AppModule extends AbstractModule {

@Value.Immutable
interface Settings {
String title();

String version();
}

public static AppModule create(Config config) {
        return new AppModule(ImmutableAppModule.Settings.builder().title(config.getString("title"))
                             .version(config.getString("version")).build());
}

private final Settings settings;

public AppModule(Settings settings) {
        this.settings = settings;
}

@Override
protected void configure() {
        bind(javax.ws.rs.core.Application.class).to(Application.class);

        bind(String.class).annotatedWith(ApplicationTitle.class).toInstance(settings.title());
        bind(String.class).annotatedWith(ApplicationVersion.class).toInstance(settings.version());
}
}
