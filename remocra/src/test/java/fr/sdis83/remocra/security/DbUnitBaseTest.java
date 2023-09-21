package fr.sdis83.remocra.security;

import fr.sdis83.remocra.utils.RemocraH2DataTypeFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/testContextInMemory.xml"})
public abstract class DbUnitBaseTest {

  @Autowired private DataSource dataSource;

  private IDataSet dataset;

  @Before
  public void initEntityManager() throws Exception {

    // load dataset
    ClassLoader classLoader = this.getClass().getClassLoader();
    URL datasetUrl = classLoader.getResource(getDataSetPath());
    dataset = new FlatXmlDataSetBuilder().build(datasetUrl);

    URL deleteDatasetUrl = classLoader.getResource(getDeleteDataSetPath());
    IDataSet deleteDataSet = new FlatXmlDataSetBuilder().build(deleteDatasetUrl);
    // insert data
    Connection con = DataSourceUtils.getConnection(dataSource);

    IDatabaseConnection databaseConnection = new DatabaseConnection(con);

    // http://dbunit.sourceforge.net/properties.html
    databaseConnection
        .getConfig()
        .setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new RemocraH2DataTypeFactory());
    // databaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES,
    // Boolean.TRUE);
    databaseConnection
        .getConfig()
        .setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, Boolean.TRUE);

    DatabaseOperation.DELETE_ALL.execute(databaseConnection, deleteDataSet);
    DatabaseOperation.INSERT.execute(databaseConnection, dataset);
  }

  public abstract String getDataSetPath();

  public abstract String getDeleteDataSetPath();

  @After
  public void cleanupEntityManager() throws DatabaseUnitException, SQLException, IOException {

    ClassLoader classLoader = this.getClass().getClassLoader();
    URL datasetUrl = classLoader.getResource(getDeleteDataSetPath());
    IDataSet deleteDataSet = new FlatXmlDataSetBuilder().build(datasetUrl);

    // Delete all data after tests
    Connection con = DataSourceUtils.getConnection(dataSource);
    IDatabaseConnection databaseConnection = new DatabaseConnection(con);
    databaseConnection
        .getConfig()
        .setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new RemocraH2DataTypeFactory());
    // databaseConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES,
    // Boolean.TRUE);
    databaseConnection
        .getConfig()
        .setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, Boolean.TRUE);
    DatabaseOperation.DELETE_ALL.execute(databaseConnection, deleteDataSet);
  }
}
