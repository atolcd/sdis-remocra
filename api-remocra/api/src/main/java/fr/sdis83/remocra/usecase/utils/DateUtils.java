package fr.sdis83.remocra.usecase.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe utilitaire pour tous les objets "date" On passe par une représentation en ZoneDateTime
 * pour gérer la différence de fuseau, en BDD on est sur de l'UTC mais sur le front et les appels,
 * on veut l'heure en GMT+1
 */
public class DateUtils {
  /** Pattern attendu pour les chaînes représentatives d'une date (moment) */
  public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static final String PATTERN_NATUREL = "dd/MM/yyyy HH:mm:ss";

  /**
   * Retourne un moment (ZoneDateTime) à partir d'une chaîne de date à l'heure "naturelle"
   *
   * @param dateString String
   * @return ZonedDateTime
   * @throws DateTimeParseException si le format PATTERN n'est pas respecté
   */
  public static ZonedDateTime getMoment(String dateString) throws DateTimeParseException {
    return ZonedDateTime.parse(
        dateString, DateTimeFormatter.ofPattern(PATTERN).withZone(ZoneId.systemDefault()));
  }

  public static String format(ZonedDateTime date) {
    return format(date, PATTERN);
  }

  public static String format(Instant instant) {
    return instant.atZone(ZoneId.systemDefault()).format(getFormatter());
  }

  public static String formatNaturel(Instant instant) {
    return instant.atZone(ZoneId.systemDefault()).format(getFormatter(PATTERN_NATUREL));
  }

  private static DateTimeFormatter getFormatter() {
    return DateTimeFormatter.ofPattern(PATTERN);
  }

  private static DateTimeFormatter getFormatter(String pattern) {
    return DateTimeFormatter.ofPattern(pattern);
  }

  public static String format(ZonedDateTime date, String pattern) {
    return date.format(DateTimeFormatter.ofPattern(pattern));
  }
}
