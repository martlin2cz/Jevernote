package cz.martlin.jevernote.storage.impls;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.WrappingStorage;

public class LoggingStorageWrapper extends WrappingStorage {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final String suffix;

	public LoggingStorageWrapper(BaseStorage wrapped, String suffix) {
		super(wrapped);

		this.suffix = suffix;
	}

	///////////////////////////////////////////////////////////////////////////
	@Override
	public StorageData list() throws JevernoteException {
		LOG.info("Listing data " + suffix);
		StorageData data = super.list();
		LOG.debug("Listed data, found " + data.getItems().size() + " items in " + data.getPackages().size()
				+ " packages " + suffix);
		return data;
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		return super.listPackages();
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		return super.listItems(pack);
	}

	///////////////////////////////////////////////////////////////////////////
	@Override
	public void createPackage(Package pack) throws JevernoteException {
		LOG.debug("Creating package " + pack.getName() + " " + suffix);
		super.createPackage(pack);
		LOG.info("Created package " + pack.getName() + " " + suffix);
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		LOG.debug("Creating item " + item.getName() + " " + suffix);
		super.createItem(item);
		LOG.info("Created item " + item.getName() + " " + suffix);
	}

	@Override
	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		LOG.debug("Moving package " + oldPack.getName() + " " + suffix);
		super.movePackage(oldPack, newPack);
		LOG.info("Moved package " + oldPack.getName() + " " + suffix);
	}

	@Override
	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		LOG.debug("Moving item " + oldItem.getName() + " " + suffix);
		super.moveItem(oldItem, newItem);
		LOG.info("Moved item " + oldItem.getName() + " " + suffix);
	}

	@Override
	public void updateItem(Item item) throws JevernoteException {
		LOG.debug("Updating item " + item.getName() + " " + suffix);
		super.updateItem(item);
		LOG.info("Updated item " + item.getName() + " " + suffix);
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		LOG.debug("Removing package " + pack.getName() + " " + suffix);
		super.removePackage(pack);
		LOG.info("Removed package " + pack.getName() + " " + suffix);
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		LOG.debug("Removing item " + item.getName() + " " + suffix);
		super.removeItem(item);
		LOG.info("Removed item " + item.getName() + " " + suffix);
	}

	@Override
	public void backupPackage(Package pack) throws JevernoteException {
		LOG.debug("Backing up package " + pack.getName() + " " + suffix);
		super.backupPackage(pack);
		LOG.info("Backed up package " + pack.getName() + " " + suffix);
	}

	@Override
	public void backupItem(Item item) throws JevernoteException {
		LOG.debug("Backing up item " + item.getName() + " " + suffix);
		super.backupItem(item);
		LOG.info("Backed up item " + item.getName() + " " + suffix);
	}

	@Override
	public void donePackChangeOnAnother(Change<Package> change) throws JevernoteException {
		super.donePackChangeOnAnother(change);
	}

	@Override
	public void doneItemChangeOnAnother(Change<Item> change) throws JevernoteException {
		super.doneItemChangeOnAnother(change);
	}

}
