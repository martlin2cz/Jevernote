package cz.martlin.jevernote.tools;

import java.util.Calendar;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;

public class TestingUtils {

	private TestingUtils() {
	}

	
	public static Package createPackageObj(boolean withId, String name) {
		String id = withId ? "the-pack-" + name + "-" + System.nanoTime() : null;

		Package pack1 = new Package(id, name);
		return pack1;
	}

	public static Item createItemObj(boolean withId, String packName, String name, String content) {
		Package pack = createPackageObj(withId, packName);

		return createItemObj(withId, pack, name, content);
	}

	public static Item createItemObj(boolean withId, Package pack, String name, String content) {
		String id = withId ? "the-item-" + pack.getName() + "/" + name + "-" + System.nanoTime() : null;
		Calendar date = Calendar.getInstance();

		Item item = new Item(pack, id, name, content, date);
		return item;
	}


	public static Calendar makeTime(int minsAgo) {
		Calendar cal = Calendar.getInstance();
	
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - minsAgo);
	
		return cal;
	
	}
}
