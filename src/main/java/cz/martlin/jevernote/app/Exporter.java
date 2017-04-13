package cz.martlin.jevernote.app;

import java.util.List;
import java.util.function.Function;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;

public class Exporter {

	public Exporter() {
	}

	public String exportDiff(StoragesDifference diff) {

		String packages = exportChangesList(diff.getPackageDifferences(), (p) -> exportPackage(p));
		String items = exportChangesList(diff.getItemsDifferences(), (p) -> exportItem(p));

		return packages + items;

	}

	private <T> String exportChangesList(List<Change<T>> packageDifferences, Function<T, String> stringer) {

		StringBuilder stb = new StringBuilder();

		packageDifferences.stream().forEach((c) -> {
			String exported = exportChange(c, stringer);
			stb.append('\t');
			stb.append(exported);
			stb.append('\n');
		});

		return stb.toString();
	}

	private <T> String exportChange(Change<T> change, Function<T, String> stringer) {
		switch (change.getType()) {
		case CREATE:
			return "create " + stringer.apply(change.getFirst());
		case DELETE:
			return "delete " + stringer.apply(change.getFirst());
		case RENAME:
			return "rename " + stringer.apply(change.getFirst()) + " -> " + stringer.apply(change.getSecond());
		case UPDATE:
			return "update " + stringer.apply(change.getFirst());
		default:
			throw new IllegalArgumentException("Unknown change type");
		}
	}

	private String exportPackage(Package pack) {
		return pack.getName();
	}

	private String exportItem(Item item) {
		return item.getPack().getName() + "/" + item.getName();
	}

}
