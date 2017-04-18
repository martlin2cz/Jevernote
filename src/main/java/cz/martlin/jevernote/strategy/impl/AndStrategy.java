package cz.martlin.jevernote.strategy.impl;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.strategy.base.BaseOperationsStrategy;

public class AndStrategy implements BaseOperationsStrategy {

	private final BaseOperationsStrategy first;
	private final BaseOperationsStrategy second;

	public AndStrategy(BaseOperationsStrategy first, BaseOperationsStrategy second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean performCreatePackage(Package pack) {
		return first.performCreatePackage(pack) && second.performCreatePackage(pack);
	}

	@Override
	public boolean performRenamePackage(Package oldPack, Package newPack) {
		return first.performRenamePackage(oldPack, newPack) && second.performRenamePackage(oldPack, newPack);
	}

	@Override
	public boolean performDeletePackage(Package pack) {
		return first.performDeletePackage(pack) && second.performDeletePackage(pack);
	}

	@Override
	public boolean performCreateItem(Item item) {
		return first.performCreateItem(item) && second.performCreateItem(item);
	}

	@Override
	public boolean performRenameItem(Item oldItem, Item newItem) {
		return first.performRenameItem(oldItem, newItem) && second.performRenameItem(oldItem, newItem);
	}

	@Override
	public boolean performUpdateItem(Item oldItem, Item newItem) {
		return first.performUpdateItem(oldItem, newItem) && second.performUpdateItem(oldItem, newItem);
	}

	@Override
	public boolean performRemoveItem(Item item) {
		return first.performRemoveItem(item) && second.performRemoveItem(item);
	}

}
