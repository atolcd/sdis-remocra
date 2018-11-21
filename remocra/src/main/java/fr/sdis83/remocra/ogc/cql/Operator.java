package fr.sdis83.remocra.ogc.cql;

public enum Operator {
    NEQ("<>"), EQ("="), LT("<"), LTE("<="), GT(">"), GTE(">="), LIKE("LIKE"), ILIKE("ILIKE"),
    BETWEEN("BETWEEN")/*Fixer la value avec un AND*/, IS_NOT_NULL("IS NOT NULL"), IS_NULL("IS NULL"), IN("IN"), NOT("NOT"),
    AFTER("AFTER"), BEFORE("BEFORE");
    // TODO Ajouter les spatial filters ?
    // INTERSECTS, DISJOINT, CONTAINS, WITHIN, TOUCHES, CROSSES, EQUALS, DWITHIN, and  BBOX
    final String value;

    Operator(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}