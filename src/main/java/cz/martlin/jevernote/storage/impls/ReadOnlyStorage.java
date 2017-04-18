package cz.martlin.jevernote.storage.impls;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;
import cz.martlin.jevernote.storage.base.WrappingStorage;

public class ReadOnlyStorage extends WrappingStorage {

	public ReadOnlyStorage(StorageRequiringLoad source) {
		super(source);
	}

	@Override
	public StorageData doList() throws JevernoteException {
		return super.doList();
	}

	@Override
	public List<Package> doListPackages() throws JevernoteException {
		return super.doListPackages();
	}

	@Override
	public List<Item> doListItems(Package pack) throws JevernoteException {
		return super.doListItems(pack);
	}

	// TODO at least print? LOG or stdout? or what?!

	@Override
	public void doCreatePackage(Package pack) {
		// nop
	}

	@Override
	public void doCreateItem(Item item) {
		// nop
	}

	@Override
	public void doMovePackage(Package oldPack, Package newPack) {
		// nop
	}

	@Override
	public void doMoveItem(Item oldItem, Item newItem) {
		// nop
	}

	@Override
	public void doUpdateItem(Item item) {
		// nop
	}

	@Override
	public void doRemovePackage(Package pack) {
		// nop
	}

	@Override
	public void doRemoveItem(Item item) {
		// nop
	}

	@Override
	public void doBackupPackage(Package pack) {
		// nop
	}

	@Override
	public void doBackupItem(Item item) {
		// nop
	}

}
