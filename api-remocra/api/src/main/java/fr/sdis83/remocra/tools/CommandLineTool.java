package fr.sdis83.remocra.tools;

import com.typesafe.config.Config;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CommandLineTool {

@Option(name = "-c", usage = "Configuration file", metaVar = "file")
private Path configurationPath;

@Option(name = "-l", usage = "Log4j configuration file", metaVar = "file")
private Path log4jConfig;

protected abstract Logger logger();

protected Config init(String[] args) throws IOException {
        parseArgs(args);

        if (log4jConfig != null && Files.isRegularFile(log4jConfig) && Files.isReadable(log4jConfig)) {
                System.setProperty("log4j.configurationFile", log4jConfig.toRealPath().toString());
        } else {
                // use default log4j configuration: all INFO and more to stdout
                System.setProperty("org.apache.logging.log4j.level",
                                   System.getProperty("org.apache.logging.log4j.level", "INFO"));

                if (log4jConfig != null) {
                        if (!Files.isRegularFile(log4jConfig) || !Files.isReadable(log4jConfig)) {
                                logger().warn("log4j2 configuration file not found or not readable. Using default configuration.");
                        }
                } else {
                        logger().debug("No log4j2 configuration file specified. Using default configuration.");
                }
        }

        return SettingsLoader.load(configurationPath);
}

// @SuppressWarnings("DM_EXIT")
private void parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
                parser.parseArgument(args);
        } catch (CmdLineException e) {
                printUsage(e);
                System.exit(1);
        }
}

// @SuppressWarnings("PMD.SystemPrintln")
protected void printUsage(CmdLineException e) {
        // TODO: detailed usage description
        System.err.println(e.getMessage());
        e.getParser().printUsage(System.err);
}
}
