package fr.sdis83.remocra.jvm;

import java.util.TimeZone;
import javax.inject.Inject;

public class JvmInitializer {
  private final JvmModule.Settings settings;

  @Inject
  public JvmInitializer(JvmModule.Settings settings) {
    this.settings = settings;
  }

  public void initialize() {
    TimeZone.setDefault(settings.timeZone());
  }
}
