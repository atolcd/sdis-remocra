package fr.sdis83.remocra.db.converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTWriter;
import fr.sdis83.remocra.GlobalConstants;
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

/** Binding Jooq pour les géométries. */
public abstract class AbstractGeometryBinding<T extends Geometry> implements Binding<Object, T> {

  private final WKBReader reader;
  private final WKTWriter writer;

  private final Class<T> geometryClass;

  /** Constructeur par défaut. */
  public AbstractGeometryBinding(Class<T> geometryClass) {
    // TODO: inject this
    reader = new WKBReader();
    writer = new WKTWriter();
    this.geometryClass = geometryClass;
  }

  protected abstract T cast(Geometry geometry);

  // The converter does all the work
  @Override
  public Converter<Object, T> converter() {
    return new Converter<Object, T>() {

      @Override
      public T from(Object object) {
        try {
          if (object == null) {
            return null;
          }
          Geometry geometry = reader.read(WKBReader.hexToBytes((String) object));
          return cast(geometry);
        } catch (ParseException exception) {
          throw new IllegalArgumentException("Not a valid Geometry", exception);
        }
      }

      @Override
      public Object to(Geometry geometry) {
        return geometry == null ? null : (writer.write(geometry));
      }

      @Override
      public Class<Object> fromType() {
        return Object.class;
      }

      @Override
      public Class<T> toType() {
        return geometryClass;
      }
    };
  }

  // Rending a bind variable for the binding context's value and casting it to the Geometry type
  @Override
  public void sql(BindingSQLContext<T> ctx) throws SQLException {
    Object value = ctx.convert(converter()).value();
    if (value == null) {
      ctx.render().visit(DSL.val(value)).sql("::geometry");
      return;
    }
    ctx.render()
        .sql("ST_GeomFromText(")
        .visit(DSL.val(value))
        .sql("," + GlobalConstants.SRID_PARAM + ")");
  }

  // Registering BLOB types for JDBC CallableStatement OUT parameters
  @Override
  public void register(BindingRegisterContext<T> ctx) throws SQLException {
    ctx.statement().registerOutParameter(ctx.index(), Types.BLOB);
  }

  // Converting the Geometry to a byte[] value and setting that on a JDBC PreparedStatement
  @Override
  public void set(BindingSetStatementContext<T> ctx) throws SQLException {
    ctx.statement().setString(ctx.index(), (String) ctx.convert(converter()).value());
  }

  // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
  @Override
  public void set(BindingSetSQLOutputContext<T> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  // Getting a String value from a JDBC ResultSet and converting that to a Geometry
  @Override
  public void get(BindingGetResultSetContext<T> ctx) throws SQLException {
    ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
  }

  // Getting a String value from a JDBC CallableStatement and converting that to a Geometry
  @Override
  public void get(BindingGetStatementContext<T> ctx) throws SQLException {
    ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
  }

  // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
  @Override
  public void get(BindingGetSQLInputContext<T> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
}
