package cz.martlin.jevernote.core.base;

import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.diff.core.StoragesDifferencer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.DefaultDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.ForceDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.WeakDifferencesPerformer;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.impls.LoggingStorageWrapper;

public abstract class BaseJevernoteCore {

	private BaseStorage local;
	private BaseStorage remote;
	private boolean loaded;

	public BaseJevernoteCore() {
		super();
	}

	public abstract BaseStorage createLocal() throws JevernoteException;

	public abstract BaseStorage createRemote()throws JevernoteException;

	///////////////////////////////////////////////////////////////////////////

	public void initCmd(String remoteSpecifier) {
		// TODO
	}

	public void cloneCmd(String remoteSpecifier) {
		// TODO
	}

	public void pushCmd(String remoteSpecifier) {
		// TODO
	}

	public void pullCmd(String remoteSpecifier) {
		// TODO
	}

	///////////////////////////////////////////////////////////////////////////

	public void initialize() throws JevernoteException {
		if (isInitialized()) {
			Exception e = new IllegalStateException("Yet initialized");
			throw new JevernoteException("Yet initialized", e);
		}

		doInitialize();
	}

	protected boolean isInitialized() {
		return local != null && remote != null;
	}

	protected void doInitialize() throws JevernoteException {
		try {
			local = createLocal();
			local.install();

			remote = createRemote();
			remote.install();
		} catch (JevernoteException e) {
			throw new JevernoteException("Cannot initialize", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	public void checkAndLoad() throws JevernoteException {
		if (!loaded) {
			doLoad();
		}
	}

	private void doLoad() throws JevernoteException {
		try {
			local.initialize();
			remote.initialize();
			loaded = true;
		} catch (JevernoteException e) {
			throw new JevernoteException("Cannot load", e);
		}
	}

	public void complete() throws JevernoteException {
		if (!loaded) {
			Exception e = new IllegalStateException("Not loaded");
			throw new JevernoteException("Not loaded", e);
		}

		doComplete();
	}

	private void doComplete() throws JevernoteException {
		try {
			local.finish();
			remote.finish();
			loaded = false;
		} catch (JevernoteException e) {
			throw new JevernoteException("Cannot complete", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	// XXX
	// XXX
	// XXX

	@Deprecated
	public abstract BaseStorage getLocal();

	@Deprecated
	public abstract BaseStorage getRemote();

	@Deprecated
	public LoggingStorageWrapper getWrappedLocal() {
		return new LoggingStorageWrapper(getLocal());
	}

	@Deprecated
	public LoggingStorageWrapper getWrappedRemote() {
		return new LoggingStorageWrapper(getRemote());
	}

	///////////////////////////////////////////////////////////////////////////

	public abstract boolean isReady();

	public abstract void start() throws JevernoteException;

	public abstract void finish() throws JevernoteException;

	private void checkReady() throws JevernoteException {
		if (!isReady()) {
			Exception e = new IllegalStateException("Not ready");
			throw new JevernoteException("Not ready", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	public abstract void initialize(String remoteIdentifier) throws JevernoteException;

	public void xXXXcloneCmd(String remoteIdentifier) throws JevernoteException {
		initialize(remoteIdentifier);
		pull(false, true);
	}

	// TODO load/list
	// TODO --dry-run ?

	public void push(boolean weak, boolean force) throws JevernoteException {
		checkReady();

		transfer(getWrappedLocal(), getWrappedRemote(), weak, force);
	}

	public void pull(boolean weak, boolean force) throws JevernoteException {
		checkReady();

		transfer(getWrappedRemote(), getWrappedLocal(), weak, force);
	}

	///////////////////////////////////////////////////////////////////////////

	private void transfer(BaseStorage source, BaseStorage target, boolean weak, boolean force)
			throws JevernoteException {

		StoragesDifferencer differ = new StoragesDifferencer();
		StoragesDifference diff = differ.compute(source, target);

		BaseDifferencesPerformer perf = findPerformer(source, target, weak, force);
		perf.performDifferences(diff);
	}

	private BaseDifferencesPerformer findPerformer(BaseStorage source, BaseStorage target, boolean weak,
			boolean force) {

		if (weak) {
			return new WeakDifferencesPerformer(source, target);
		}

		if (force) {
			return new ForceDifferencesPerformer(source, target);
		}

		return new DefaultDifferencesPerformer(source, target);
	}

}
