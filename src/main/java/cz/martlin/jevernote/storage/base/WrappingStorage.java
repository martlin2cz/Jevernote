package cz.martlin.jevernote.storage.base;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;

public abstract class WrappingStorage implements BaseStorage, RequiresLoad {

	private final BaseStorage wrapped;

	private boolean loaded;

	public WrappingStorage(BaseStorage wrapped) {
		this.wrapped = wrapped;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void load() throws Exception {
		StorageRequiringLoad.checkAndLoad(wrapped);
		loaded = true;
	}

	@Override
	public void store() throws Exception {
		StorageRequiringLoad.checkAndStore(wrapped);
		loaded = false;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void initialize(String storageDesc) throws JevernoteException {
		wrapped.initialize(storageDesc);
	}

	public StorageData list() throws JevernoteException {
		return wrapped.list();
	}

	public List<Package> listPackages() throws JevernoteException {
		return wrapped.listPackages();
	}

	public List<Item> listItems(Package pack) throws JevernoteException {
		return wrapped.listItems(pack);
	}

	public void createPackage(Package pack) throws JevernoteException {
		wrapped.createPackage(pack);
	}

	public void createItem(Item item) throws JevernoteException {
		wrapped.createItem(item);
	}

	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		wrapped.movePackage(oldPack, newPack);
	}

	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		wrapped.moveItem(oldItem, newItem);
	}

	public void updateItem(Item item) throws JevernoteException {
		wrapped.updateItem(item);
	}

	public void removePackage(Package pack) throws JevernoteException {
		wrapped.removePackage(pack);
	}

	public void removeItem(Item item) throws JevernoteException {
		wrapped.removeItem(item);
	}

	@Override
	public void backupPackage(Package pack) throws JevernoteException {
		wrapped.backupPackage(pack);
	}

	@Override
	public void backupItem(Item item) throws JevernoteException {
		wrapped.backupItem(item);
	}

}
