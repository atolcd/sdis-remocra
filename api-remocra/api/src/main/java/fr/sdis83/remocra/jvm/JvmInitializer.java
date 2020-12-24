package fr.sdis83.remocra.jvm;

import javax.inject.Inject;
import java.util.TimeZone;

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
