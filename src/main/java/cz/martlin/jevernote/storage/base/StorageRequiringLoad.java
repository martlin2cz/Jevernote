package cz.martlin.jevernote.storage.base;

import java.util.List;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;

public abstract class StorageRequiringLoad<PT, IT> //
		extends CommonStorage<PT, IT> //
		implements RequiresLoad {

	protected final Config config;
	private boolean loaded;

	public StorageRequiringLoad(Config config) {
		this.config = config;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void load() throws JevernoteException {
		if (loaded) {
			Exception e = new IllegalStateException("Already loaded");
			throw new JevernoteException(e);
		}
		doLoad();
		loaded = true;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void store() throws JevernoteException {
		if (!loaded) {
			Exception e = new IllegalStateException("Not loaded");
			throw new JevernoteException(e);
		}

		doStore();
		loaded = false;
	}

	///////////////////////////////////////////////////////////////////////////

	protected abstract void doLoad() throws JevernoteException;

	protected abstract void doStore() throws JevernoteException;

	private void check() throws JevernoteException {
		if (!isLoaded()) {
			Exception e = new IllegalStateException("Initialization required");
			throw new JevernoteException("Not yet initialized", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public StorageData list() throws JevernoteException {
		check();
		return super.list();
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		check();
		return super.listPackages();
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		check();
		return super.listItems(pack);
	}

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		check();
		super.createPackage(pack);
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		check();
		super.createItem(item);
	}

	@Override
	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		check();
		super.movePackage(oldPack, newPack);
	}

	@Override
	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		check();
		super.moveItem(oldItem, newItem);
	}

	@Override
	public void updateItem(Item item) throws JevernoteException {
		check();
		super.updateItem(item);
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		check();
		super.removePackage(pack);
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		check();
		super.removeItem(item);
	}

	///////////////////////////////////////////////////////////////////////////

	public static void checkAndLoad(BaseStorage storage) throws JevernoteException {
		if (storage instanceof StorageRequiringLoad) {
			StorageRequiringLoad<?, ?> ris = (StorageRequiringLoad<?, ?>) storage;

			ris.load();
		}
	}

	public static void checkAndStore(BaseStorage storage) throws JevernoteException {
		if (storage instanceof StorageRequiringLoad) {
			StorageRequiringLoad<?, ?> ris = (StorageRequiringLoad<?, ?>) storage;

			ris.store();
		}
	}

}
