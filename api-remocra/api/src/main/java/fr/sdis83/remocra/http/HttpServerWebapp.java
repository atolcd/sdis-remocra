package fr.sdis83.remocra.http;

import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.ERROR;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.INCLUDE;
import static javax.servlet.DispatcherType.REQUEST;
import static org.eclipse.jetty.servlet.ServletContextHandler.SECURITY;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.GuiceFilter;
import fr.sdis83.remocra.resteasy.GuiceInjectorFactory;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.Filter30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class HttpServerWebapp implements HttpServer {
  // private static final Logger logger =
  // LoggerFactory.getLogger(HttpServerWebapp.class);

  private final GuiceFilter guiceFilter;
  private final GuiceInjectorFactory guiceInjectorFactory;
  private final HttpServerWebappModule.Settings settings;
  private final Application application;
  private final PersistFilter persistFilter;

  private Server server;

  @Inject
  HttpServerWebapp(
      GuiceInjectorFactory guiceInjectorFactory,
      GuiceFilter guiceFilter,
      HttpServerWebappModule.Settings settings,
      Application application,
      PersistFilter persistFilter) {
    this.guiceInjectorFactory = guiceInjectorFactory;
    this.guiceFilter = guiceFilter;
    this.settings = settings;
    this.application = application;
    this.persistFilter = persistFilter;
  }

  @Override
  public void start() throws Exception {
    server = new Server();
    server.setStopAtShutdown(true);
    server.setStopTimeout(settings.gracefulStopTime());

    // Connector, avec support pour X-Forwarded-Proto et al.
    final ServerConnector httpConnector = new ServerConnector(server);
    server.addConnector(httpConnector);
    httpConnector.setPort(settings.port());
    final HttpConfiguration httpConfiguration = new HttpConfiguration();
    httpConfiguration.addCustomizer(new ForwardedRequestCustomizer());
    httpConfiguration.setSendServerVersion(false);
    httpConfiguration.setSendXPoweredBy(false);
    httpConnector.addConnectionFactory(new HttpConnectionFactory(httpConfiguration));

    // Servlets
    final ServletContextHandler context = new ServletContextHandler(SESSIONS | SECURITY);
    final File tmpdir = Files.createTempDirectory("jetty").toFile();
    tmpdir.deleteOnExit();
    context.setAttribute(ServletContext.TEMPDIR, tmpdir);
    server.setHandler(context);

    // Configure DefaultServlet cache
    for (Map.Entry<String, String> entry : settings.defaultServletConfig().entrySet()) {
      context.setInitParameter(
          "org.eclipse.jetty.servlet.Default." + entry.getKey(), entry.getValue());
    }

    // Servlets + Guice
    final FilterHolder guiceFilterHolder = new FilterHolder(guiceFilter);
    context.addFilter(guiceFilterHolder, "/*", null);

    // Servlets + Persist
    final FilterHolder persistFilterHolder = new FilterHolder(persistFilter);
    context.addFilter(persistFilterHolder, "/*", null);

    // Servlets + RequestAddrHostFilter for documentation
    /*final FilterHolder requestIPFilterHolder = new FilterHolder(new RequestAddrHostFilter(settings.documentationAuthorizedIp()));
    context.addFilter(requestIPFilterHolder, "/documentation/*", null);*/

    // Resteasy
    context.setInitParameter("resteasy.logger.type", "SLF4J");
    final FilterHolder resteasy = new FilterHolder(Filter30Dispatcher.class);
    resteasy.setAsyncSupported(true);
    context.addFilter(resteasy, "/*", EnumSet.of(REQUEST, ASYNC, ERROR, FORWARD, INCLUDE));

    // Resteasy + Guice
    final ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
    providerFactory.setInjectorFactory(guiceInjectorFactory);
    final ResteasyDeployment deployment = new ResteasyDeployment();
    deployment.setProviderFactory(providerFactory);
    deployment.setApplication(application);
    context.setAttribute(ResteasyDeployment.class.getName(), deployment);

    String clientPath = settings.clientPath();
    ServletHolder resources = new ServletHolder("client", DefaultServlet.class);
    resources.setInitParameter("resourceBase", clientPath);
    resources.setInitParameter("dirAllowed", "false");
    context.addServlet(resources, "/");

    // Servlet pour la documentation de l'API
    ServletHolder doc = new ServletHolder("doc", DefaultServlet.class);
    doc.setInitParameter("resourceBase", settings.docPath());
    doc.setInitParameter("dirAllowed", "false");
    context.addServlet(doc, "/documentation/*");

    server.start();
  }

  @Override
  public void waitTillInterrupt() throws InterruptedException {
    server.join();
  }

  @Override
  public void stop() throws Exception {
    server.stop();
  }
}
