package cz.martlin.jevernote.perf.impl;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.strategy.base.BaseDifferencePerformStrategy;

public class DiffPerformerUsingStragegies extends BaseDifferencesPerformer {

	private final BaseDifferencePerformStrategy strategy;

	public DiffPerformerUsingStragegies(BaseStorage target,
			BaseDifferencePerformStrategy strategy) {
		super(target);

		this.strategy = strategy;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void performCreatePackage(Package pack) throws JevernoteException {
		if (strategy.performCreatePackage(pack)) {
			target.createPackage(pack);
		}
	}

	@Override
	protected void performRenamePackage(Package oldPack, Package newPack) throws JevernoteException {
		if (strategy.performRenamePackage(oldPack, newPack)) {
			target.movePackage(oldPack, newPack);
		}
	}

	@Override
	protected void performDeletePackage(Package pack) throws JevernoteException {
		if (strategy.performDeletePackage(pack)) {
			target.removePackage(pack);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void performCreateItem(Item item) throws JevernoteException {
		if (strategy.performCreateItem(item)) {
			target.createItem(item);
		}
	}

	@Override
	protected void performRenameItem(Item oldItem, Item newItem) throws JevernoteException {
		if (strategy.performRenameItem(oldItem, newItem)) {
			target.moveItem(oldItem, newItem);
		}
	}

	@Override
	protected void performUpdateItem(Item oldItem, Item newItem) throws JevernoteException {
		if (strategy.performUpdateItem(oldItem, newItem)) {
			target.updateItem(newItem);
		}
	}

	@Override
	protected void performRemoveItem(Item item) throws JevernoteException {
		if (strategy.performRemoveItem(item)) {
			target.removeItem(item);
		}
	}

}
