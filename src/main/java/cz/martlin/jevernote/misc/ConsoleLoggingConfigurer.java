package cz.martlin.jevernote.misc;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class ConsoleLoggingConfigurer {

	private ConsoleLoggingConfigurer() {
	}

	public static void setTo(boolean verbose, boolean debug) {

		Level level = inferLevel(verbose, debug);

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.setLevel(level);
		ctx.updateLoggers();

	}

	private static Level inferLevel(boolean verbose, boolean debug) {
		if (debug) {
			return Level.DEBUG;
		}

		if (verbose) {
			return Level.INFO;
		}

		return Level.WARN;
	}

}
