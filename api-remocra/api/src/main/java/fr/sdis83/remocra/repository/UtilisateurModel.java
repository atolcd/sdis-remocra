package fr.sdis83.remocra.repository;

import org.immutables.value.Value;
import javax.annotation.Nullable;

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
