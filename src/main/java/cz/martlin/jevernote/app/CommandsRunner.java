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
import cz.martlin.jevernote.storage.content.base.ContentProcessor;
import cz.martlin.jevernote.storage.content.impls.EvernoteStrippingNewliningProcessor;
import cz.martlin.jevernote.storage.impls.EvernoteStorage;
import cz.martlin.jevernote.storage.impls.FSSWIUsingProperties;

public class CommandsRunner implements RequiresLoad<String> {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private boolean loaded;
	private final JevernoteCore core;

	public CommandsRunner(File basePath, boolean verbose, boolean debug, boolean dryRun, boolean interactive,
			boolean save) {
		super();

		ConsoleLoggingConfigurer.setTo(verbose, debug);

		Config config = new Config();
		
		BaseStorage local = createLocal(config, basePath);
		BaseStorage remote = createRemote(config, basePath);

		this.core = new JevernoteCore(local, remote, interactive, save, dryRun);
	}

	private BaseStorage createLocal(Config config, File basePath) {
		BaseStorage storage = new FSSWIUsingProperties(config, basePath);

		return storage;
	}

	private BaseStorage createRemote(Config config, File basePath) {
		ContentProcessor proces = new EvernoteStrippingNewliningProcessor();
		BaseStorage storage = new EvernoteStorage(config, basePath, proces);

		return storage;
	}

	///////////////////////////////////////////////////////////////////////////

	public boolean isInstalled() {
		try {
			return core.isInstalled();
		} catch (Exception e) {
			LOG.error("Cannot find out if is installed.", e);
			return false;
		}
	}

	public void installAndLoad(String installData) {
		try {
			core.installAndLoad(installData);
			loaded = true;
		} catch (Exception e) {
			LOG.error("Cannot install and load.");
		}

	}

	@Override
	public void load() {
		try {
			core.load();
			loaded = true;
		} catch (JevernoteException e) {
			LOG.error("Cannot load.", e);
		}
	}

	@Override
	public void store() {
		try {
			core.store();
			loaded = false;
		} catch (JevernoteException e) {
			LOG.error("Cannot store.", e);
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
			LOG.error("Command init failed.", e);
			return false;
		}
	}

	public boolean cmdClone(String remoteToken) {
		try {
			core.cloneCmd(null, remoteToken);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command clone failed.", e);
			return false;
		}
	}

	public boolean cmdPush(boolean weak, boolean force) {
		try {
			core.pushCmd(weak, force);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command push failed.", e);
			return false;
		}
	}

	public boolean cmdPull(boolean weak, boolean force) {
		try {
			core.pullCmd(weak, force);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command pull failed.", e);
			return false;
		}
	}

	public boolean cmdSynchronize() {
		try {
			core.synchronizeCmd();
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command synchronize failed.", e);
			return false;
		}
	}


	public boolean cmdStatus() {
		try {
			core.statusCmd();
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command status failed.", e);
			return false;
		}
	}

	
	///////////////////////////////////////////////////////////////////////////

	// TODO mv, ad, rm, ...

	///////////////////////////////////////////////////////////////////////////

}
