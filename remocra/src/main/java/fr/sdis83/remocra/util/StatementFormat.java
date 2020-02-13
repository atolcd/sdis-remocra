package fr.sdis83.remocra.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class StatementFormat {

  public static void PreparedStatement(PreparedStatement ps, int index , HashMap parameterObj) throws ParseException, SQLException {

    if (parameterObj.get("type").toString().equalsIgnoreCase("Byte")) {
      ps.setInt(index, (Byte.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Character varying")) {

      ps.setString(index,parameterObj.get("valeur").toString());
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Double precision")) {
      ps.setDouble(index, (Double.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Integer")) {
      ps.setInt(index, (Integer.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Long")) {
      ps.setLong(index, (Long.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("UUid")) {
      ps.setLong(index, (Long.valueOf(parameterObj.get("valeur").toString())));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Float")) {
      ps.setFloat(index, ((Float.valueOf(parameterObj.get("valeur").toString()))));
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Date")) {
      DateFormat format = new SimpleDateFormat("yyyy dd mm");
      ps.setObject(index, parameterObj.get("valeur").toString());
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Time")) {
      ps.setString(index, parameterObj.get("valeur").toString());
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Timestamp")) {
      ps.setString(index,parameterObj.get("valeur").toString());
    } else if (parameterObj.get("type").toString().equalsIgnoreCase("Boolean")) {
      //boolean x = parameterObj.get("valeur").toString().equalsIgnoreCase("true") ? true : false;
      ps.setObject(index, parameterObj.get("valeur"), Types.BOOLEAN);
    } else {
      ps.setObject(index,parameterObj.get("valeur"));
    }
  }
}
