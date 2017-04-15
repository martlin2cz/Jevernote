package cz.martlin.jevernote.core.impl;

import java.io.File;

import cz.martlin.jevernote.core.base.BaseJevernoteCore;
import cz.martlin.jevernote.dataobj.config.StandartConfig;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.ContentProcessor;
import cz.martlin.jevernote.storage.impls.EvernoteStorage;
import cz.martlin.jevernote.storage.impls.EvernoteStrippingProcessor;
import cz.martlin.jevernote.storage.impls.FileSystemStorageWithIndexFile;

public class StandartJevernoteCore extends BaseJevernoteCore {

	private static final ContentProcessor EVERNOTE_CONTENT_PROCESSOR = new EvernoteStrippingProcessor();

	private final File basePath;

	private StandartConfig config = null;
	private FileSystemStorageWithIndexFile local = null;
	private EvernoteStorage remote = null;

	public StandartJevernoteCore(File basePath) {
		super();

		this.basePath = basePath;
	}

	@Override
	public BaseStorage getLocal() {
		return local;
	}

	@Override
	public BaseStorage getRemote() {
		return remote;
	}

	@Override
	public boolean isReady() {
		return (local != null) && (remote != null) && (config != null);
	}

	@Override
	public void start() throws JevernoteException {
		config = StandartConfig.load();
		String token = config.getAuthToken();

		local = new FileSystemStorageWithIndexFile(basePath);
		remote = new EvernoteStorage(token, EVERNOTE_CONTENT_PROCESSOR);
	}

	@Override
	public void finish() throws JevernoteException {
		local.checkAndSaveChanges();
		StandartConfig.save(config);
	}

	@Override
	public void initialize(String remoteIdentifier) throws JevernoteException {

		FileSystemStorageWithIndexFile.createIndexFile(basePath);
		
		config = new StandartConfig();
		StandartConfig.save(config);
	}

}
