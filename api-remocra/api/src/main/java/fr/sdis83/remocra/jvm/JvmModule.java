package fr.sdis83.remocra.jvm;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import java.util.TimeZone;
import org.immutables.value.Value;

@Value.Enclosing
public class JvmModule extends AbstractModule {

  @Value.Immutable
  interface Settings {
    TimeZone timeZone();
  }

  public static JvmModule create(Config config) {
    return new JvmModule(
        ImmutableJvmModule.Settings.builder()
            .timeZone(TimeZone.getTimeZone(config.getString("timeZone")))
            .build());
  }

  private final Settings settings;

  public JvmModule(Settings settings) {
    this.settings = settings;
  }

  @Override
  protected void configure() {
    bind(Settings.class).toInstance(settings);
  }
}
