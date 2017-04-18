package cz.martlin.jevernote.strategy.impl;

import java.util.Scanner;
import java.util.regex.Pattern;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.strategy.base.BaseDifferencePerformStrategy;

public class InteractiveStrategy implements BaseDifferencePerformStrategy {

	private static final char YES_CHAR = 'y';
	private static final char NO_CHAR = 'n';

	private final String desc;
	private final Scanner scan;

	public InteractiveStrategy(String desc) {
		this.desc = desc;
		this.scan = new Scanner(System.in);
	}

	@Override
	public boolean performCreatePackage(Package pack) {
		return ask("Create", pack);
	}

	@Override
	public boolean performRenamePackage(Package oldPack, Package newPack) {
		return ask("Rename", oldPack, newPack);
	}

	@Override
	public boolean performDeletePackage(Package pack) {
		return ask("Delete", pack);
	}

	@Override
	public boolean performCreateItem(Item item) {
		return ask("Create", item);
	}

	@Override
	public boolean performRenameItem(Item oldItem, Item newItem) {
		return ask("Rename", oldItem, newItem);
	}

	@Override
	public boolean performUpdateItem(Item oldItem, Item newItem) {
		return ask("Update", oldItem);
	}

	@Override
	public boolean performRemoveItem(Item item) {
		return ask("Remove", item);
	}

	///////////////////////////////////////////////////////////////////////////

	private boolean ask(String operation, Package pack) {
		return ask(operation + " package " + pack.getName());
	}

	private boolean ask(String operation, Package oldPack, Package newPack) {
		return ask(operation + " package " + oldPack.getName() + " to " + newPack.getName());
	}

	private boolean ask(String operation, Item item) {
		return ask(operation + " item " + item.getName());
	}

	private boolean ask(String operation, Item oldItem, Item newItem) {
		return ask(operation + " item " + oldItem.getName() + " to " + newItem.getName());
	}

	private boolean ask(String operation) {
		String msg = operation + " (" + desc + ") " + "(" + YES_CHAR + "/" + NO_CHAR + ")" + ": ";

		return readBoolean(msg);
	}

	private boolean readBoolean(String prompt) {
		String regex = "[" + YES_CHAR + NO_CHAR + "]";
		String read = readPattern(prompt, regex);

		if (read == null) {
			System.out.println(NO_CHAR);
			return false;
		}

		char choose = read.toLowerCase().charAt(0);
		switch (choose) {
		case YES_CHAR:
			return true;
		case NO_CHAR:
			return false;
		default:
			throw new IllegalArgumentException();
		}
	}

	private String readPattern(String prompt, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		System.out.print(prompt);
		while (scan.hasNext()) {

			if (scan.hasNext(pattern)) {
				return scan.next();
			} else {
				System.out.print(prompt);
				scan.next();
			}
		}

		return null;
	}

}
