package fr.sdis83.remocra.authn;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface UserPrincipal extends Principal {

  Long getUserId();

  UserInfo getUserInfo();

  List<UserRoles> roles();

  String type();

  Long typeId();
}