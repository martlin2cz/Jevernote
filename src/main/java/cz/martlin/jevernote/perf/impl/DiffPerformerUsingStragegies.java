package cz.martlin.jevernote.perf.impl;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.perf.base.BaseDifferencesPerformer;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.strategy.base.BaseOperationsStrategy;

public class DiffPerformerUsingStragegies extends BaseDifferencesPerformer {

	private final BaseOperationsStrategy operationStrategy;
	private final BaseOperationsStrategy backupStrategy;

	public DiffPerformerUsingStragegies(BaseStorage source, BaseStorage target, BaseOperationsStrategy operationStrategy,
			BaseOperationsStrategy backupStrategy) {
		super(source, target);

		this.operationStrategy = operationStrategy;
		this.backupStrategy = backupStrategy;

	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void performCreatePackage(Package pack) throws JevernoteException {
		if (operationStrategy.performCreatePackage(pack)) {
			target.createPackage(pack);
		}
	}

	@Override
	protected void performRenamePackage(Package oldPack, Package newPack) throws JevernoteException {
		if (operationStrategy.performRenamePackage(oldPack, newPack)) {
			if (backupStrategy.performRenamePackage(oldPack, newPack)) {
				target.backupPackage(oldPack);
			}
			target.movePackage(oldPack, newPack);

		}
	}

	@Override
	protected void performDeletePackage(Package pack) throws JevernoteException {
		if (operationStrategy.performDeletePackage(pack)) {
			if (backupStrategy.performDeletePackage(pack)) {
				target.backupPackage(pack);
			}
			target.removePackage(pack);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void performCreateItem(Item item) throws JevernoteException {
		if (operationStrategy.performCreateItem(item)) {
			target.createItem(item);
		}
	}

	@Override
	protected void performRenameItem(Item oldItem, Item newItem) throws JevernoteException {
		if (operationStrategy.performRenameItem(oldItem, newItem)) {
			if (backupStrategy.performRenameItem(oldItem, newItem)) {
				target.backupItem(oldItem);
			}
			target.moveItem(oldItem, newItem);
		}
	}

	@Override
	protected void performUpdateItem(Item oldItem, Item newItem) throws JevernoteException {
		if (operationStrategy.performUpdateItem(oldItem, newItem)) {
			if (backupStrategy.performRenameItem(oldItem, newItem)) {
				target.backupItem(oldItem);
			}
			target.updateItem(newItem);
		}
	}

	@Override
	protected void performRemoveItem(Item item) throws JevernoteException {
		if (operationStrategy.performRemoveItem(item)) {
			if (backupStrategy.performRenameItem(item, item)) {
				target.backupItem(item);
			}
			target.removeItem(item);
		}
	}

}
