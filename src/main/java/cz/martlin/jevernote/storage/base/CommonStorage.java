package cz.martlin.jevernote.storage.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;

/**
 * The base storage for most of storage implementations. Each {@link Package}
 * and {@link Item} are converted to so-called "native" (of types PT and IT) and
 * correspondingly created/updated/modified/moved/removed/backed up/...
 * 
 * @author martin
 *
 * @param <PT>
 * @param <IT>
 */
public abstract class CommonStorage<PT, IT> extends StorageRequiringLoad {

	public CommonStorage(Config config) {
		super(config);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public StorageData doList() throws JevernoteException {
		Map<Package, List<Item>> result = new HashMap<>();

		List<Package> packages = listPackages();
		for (Package pack : packages) {
			List<Item> items = listItems(pack);
			result.put(pack, items);
		}

		return new StorageData(result);
	}

	@Override
	public List<Package> doListPackages() throws JevernoteException {
		try {
			List<PT> natives = listNativePackages();
			List<Package> packages = new ArrayList<>(natives.size());

			for (PT nativ : natives) {
				Package pack = nativeToPackage(nativ);
				packages.add(pack);
			}

			return packages;
		} catch (Exception e) {
			throw new JevernoteException("Cannot list packages", e);
		}
	}

	protected abstract List<PT> listNativePackages() throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public List<Item> doListItems(Package pack) throws JevernoteException {
		try {
			List<IT> natives = listNativeItems(pack);
			List<Item> items = new ArrayList<>(natives.size());

			for (IT nativ : natives) {
				Item item = nativeToItem(pack, nativ);
				items.add(item);
			}

			return items;
		} catch (Exception e) {
			throw new JevernoteException("Cannot list items", e);
		}
	}

	protected abstract List<IT> listNativeItems(Package pack) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void doCreatePackage(Package pack) throws JevernoteException {
		try {
			PT nativ = packageToNative(pack);

			createPackageNative(pack, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create package", e);
		}

	}

	protected abstract void createPackageNative(Package pack, PT nativ) throws Exception;

	@Override
	public void doCreateItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			createItemNative(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create item", e);
		}
	}

	protected abstract void createItemNative(Item item, IT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void doMovePackage(Package oldPack, Package newPack) throws JevernoteException {
		try {
			PT oldNativ = packageToNative(oldPack);
			PT newNativ = packageToNative(newPack);

			movePackageNative(oldPack, newPack, oldNativ, newNativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update package", e);
		}

	}

	protected abstract void movePackageNative(Package oldPack, Package newPack, PT oldNativ, PT newNativ)
			throws Exception;

	@Override
	public void doMoveItem(Item oldItem, Item newItem) throws JevernoteException {
		try {
			IT oldNativ = itemToNative(oldItem);
			IT newNativ = itemToNative(newItem);

			moveItemNative(oldItem, newItem, oldNativ, newNativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update item", e);
		}
	}

	protected abstract void moveItemNative(Item oldItem, Item newItem, IT oldNativ, IT newNativ) throws Exception;

	@Override
	public void doUpdateItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			updateItemNative(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update item", e);
		}
	}

	protected abstract void updateItemNative(Item item, IT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void doRemovePackage(Package pack) throws JevernoteException {
		try {
			PT nativ = packageToNative(pack);

			removePackageNative(pack, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove package", e);
		}
	}

	protected abstract void removePackageNative(Package pack, PT nativ) throws Exception;

	@Override
	public void doRemoveItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			removeItemNative(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove item", e);
		}
	}

	protected abstract void removeItemNative(Item item, IT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void doBackupPackage(Package pack) throws JevernoteException {
		try {
			PT nativ = packageToNative(pack);

			backupPackageNative(pack, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot backup package", e);
		}
	}

	protected abstract void backupPackageNative(Package pack, PT nativ) throws Exception;

	@Override
	public void doBackupItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			backupItemNative(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot backup item", e);
		}
	}

	protected abstract void backupItemNative(Item item, IT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	protected abstract IT itemToNative(Item item) throws Exception;

	protected abstract Item nativeToItem(Package pack, IT nativ) throws Exception;

	protected abstract PT packageToNative(Package pack) throws Exception;

	protected abstract Package nativeToPackage(PT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	protected static Calendar toCalendar(long value) {
		Calendar cal = Calendar.getInstance();

		cal.setTimeInMillis(value);

		return cal;
	}
}