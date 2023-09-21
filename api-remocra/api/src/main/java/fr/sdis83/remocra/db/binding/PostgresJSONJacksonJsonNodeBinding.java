package fr.sdis83.remocra.db.binding;

import com.fasterxml.jackson.databind.JsonNode;
import fr.sdis83.remocra.db.converter.PostgresJSONJacksonJsonNodeConverter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.DSL;

public class PostgresJSONJacksonJsonNodeBinding implements Binding<Object, JsonNode> {
  private static final long serialVersionUID = 1L;

  @Override
  public Converter<Object, JsonNode> converter() {
    return new PostgresJSONJacksonJsonNodeConverter();
  }

  @Override
  public void sql(BindingSQLContext<JsonNode> ctx) throws SQLException {

    // This ::jsonb cast is explicitly needed by PostgreSQL:
    ctx.render().visit(DSL.val(ctx.convert(converter()).value())).sql("::jsonb");
  }

  @Override
  public void register(BindingRegisterContext<JsonNode> ctx) throws SQLException {
    ctx.statement().registerOutParameter(ctx.index(), Types.OTHER);
  }

  @Override
  public void set(BindingSetStatementContext<JsonNode> ctx) throws SQLException {
    ctx.statement().setString(ctx.index(), (String) ctx.convert(converter()).value());
  }

  @Override
  public void get(BindingGetResultSetContext<JsonNode> ctx) throws SQLException {
    ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
  }

  @Override
  public void get(BindingGetStatementContext<JsonNode> ctx) throws SQLException {
    ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
  }

  // The below methods aren't needed in PostgreSQL:

  @Override
  public void set(BindingSetSQLOutputContext<JsonNode> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public void get(BindingGetSQLInputContext<JsonNode> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
}
