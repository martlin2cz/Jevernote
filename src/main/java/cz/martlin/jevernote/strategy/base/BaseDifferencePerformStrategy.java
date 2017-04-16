package cz.martlin.jevernote.strategy.base;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;

public interface BaseDifferencePerformStrategy {

	public boolean performCreatePackage(Package pack);

	public boolean performRenamePackage(Package oldPack, Package newPack);

	public boolean performDeletePackage(Package pack);

	public boolean performCreateItem(Item item);

	public boolean performRenameItem(Item oldItem, Item newItem);

	public boolean performUpdateItem(Item oldItem, Item newItem);

	public boolean performRemoveItem(Item item);
}
