package fr.sdis83.remocra.ogc.cql;

import java.util.Collection;
import java.util.LinkedList;

/** Statement Composite avec conjunction AND par défaut */
public class CompositeStatement extends Statement {
  Collection<Statement> statements;
  Conjunction conjunction;

  public CompositeStatement() {
    this(Conjunction.AND);
  }

  public CompositeStatement(Conjunction conjunction) {
    this(conjunction, new LinkedList<Statement>());
    this.conjunction = conjunction;
  }

  public CompositeStatement(Conjunction conjunction, Collection<Statement> statements) {
    this.statements = statements;
    this.conjunction = conjunction;
  }

  public Collection<Statement> getStatements() {
    return this.statements;
  }

  public Conjunction getConjunction() {
    return this.conjunction;
  }

  public void addStatement(Statement statement) {
    statements.add(statement);
  }

  public String toCQL() {
    if (statements.size() == 1) {
      return statements.iterator().next().toCQL();
    }
    StringBuffer sb = new StringBuffer("(");
    for (Statement s : this.statements) {
      if (sb.length() > 1) {
        sb.append(" ").append(conjunction).append(" ");
      }
      sb.append(s.toCQL());
    }
    sb.append(")");
    return sb.toString();
  }
}
