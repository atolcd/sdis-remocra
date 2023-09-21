package fr.sdis83.remocra.domain.utils;

import java.text.SimpleDateFormat;

public class RemocraDateFormat extends SimpleDateFormat {

  private static final long serialVersionUID = 1L;

  public static final String FORMAT = "dd/MM/yyyy";

  public RemocraDateFormat() {
    super(FORMAT);
  }
}
