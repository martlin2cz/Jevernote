package cz.martlin.jevernote.app;

import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.diff.core.StoragesDifferencer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.DefaultDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.ForceDifferencesPerformer;
import cz.martlin.jevernote.perf.impl.WeakDifferencesPerformer;
import cz.martlin.jevernote.storage.base.BaseStorage;

public class JevernoteCore {

	private final BaseStorage local;
	private final BaseStorage remote;

	public JevernoteCore(BaseStorage local, BaseStorage remote) {
		super();
		this.local = local;
		this.remote = remote;
	}

	public void push(boolean weak, boolean force) throws JevernoteException {
		transfer(local, remote, weak, force);
	}

	public void pull(boolean weak, boolean force) throws JevernoteException {
		transfer(remote, local, weak, force);
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
