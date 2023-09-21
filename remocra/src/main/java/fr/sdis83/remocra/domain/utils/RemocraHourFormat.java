package fr.sdis83.remocra.domain.utils;

import java.text.SimpleDateFormat;

public class RemocraHourFormat extends SimpleDateFormat {

  private static final long serialVersionUID = 1L;

  public static final String FORMAT = "hh'h'mm";

  public RemocraHourFormat() {
    super(FORMAT);
  }
}
