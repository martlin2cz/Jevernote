package cz.martlin.jevernote.core.impl;

import java.io.File;

import cz.martlin.jevernote.core.base.BaseJevernoteCore;
import cz.martlin.jevernote.dataobj.config.StandartConfig;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.ContentProcessor;
import cz.martlin.jevernote.storage.impls.EvernoteStorage;
import cz.martlin.jevernote.storage.impls.EvernoteStrippingProcessor;
import cz.martlin.jevernote.storage.impls.FSwIndexFileStorageWrapper;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;

public class XXXStandartJevernoteCore extends BaseJevernoteCore {

	private static final ContentProcessor EVERNOTE_CONTENT_PROCESSOR = new EvernoteStrippingProcessor();

	private final File basePath;
	private final boolean dryRun;

	private StandartConfig config = null;
	private BaseStorage local = null;
	private BaseStorage remote = null;

	public XXXStandartJevernoteCore(File basePath, boolean dryRun) {
		super();

		this.basePath = basePath;
		this.dryRun = dryRun;
	}

	public void initialize() throws JevernoteException {
		config = StandartConfig.load();
	}
	
	public BaseStorage createLocal() {
		if (dryRun) {
			return new InMemoryStorage();
		} else {
			return new FSwIndexFileStorageWrapper(basePath);
		}
	}
	
	public BaseStorage createRemote() throws JevernoteException {
		String token = config.getAuthToken();
		return new EvernoteStorage(token, EVERNOTE_CONTENT_PROCESSOR);
	}

	@Deprecated
	@Override
	public BaseStorage getLocal() {
		return local;
	}

	@Deprecated
	@Override
	public BaseStorage getRemote() {
		return remote;
	}

	@Deprecated
	@Override
	public boolean isReady() {
		return (local != null) && (remote != null) && (config != null);
	}

	@Deprecated
	@Override
	public void start() throws JevernoteException {
		local.initialize();

		config = StandartConfig.load();
		String token = config.getAuthToken();
		remote = new EvernoteStorage(token, EVERNOTE_CONTENT_PROCESSOR);
		remote.initialize();
	}

	@Override
	public void finish() throws JevernoteException {
		local.finish();
		remote.finish();
		StandartConfig.save(config);
	}

	@Override
	public void initialize(String remoteIdentifier) throws JevernoteException {

		local.install();
		remote.install();

		config = new StandartConfig();
		StandartConfig.save(config);
	}

}
