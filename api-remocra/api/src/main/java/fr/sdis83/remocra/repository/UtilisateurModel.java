package fr.sdis83.remocra.repository;

import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface UtilisateurModel {
  Long id();

  String identifiant();

  String password();

  @Nullable
  String salt();

  boolean actif();

  String email();
}
