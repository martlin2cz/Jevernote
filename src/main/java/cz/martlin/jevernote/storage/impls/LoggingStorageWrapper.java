package cz.martlin.jevernote.storage.impls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;
import cz.martlin.jevernote.storage.base.WrappingStorage;

public class LoggingStorageWrapper extends WrappingStorage {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public LoggingStorageWrapper(StorageRequiringLoad wrapped) {
		super(wrapped);
	}

	///////////////////////////////////////////////////////////////////////////

	public StorageData doList() throws JevernoteException {
		return super.doList();
	}

	public List<Package> doListPackages() throws JevernoteException {
		return super.doListPackages();
	}

	public List<Item> doListItems(Package pack) throws JevernoteException {
		return super.doListItems(pack);
	}

	///////////////////////////////////////////////////////////////////////////

	public void doCreatePackage(Package pack) throws JevernoteException {
		LOG.debug("Creating package " + pack.getName());
		super.doCreatePackage(pack);
		LOG.info("Created package " + pack.getName());
	}

	public void doCreateItem(Item item) throws JevernoteException {
		LOG.debug("Creating item " + item.getName());
		super.doCreateItem(item);
		LOG.info("Created item " + item.getName());
	}

	public void doMovePackage(Package oldPack, Package newPack) throws JevernoteException {
		LOG.debug("Moving package " + oldPack.getName());
		super.doMovePackage(oldPack, newPack);
		LOG.info("Moved package " + oldPack.getName());
	}

	public void doMoveItem(Item oldItem, Item newItem) throws JevernoteException {
		LOG.debug("Moving item " + oldItem.getName());
		super.doMoveItem(oldItem, newItem);
		LOG.info("Moved item " + oldItem.getName());
	}

	public void doUpdateItem(Item item) throws JevernoteException {
		LOG.debug("Updating item " + item.getName());
		super.doUpdateItem(item);
		LOG.info("Updated item " + item.getName());
	}

	public void doRemovePackage(Package pack) throws JevernoteException {
		LOG.debug("Removing package " + pack.getName());
		super.doRemovePackage(pack);
		LOG.info("Removed package " + pack.getName());
	}

	public void doRemoveItem(Item item) throws JevernoteException {
		LOG.debug("Removing item " + item.getName());
		super.doRemoveItem(item);
		LOG.info("Removed item " + item.getName());
	}

	@Override
	public void doBackupPackage(Package pack) throws JevernoteException {
		LOG.debug("Backing up package " + pack.getName());
		super.doBackupPackage(pack);
		LOG.info("Backed up package " + pack.getName());
	}

	@Override
	public void doBackupItem(Item item) throws JevernoteException {
		LOG.debug("Backing up item " + item.getName());
		super.doBackupItem(item);
		LOG.info("Backed up item " + item.getName());

	}

}
