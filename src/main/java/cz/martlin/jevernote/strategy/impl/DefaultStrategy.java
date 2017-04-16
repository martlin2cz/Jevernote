package cz.martlin.jevernote.strategy.impl;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.perf.impl.DiffPerformerUsingStragegies;
import cz.martlin.jevernote.strategy.base.BaseDifferencePerformStrategy;

public class DefaultStrategy implements BaseDifferencePerformStrategy {

	public DefaultStrategy() {
	}

	@Override
	public boolean performCreatePackage(Package pack) {
		return true;
	}

	@Override
	public boolean performRenamePackage(Package oldPack, Package newPack) {
		return true;
	}

	@Override
	public boolean performDeletePackage(Package pack) {
		return true;
	}

	@Override
	public boolean performCreateItem(Item item) {
		return true;
	}

	@Override
	public boolean performRenameItem(Item oldItem, Item newItem) {
		return true;
	}

	@Override
	public boolean performUpdateItem(Item oldItem, Item newItem) {
		return DiffPerformerUsingStragegies.isToNewer(oldItem, newItem);
	}

	@Override
	public boolean performRemoveItem(Item item) {
		return true;
	}

}
