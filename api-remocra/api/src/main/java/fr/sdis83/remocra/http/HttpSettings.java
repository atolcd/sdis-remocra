package fr.sdis83.remocra.http;

import org.immutables.value.Value;

@Value.Immutable
public interface HttpSettings {
int port();

int gracefulStopTime();
}
