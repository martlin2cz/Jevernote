package cz.martlin.jevernote.perf.impl;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.storage.base.BaseStorage;

public class WeakDifferencesPerformer extends BaseDifferencesPerformer {

	public WeakDifferencesPerformer(BaseStorage source, BaseStorage target) {
		super(source, target);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void performCreatePackage(Change<Package> change) throws JevernoteException {
		target.createPackage(change.getFirst());
	}

	@Override
	protected void performRenamePackage(Change<Package> change) throws JevernoteException {
		target.movePackage(change.getFirst(), change.getSecond());
	}

	@Override
	protected void performDeletePackage(Change<Package> change) throws JevernoteException {
		// weak performer does not remove
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void performCreateItem(Change<Item> change) throws JevernoteException {
		target.createItem(change.getFirst());
	}

	@Override
	protected void performRenameItem(Change<Item> change) throws JevernoteException {
		target.moveItem(change.getFirst(), change.getSecond());
	}

	@Override
	protected void performUpdateItem(Change<Item> change) throws JevernoteException {
		// weak performer does not update
	}

	@Override
	protected void performRemoveItem(Change<Item> change) throws JevernoteException {
		//weak performer does not remove
	}

}
