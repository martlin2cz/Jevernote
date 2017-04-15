package cz.martlin.jevernote.core.base;

import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.diff.core.StoragesDifferencer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.DefaultDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.ForceDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.WeakDifferencesPerformer;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.impls.LoggingStorageWrapper;

public abstract class BaseJevernoteCore {

	public BaseJevernoteCore() {
		super();
	}

	public abstract BaseStorage getLocal();

	public abstract BaseStorage getRemote();
	
	public LoggingStorageWrapper getWrappedLocal() {
		return new LoggingStorageWrapper(getLocal());
	}
	
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

	public void cloneCmd(String remoteIdentifier) throws JevernoteException {
		initialize(remoteIdentifier);
		pull(false, true);
	}

	//TODO load/list
	//TODO --dry-run ?
	
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
