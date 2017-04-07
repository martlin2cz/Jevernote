package cz.martlin.jevernote.impls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import cz.martlin.jevernote.core.BaseStorage;
import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;

public class EvernoteStorage implements BaseStorage {

	private final NoteStoreClient cli;

	public EvernoteStorage(String token) throws JevernoteException {
		this.cli = createNoteStore(token);
	}

	/**
	 * Connects to the evernote using given auth token.
	 * 
	 * @param token
	 * @return
	 * @throws JevernoteException
	 */
	private NoteStoreClient createNoteStore(String token) throws JevernoteException {
		try {
			// https://github.com/evernote/evernote-sdk-java/blob/master/sample/client/EDAMDemo.java
			EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
			ClientFactory factory = new ClientFactory(evernoteAuth);
			UserStoreClient userStore = factory.createUserStoreClient();

			boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
					com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
					com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);

			if (!versionOk) {
				throw new IllegalArgumentException("Incompatible Evernote client protocol version");
			}

			// Set up the NoteStore client
			NoteStoreClient noteStore = factory.createNoteStoreClient();
			return noteStore;
		} catch (Exception e) {
			throw new JevernoteException("Cannot connec to evernote", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////////

	@Override
	public Map<Package, List<Item>> list() throws JevernoteException {
		Map<Package, List<Item>> result = new HashMap<>();

		List<Package> packages = listPackages();
		for (Package pack : packages) {
			List<Item> items = listItems(pack);
			result.put(pack, items);
		}

		return result;

	}

	@Override
	public List<Package> listPackages() throws JevernoteException {

		List<Notebook> notebooks;
		try {
			notebooks = cli.listNotebooks();
		} catch (Exception e) {
			throw new JevernoteException("Cannot load packages", e);
		}

		List<Package> packages = new ArrayList<>(notebooks.size());

		notebooks.forEach((n) -> packages.add(notebookToPackage(n)));

		return packages;
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {

		NoteFilter filter = new NoteFilter();
		filter.setNotebookGuid(pack.getId());

		NotesMetadataResultSpec resultSpec = new NotesMetadataResultSpec();
		List<NoteMetadata> metadatas;
		try {
			NotesMetadataList notesMetadataList = cli.findNotesMetadata(filter, 0, Integer.MAX_VALUE, resultSpec);
			metadatas = notesMetadataList.getNotes();
		} catch (Exception e) {
			throw new JevernoteException("Cannot load items", e);
		}

		List<Item> items = new ArrayList<>(metadatas.size());

		for (NoteMetadata metadata : metadatas) {
			String guid = metadata.getGuid();
			Note note;
			try {
				note = cli.getNote(guid, true, false, false, false);
			} catch (Exception e) {
				throw new JevernoteException("Cannot load item");
			}
			Item item = noteToItem(note);
			items.add(item);
		}

		return items;
	}

	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		Notebook notebook = packageToNotebook(pack);

		try {
			notebook = cli.createNotebook(notebook);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create package", e);
		}

		String id = notebook.getGuid();
		pack.setId(id);
	}

	@Override
	public void createItem(Package pack, Item item) throws JevernoteException {
		Note note = itemToNote(item);

		String pid = pack.getId();
		note.setNotebookGuid(pid);

		try {
			note = cli.createNote(note);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create item", e);
		}

		String id = note.getGuid();
		item.setId(id);
	}

	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void updatePackage(Package pack) throws JevernoteException {
		Notebook notebook = packageToNotebook(pack);

		try {
			cli.updateNotebook(notebook);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update package", e);
		}
	}

	@Override
	public void updateItem(Item item, Package pack) throws JevernoteException {
		Note note = itemToNote(item);

		if (pack != null) {
			Notebook notebook = packageToNotebook(pack);
			String nid = notebook.getGuid();
			note.setNotebookGuid(nid);
		}

		try {
			cli.updateNote(note);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update item", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////////

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		@SuppressWarnings("unused")
		Notebook notebook = packageToNotebook(pack);

		try {
			throw new UnsupportedOperationException("remove notebook");
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove package", e);
		}

	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		Note note = itemToNote(item);

		try {
			String id = note.getGuid();
			cli.deleteNote(id);
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove item", e);
		}

		item.setId(null);
	}

	///////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the notebook into package.
	 * 
	 * @param notebook
	 * @return
	 */
	private Package notebookToPackage(Notebook notebook) {
		String id = notebook.getGuid();
		String name = notebook.getName();

		return new Package(id, name);
	}

	/**
	 * Converts the note to item.
	 * 
	 * @param note
	 * @return
	 */
	private Item noteToItem(Note note) {
		System.out.println(note);
		String id = note.getGuid();
		String name = note.getTitle();
		String content = note.getContent();

		Calendar lastModifiedAt= Calendar.getInstance();
		lastModifiedAt.setTimeInMillis(note.getUpdated());
		
		return new Item(id, name, content, lastModifiedAt);
	}

	/**
	 * Converts package into notebook.
	 * 
	 * @param pack
	 * @return
	 */
	private Notebook packageToNotebook(Package pack) {
		Notebook notebook = new Notebook();

		String name = pack.getName();
		notebook.setName(name);

		String id = pack.getId();
		notebook.setGuid(id);

		return notebook;
	}

	/**
	 * Converts item to note.
	 * 
	 * @param item
	 * @return
	 */
	private Note itemToNote(Item item) {
		Note note = new Note();

		String name = item.getName();
		note.setTitle(name);

		String id = item.getId();
		note.setGuid(id);

		String content = item.getContent();
		note.setContent(content);

		return note;
	}

}
