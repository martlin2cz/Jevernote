package cz.martlin.jevernote.test;

import java.util.List;

import cz.martlin.jevernote.core.BaseStorage;
import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;
import cz.martlin.jevernote.impls.EvernoteStorage;

public class _Testing {

	public static void main(String[] args) {
		// TODO
		testEvernote();

	}

	private static void testEvernote() {
		final String token = "xxxxx";

		try {
			BaseStorage evernote = new EvernoteStorage(token);
/*
			// create package
			String name1 = "Můj třetí noteboočík";
			Package pack1 = new Package(null, name1);
			evernote.createPackage(pack1);
			System.out.println("Created pack: " + pack1);
*/
			// list packages
			List<Package> packs2 = evernote.listPackages();
			System.out.println("List packs: " + packs2);
			Package p2 = packs2.get(0);
/*
			// create item
			String name3 = "Moje třetí poznámka";
			String content3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
			        + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">" //
			        + "<en-note>" //
			        + "<span style=\"color:green;\">Ahoj, musíš toho udělat ještě hodně!</span><br/>" //
			        + "</en-note>";  //
			Item item3 = new Item(null, name3, content3);
			evernote.createItem(p2, item3);
			System.out.println("Created item: " + item3);
*/
			
			// list items
			List<Item> items4 = evernote.listItems(p2);
			System.out.println("List items: " + items4);
/*
			// update package
			String name5 = "Můj fakt třetí notebočík";
			pack1.setName(name5);
			evernote.updatePackage(pack1);
			System.out.println("Updated pack: " + pack1);
			
			// update item
			String name6 = "Moje fakt opravdu třetí poznámka";
			item3.setName(name6);
			evernote.updateItem(item3);
			System.out.println("Updated item: " + item3);
			
			// remove item
			evernote.removeItem(item3);
			System.out.println("Removed item.");
			
			// remove package
			//evernote.removePackage(pack1);
			System.out.println("Removed package. (not implemented!)");
	*/
		} catch (JevernoteException e) {
			e.printStackTrace();
		}
	}

}
