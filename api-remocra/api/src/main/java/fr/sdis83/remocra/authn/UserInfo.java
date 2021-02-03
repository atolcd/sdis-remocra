package fr.sdis83.remocra.authn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.UUID;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableUserInfo.class)
@JsonDeserialize(as = ImmutableUserInfo.class)
public interface UserInfo {

  @Value.Parameter
  Long userId();

  @Value.Parameter
  String username();

  @Value.Parameter
  List<UserRoles> roles();

  @Value.Parameter
  String type();

  @Value.Parameter
  Long typeId();
}