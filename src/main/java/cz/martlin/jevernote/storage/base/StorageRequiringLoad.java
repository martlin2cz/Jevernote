package cz.martlin.jevernote.storage.base;

import java.util.List;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;

/**
 * Represents abstract storage which requires some "load" and "store" procedures
 * before and after using. This storage should also specify some installation
 * procedure.
 * 
 * @author martin
 *
 */
public abstract class StorageRequiringLoad implements BaseStorage, RequiresLoad<String> {

	protected final Config config;
	private boolean loaded;

	public StorageRequiringLoad(Config config) {
		this.config = config;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public final boolean isInstalled() throws JevernoteException {
		try {
			return doIsInstalled();
		} catch (Exception e) {
			throw new JevernoteException("Cannot find out if is storage installed", e);
		}
	}

	/**
	 * Concretelly finds out and returns true, if this storage is installed.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract boolean doIsInstalled() throws Exception;

	@Override
	public final void load() throws JevernoteException {
		if (!isInstalled()) {
			return;
		}

		if (loaded) {
			// Exception e = new IllegalStateException("Already loaded");
			// throw new JevernoteException(e);
			return;
		}

		try {
			doLoad();
		} catch (Exception e) {
			throw new JevernoteException("Cannot load", e);
		}

		loaded = true;
	}

	/**
	 * Conretelly does the load.
	 * 
	 * @throws Exception
	 */
	protected abstract void doLoad() throws Exception;

	/**
	 * If is yet installed, loads.
	 * 
	 * @throws JevernoteException
	 */
	public void checkInstallAndLoad() throws JevernoteException {
		if (isInstalled()) {
			load();
		}
	}

	@Override
	public final void store() throws JevernoteException {
		if (!loaded) {
			Exception e = new IllegalStateException("Not loaded");
			throw new JevernoteException(e);
		}

		try {
			doStore();
		} catch (Exception e) {
			throw new JevernoteException("Cannot store", e);
		}

		loaded = false;
	}

	/**
	 * Concretelly does the store.
	 * 
	 * @throws Exception
	 */
	protected abstract void doStore() throws Exception;

	/**
	 * If is yet installed, stores.
	 * 
	 * @throws JevernoteException
	 */
	public void checkInstallAndStore() throws JevernoteException {
		if (isInstalled()) {
			store();
		}
	}

	@Override
	public final boolean isLoaded() {
		return loaded;
	}

	@Override
	public final void installAndLoad(String installData) throws JevernoteException {
		try {
			doInstallAndLoad(installData);
			loaded = true;
		} catch (Exception e) {
			throw new JevernoteException("Cannot install/load", e);
		}
	}

	/**
	 * Concretelly does the installation and load.
	 * 
	 * @param installData
	 * @throws Exception
	 */
	protected abstract void doInstallAndLoad(String installData) throws Exception;
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether is the service yet loaded. If not throws exception.
	 * 
	 * @throws JevernoteException
	 */
	private void check() throws JevernoteException {
		if (!isLoaded()) {
			Exception e = new IllegalStateException(getClass().getSimpleName() + " requires load");
			throw new JevernoteException("Not yet loaded", e);
		}
	}

	@Override
	public final void initialize(String storageDesc) throws JevernoteException {
		installAndLoad(storageDesc);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public final StorageData list() throws JevernoteException {
		check();
		return doList();
	}

	@Override
	public final List<Package> listPackages() throws JevernoteException {
		check();
		return doListPackages();
	}

	@Override
	public final List<Item> listItems(Package pack) throws JevernoteException {
		check();
		return doListItems(pack);
	}

	@Override
	public final void createPackage(Package pack) throws JevernoteException {
		check();
		doCreatePackage(pack);
	}

	@Override
	public final void createItem(Item item) throws JevernoteException {
		check();
		doCreateItem(item);
	}

	@Override
	public final void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		check();
		doMovePackage(oldPack, newPack);
	}

	@Override
	public final void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		check();
		doMoveItem(oldItem, newItem);
	}

	@Override
	public final void updateItem(Item item) throws JevernoteException {
		check();
		doUpdateItem(item);
	}

	@Override
	public final void removePackage(Package pack) throws JevernoteException {
		check();
		doRemovePackage(pack);
	}

	@Override
	public final void removeItem(Item item) throws JevernoteException {
		check();
		doRemoveItem(item);
	}

	@Override
	public final void backupPackage(Package pack) throws JevernoteException {
		check();
		doBackupPackage(pack);
	}

	@Override
	public final void backupItem(Item item) throws JevernoteException {
		check();
		doBackupItem(item);
	}

	///////////////////////////////////////////////////////////////////////////

	public abstract StorageData doList() throws JevernoteException;

	public abstract List<Package> doListPackages() throws JevernoteException;

	public abstract List<Item> doListItems(Package pack) throws JevernoteException;

	public abstract void doCreatePackage(Package pack) throws JevernoteException;

	public abstract void doCreateItem(Item item) throws JevernoteException;

	public abstract void doMovePackage(Package oldPack, Package newPack) throws JevernoteException;

	public abstract void doMoveItem(Item oldItem, Item newItem) throws JevernoteException;

	public abstract void doUpdateItem(Item item) throws JevernoteException;

	public abstract void doRemovePackage(Package pack) throws JevernoteException;

	public abstract void doRemoveItem(Item item) throws JevernoteException;

	public abstract void doBackupPackage(Package pack) throws JevernoteException;

	public abstract void doBackupItem(Item item) throws JevernoteException;

	///////////////////////////////////////////////////////////////////////////

}
