package fr.sdis83.remocra.tools;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsLoader {
  private static final Logger logger = LoggerFactory.getLogger(SettingsLoader.class);

  public static Config load(Path path) {
    Config applicationConfig = ConfigFactory.empty();
    if (path != null) {
      if (Files.isRegularFile(path) && Files.isReadable(path)) {
        applicationConfig =
            ConfigFactory.parseFileAnySyntax(path.toFile())
                .withFallback(
                    ConfigFactory.parseMap(
                        Collections.singletonMap(
                            "application.conf-dir", path.getParent().toAbsolutePath().toString())));
      } else {
        logger.warn("Configuration file not found or not readable. Using default configuration.");
      }
    } else {
      logger.debug("No configuration file specified. Using default configuration.");
    }

    // Chargement sans overrides du fichier "const" pour éviter la surcharge
    // de constantes
    Config constConfig = ConfigFactory.parseResourcesAnySyntax("const.conf");

    // TODO: handle fallback manually, to fallback with a warning in case of
    // illegal values, instead of throwing
    return constConfig
        .withFallback(ConfigFactory.defaultOverrides())
        .withFallback(applicationConfig)
        .withFallback(ConfigFactory.defaultReference())
        .resolve();
  }
}
