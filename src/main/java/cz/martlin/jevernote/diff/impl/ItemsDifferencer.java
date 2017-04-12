package cz.martlin.jevernote.diff.impl;

import java.util.Map;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.diff.base.BaseDifferencer;

public class ItemsDifferencer extends BaseDifferencer<Item> {

	private final Map<Package, Package> renamedPackages;

	public ItemsDifferencer(Map<Package, Package> renamedPackages) {
		this.renamedPackages = renamedPackages;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected boolean isRenamed(Item sourceObj, Item targetObj) {
		if (sourceObj == null || targetObj == null) {
			return false;
		}

		if (isInRenamedPackage(sourceObj, targetObj)) {
			// ignore
		} else {
			String sourcePack = sourceObj.getPack().getName();
			String targetPack = targetObj.getPack().getName();

			if (!sourcePack.equals(targetPack)) {
				return true;
			}
		}

		String sourceName = sourceObj.getName();
		String targetName = targetObj.getName();

		if (!sourceName.equals(targetName)) {
			return true;
		}

		return false;
	}

	@Override
	protected boolean isChanged(Item sourceObj, Item targetObj) {
		if (sourceObj == null || targetObj == null) {
			return false;
		}

		String sourceContent = sourceObj.getContent();
		String targetContent = targetObj.getContent();

		return !sourceContent.equals(targetContent);
	}

	@Override
	protected String idOf(Item obj) {
		return obj.getId();
	}

	///////////////////////////////////////////////////////////////////////////

	private boolean isInRenamedPackage(Item sourceObj, Item targetObj) {
		Package sourcePack = sourceObj.getPack();
		Package targetPack = targetObj.getPack();

		Package renamedSource = renamedPackages.get(sourcePack);
		return targetPack.equals(renamedSource);
	}

}
