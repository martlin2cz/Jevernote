package cz.martlin.jevernote.storage.base;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;

public abstract class CommonStorage<PT, IT> implements BaseStorage {

	public CommonStorage() {
		super();
	}



	///////////////////////////////////////////////////////////////////////////

	@Override
	public StorageData list() throws JevernoteException {
		Map<Package, List<Item>> result = new HashMap<>();

		List<Package> packages = listPackages();
		for (Package pack : packages) {
			List<Item> items = listItems(pack);
			result.put(pack, items);
		}

		return new StorageData(result);
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
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
	public List<Item> listItems(Package pack) throws JevernoteException {
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
	public void createPackage(Package pack) throws JevernoteException {
		try {
			PT nativ = packageToNative(pack);

			createPackageNative(pack, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create package", e);
		}

	}

	protected abstract void createPackageNative(Package pack, PT nativ) throws Exception;

	@Override
	public void createItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			createNativeItem(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create item", e);
		}
	}

	protected abstract void createNativeItem(Item item, IT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
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
	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
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
	public void updateItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			updateNativeItem(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update item", e);
		}
	}

	protected abstract void updateNativeItem(Item item, IT nativ) throws Exception;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		try {
			PT nativ = packageToNative(pack);

			removePackageNative(pack, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove package", e);
		}
	}

	protected abstract void removePackageNative(Package pack, PT nativ) throws Exception;

	@Override
	public void removeItem(Item item) throws JevernoteException {
		try {
			IT nativ = itemToNative(item);

			removeNativeItem(item, nativ);
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove item", e);
		}
	}

	protected abstract void removeNativeItem(Item item, IT nativ) throws Exception;

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