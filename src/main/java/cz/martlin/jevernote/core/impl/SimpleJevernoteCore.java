package cz.martlin.jevernote.core.impl;

import cz.martlin.jevernote.core.base.BaseJevernoteCore;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;

public class SimpleJevernoteCore extends BaseJevernoteCore {

	private final BaseStorage local;
	private final BaseStorage remote;

	public SimpleJevernoteCore(BaseStorage local, BaseStorage remote) {
		super();
		this.local = local;
		this.remote = remote;
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
		return true;
	}

	@Override
	public void start() throws JevernoteException {
		// assuming nothing special is needed
	}

	@Override
	public void finish() throws JevernoteException {
		// assuming nothing special is needed
	}

	@Override
	public void initialize(String remoteIdentifier) throws JevernoteException {
		// assuming nothing special is needed

	}

	@Override
	public BaseStorage createLocal() throws JevernoteException {
		// TODO ....
		return null;
	}

	@Override
	public BaseStorage createRemote() throws JevernoteException {
		// TODO ....
		return null;
	}

}
