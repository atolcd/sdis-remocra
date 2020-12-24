package fr.sdis83.remocra;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.servlet.ServletModule;
import com.typesafe.config.Config;
import fr.sdis83.remocra.app.AppModule;
import fr.sdis83.remocra.db.DatabaseModule;
import fr.sdis83.remocra.http.HttpServer;
import fr.sdis83.remocra.http.HttpServerWebappModule;
import fr.sdis83.remocra.http.client.HttpClientModule;

import fr.sdis83.remocra.jvm.JvmInitializer;
import fr.sdis83.remocra.jvm.JvmModule;
import fr.sdis83.remocra.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class WebApp extends CommandLineTool {

// logger is not a static field to be initialized once log4j is configured
@Override
protected Logger logger() {
        return LoggerFactory.getLogger(WebApp.class);
}

@Inject
HttpServer httpServer;

@Inject
JvmInitializer jvmInitializer;

@Inject
PersistService persistService;


public static void main(String[] args) throws Throwable {
        new WebApp().run(args);
}

public void run(String[] args) throws Throwable {
        final Config config = init(args);

        // Identification des modules à initialiser en fonction de la conf
        final Injector injector = Guice.createInjector(getModules(config));
        injector.injectMembers(this);

        jvmInitializer.initialize();

        Runtime.getRuntime()
            .addShutdownHook(new Thread(() -> {
                    try {
                            httpServer.stop();
                    } catch (Exception exception) {
                            logger().error("Error stopping HttpServer", exception);
                    }
                    persistService.stop();
            }));
        persistService.start();
        httpServer.start();
        httpServer.waitTillInterrupt();
}

private List<AbstractModule> getModules(final Config config) throws Throwable {
        List<AbstractModule> res = new ArrayList<>();

        // Common modules
        res.add(new ServletModule());
        res.add(DatabaseModule.create(config.getConfig("api-remocra.database")));
        res.add(JvmModule.create(config.getConfig("api-remocra.jvm")));
        res.add(AppModule.create(config.getConfig("api-remocra.app")));
        res.add(HttpServerWebappModule.create(config.getConfig("api-remocra.http")));
        res.add(HttpClientModule.create(config.getConfig("api-remocra.http-client")));
        return res;
}

}
