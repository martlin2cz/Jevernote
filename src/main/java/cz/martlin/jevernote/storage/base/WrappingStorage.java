package cz.martlin.jevernote.storage.base;

import java.util.List;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;

public abstract class WrappingStorage extends StorageRequiringLoad {

	private final StorageRequiringLoad wrapped;

	public WrappingStorage(StorageRequiringLoad wrapped) {
		super(wrapped.config);

		this.wrapped = wrapped;
	}

	public boolean doIsInstalled() throws Exception {
		//return true;
		return wrapped.doIsInstalled();
	}

	@Override
	protected void doInstallAndLoad(String installData) throws Exception {
		wrapped.doInstallAndLoad(installData);
	}

	@Override
	protected void doLoad() throws Exception {
		wrapped.doLoad();
	}

	@Override
	protected void doStore() throws Exception {
		wrapped.doStore();
	}
	
	

	///////////////////////////////////////////////////////////////////////////

	public StorageData doList() throws JevernoteException {
		return wrapped.doList();
	}

	public List<Package> doListPackages() throws JevernoteException {
		return wrapped.doListPackages();
	}

	public List<Item> doListItems(Package pack) throws JevernoteException {
		return wrapped.doListItems(pack);
	}

	public void doCreatePackage(Package pack) throws JevernoteException {
		wrapped.doCreatePackage(pack);
	}

	public void doCreateItem(Item item) throws JevernoteException {
		wrapped.doCreateItem(item);
	}

	public void doMovePackage(Package oldPack, Package newPack) throws JevernoteException {
		wrapped.doMovePackage(oldPack, newPack);
	}

	public void doMoveItem(Item oldItem, Item newItem) throws JevernoteException {
		wrapped.doMoveItem(oldItem, newItem);
	}

	public void doUpdateItem(Item item) throws JevernoteException {
		wrapped.doUpdateItem(item);
	}

	public void doRemovePackage(Package pack) throws JevernoteException {
		wrapped.doRemovePackage(pack);
	}

	public void doRemoveItem(Item item) throws JevernoteException {
		wrapped.doRemoveItem(item);
	}

	public void doBackupPackage(Package pack) throws JevernoteException {
		wrapped.doBackupPackage(pack);
	}

	public void doBackupItem(Item item) throws JevernoteException {
		wrapped.doBackupItem(item);
	}

	///////////////////////////////////////////////////////////////////////////

}
