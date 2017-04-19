package cz.martlin.jevernote.app;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.misc.CommandLineData;
import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.misc.ConsoleLoggingConfigurer;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.content.base.ContentProcessor;
import cz.martlin.jevernote.storage.content.impls.EvernoteStrippingNewliningProcessor;
import cz.martlin.jevernote.storage.impls.EvernoteStorage;
import cz.martlin.jevernote.storage.impls.FSSWIUsingProperties;

/**
 * Runs command upon the command line arguments.
 * 
 * @author martin
 *
 */
public class ConsoleDataProcessor {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public ConsoleDataProcessor() {
	}

	/**
	 * Creates local storage to be used.
	 * 
	 * @param config
	 * @param basePath
	 * @return
	 */
	private BaseStorage createLocal(Config config, File basePath) {
		BaseStorage storage = new FSSWIUsingProperties(config, basePath);

		return storage;
	}

	/**
	 * Create remote storage to be used.
	 * 
	 * @param config
	 * @param basePath
	 * @return
	 */
	private BaseStorage createRemote(Config config, File basePath) {
		ContentProcessor proces = new EvernoteStrippingNewliningProcessor();
		BaseStorage storage = new EvernoteStorage(config, basePath, proces);

		return storage;
	}

	/**
	 * Runs command (with particullar params) specified by given command line
	 * data.
	 * 
	 * @param data
	 * @return true if success
	 */
	public boolean process(CommandLineData data) {
		ConsoleLoggingConfigurer.setTo(data.isVerbose(), data.isDebug());

		File basePath = basePath(data);
		Config config = new Config();
		BaseStorage local = createLocal(config, basePath);
		BaseStorage remote = createRemote(config, basePath);

		SyncCommandsRunner syncs = new SyncCommandsRunner(local, remote, basePath, data.isDryRun(),
				data.isInteractive(), data.isSave());

		LocalCommandsRunner locals = new LocalCommandsRunner(local);

		syncs.load();
		locals.load();
		if (!(syncs.isLoaded() && locals.isLoaded())) {
			return false;
		}

		boolean success = process(syncs, locals, data);

		syncs.store();
		locals.store();
		if (!(syncs.isLoaded() && locals.isLoaded())) {
			return false;
		}

		return success;

	}

	/**
	 * Invokes particullar method of one of given runners depending on command.
	 * 
	 * @param syncs
	 * @param locals
	 * @param data
	 * @return
	 */
	private boolean process(SyncCommandsRunner syncs, LocalCommandsRunner locals, CommandLineData data) {
		switch (data.getCommand()) {
		case "init":
			return syncs.cmdInit(data.getRemoteToken());

		case "clone":
			return syncs.cmdClone(data.getRemoteToken());

		case "push":
			return syncs.cmdPush(data.isWeak(), data.isForce());

		case "pull":
			return syncs.cmdPull(data.isWeak(), data.isForce());

		case "synchronize":
			return syncs.cmdSynchronize();

		case "status":
			return syncs.cmdStatus();

		case "ad":
			return locals.adCmd(data.getFirstItemOrPack());
		case "mk":
			return locals.mkCmd(data.getFirstItemOrPack(), data.getInitialText());
		case "mv":
			return locals.mvCmd(data.getFirstItemOrPack(), data.getSecondItemOrPack());
		case "rm":
			return locals.rmCmd(data.getFirstItemOrPack());
		default:
			LOG.error("Unknown command " + data.getCommand());
			return false;
		}
	}

	/**
	 * Returns base path. If base path is specified by data, returns its value,
	 * else returns current working directory.
	 * 
	 * @param data
	 * @return
	 */
	private File basePath(CommandLineData data) {
		if (data.getBaseDir() != null) {
			return data.getBaseDir();
		} else {
			String path = System.getProperty("user.dir");
			return new File(path);
		}

	}

}
