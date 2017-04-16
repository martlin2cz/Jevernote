package cz.martlin.jevernote.app;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.core.JevernoteCore;
import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.misc.ConsoleLoggingConfigurer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.content.impls.ReadOnlyStorage;
import cz.martlin.jevernote.storage.impls.FSSWIUsingProperties;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;

public class CommandsRunner implements RequiresLoad {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private boolean loaded;
	private final JevernoteCore core;

	public CommandsRunner(File basePath, boolean verbose, boolean debug, boolean dryRun, boolean interactive) {
		super();
		// TODO dry run
		// TODO interactive

		ConsoleLoggingConfigurer.setTo(verbose, debug);

		Config config = new Config();
		BaseStorage local = createLocal(config, basePath, dryRun);
		BaseStorage remote = createRemote(config, basePath, dryRun);

		this.core = new JevernoteCore(local, remote);
	}

	private BaseStorage createLocal(Config config, File basePath, boolean dryRun) {
		BaseStorage storage = new FSSWIUsingProperties(config, basePath);
		return tryMakeDry(storage, dryRun);
	}

	private BaseStorage createRemote(Config config, File basePath, boolean dryRun) {
		BaseStorage storage = new InMemoryStorage();
		return tryMakeDry(storage, dryRun);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void load() {
		try {
			core.load();
			loaded = true;
		} catch (JevernoteException e) {
			LOG.error("Cannot load", e);
		}
	}

	@Override
	public void store() {
		try {
			core.store();
			loaded = false;
		} catch (JevernoteException e) {
			LOG.error("Cannot store", e);
		}
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	///////////////////////////////////////////////////////////////////////////

	public boolean cmdInit(String remoteToken) {
		try {
			core.initCmd(null, remoteToken);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command init failed", e);
			return false;
		}
	}

	public boolean cmdClone(String remoteToken) {
		try {
			core.cloneCmd(null, remoteToken);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command clone failed", e);
			return false;
		}
	}

	public boolean cmdPush(boolean weak, boolean force) {
		try {
			core.pushCmd(weak, force);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command push failed", e);
			return false;
		}
	}

	public boolean cmdPull(boolean weak, boolean force) {
		try {
			core.pullCmd(weak, force);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command pull failed", e);
			return false;
		}
	}

	public boolean cmdSynchronize(boolean preferLocal) {
		try {
			core.synchronizeCmd(preferLocal);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command synchronize failed", e);
			return false;
		}
	}

	public boolean cmdStatus() {
		try {
			core.statusCmd();
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command status failed", e);
			return false;
		}
	}

	///////////////////////////////////////////////////////////////////////////

	// TODO mv, ad, rm, ...

	///////////////////////////////////////////////////////////////////////////

	private BaseStorage tryMakeDry(BaseStorage storage, boolean dryRun) {
		if (dryRun) {
			return new ReadOnlyStorage(storage);
		} else {
			return storage;
		}
	}

}
