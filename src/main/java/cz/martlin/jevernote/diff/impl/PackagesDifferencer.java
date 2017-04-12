package cz.martlin.jevernote.diff.impl;

import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.diff.base.BaseDifferencer;

public class PackagesDifferencer extends BaseDifferencer<Package> {

	public PackagesDifferencer() {
	}

	@Override
	protected boolean isRenamed(Package sourceObj, Package targetObj) {
		if (sourceObj == null || targetObj == null) {
			return false;
		}
		
		String sourceName = sourceObj.getName();
		String targetName = targetObj.getName();

		return !sourceName.equals(targetName);
	}

	@Override
	protected boolean isChanged(Package sourceObj, Package targetObj) {
		return false; // package never changes!
	}

	@Override
	protected String idOf(Package obj) {
		return obj.getId();
	}

}
