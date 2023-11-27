package fr.sdis83.remocra.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public class StatementFormat {

  public static void PreparedStatement(PreparedStatement ps, int index, Map parameterObj)
      throws ParseException, SQLException {
    String type = parameterObj.get("type").toString().toUpperCase();
    String valeur = parameterObj.get("valeur").toString();

    switch (type) {
      case "BYTE":
        ps.setInt(index, Byte.parseByte(valeur));
        break;
      case "TIME":
      case "TIMESTAMP":
      case "CHARACTER VARYING":
        ps.setString(index, valeur);
        break;
      case "DOUBLE PRECISION":
        ps.setDouble(index, Double.parseDouble(valeur));
        break;
      case "INTEGER":
        ps.setInt(index, Integer.parseInt(valeur));
        break;
      case "UUID":
      case "LONG":
        ps.setLong(index, Long.parseLong(valeur));
        break;
      case "FLOAT":
        ps.setFloat(index, Float.parseFloat(valeur));
        break;
      case "BOOLEAN":
        ps.setBoolean(index, Boolean.getBoolean(valeur));
        break;
      case "DATE":
      default:
        ps.setObject(index, valeur);
        break;
    }
  }
}
