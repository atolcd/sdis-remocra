package fr.sdis83.remocra.db;

import com.adamlewis.guice.persist.jooq.JooqPersistModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.SQLDialect;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class DatabaseModule extends AbstractModule {

public static DatabaseModule create(Config config) throws IOException {
        return new DatabaseModule(SQLDialect.valueOf(config.getString("sql-dialect").toUpperCase(Locale.US)),
                                  toProperties(config.withoutPath("sql-dialect")));
}

private final SQLDialect sqlDialect;
private final Properties properties = new Properties();

public DatabaseModule(SQLDialect sqlDialect, Properties properties) {
        this.sqlDialect = sqlDialect;
        this.properties.putAll(properties);
}

@Override
protected void configure() {
        install(new JooqPersistModule());
        bind(DataSource.class).to(HikariDataSource.class);
        bind(SQLDialect.class).toInstance(this.sqlDialect);
}

@Provides
@Singleton
HikariDataSource provideHikariDataSource() {
        return new HikariDataSource(new HikariConfig(properties));
}

private static Properties toProperties(Config config) {
        Properties properties = new Properties();
        for (Map.Entry<String, ConfigValue> entry : config.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().unwrapped().toString());
        }
        return properties;
}
}
