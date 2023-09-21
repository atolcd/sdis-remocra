package fr.sdis83.remocra.ogc.cql;

/** Statement réel : attribut / opérateur / valeur */
public class ObjectStatement extends Statement {
  String attribute;
  Operator operator;
  String value;

  public ObjectStatement(String attribute, Operator operator) {
    this(attribute, operator, "");
  }

  public ObjectStatement(String attribute, Operator operator, String value) {
    this.attribute = attribute;
    this.operator = operator;
    this.value = value;
  }

  public String getAttribute() {
    return this.attribute;
  }

  public Operator getOperator() {
    return this.operator;
  }

  public String getValue() {
    return this.value;
  }

  public String toCQL() {
    return attribute + " " + operator + (value == null || value.length() < 1 ? "" : " " + value);
  }
}
