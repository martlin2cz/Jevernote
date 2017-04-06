package cz.martlin.jevernote.impl;

import java.util.List;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.type.Notebook;

public class EvernoteWrapper {

	private final NoteStoreClient cli;

	public EvernoteWrapper(String token) throws JevernoteException {
		this.cli = createNoteStore(token);
	}

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

	public List<Package> listPackages() throws JevernoteException  {

		List<Notebook> notebooks;
		try {
			notebooks = cli.listNotebooks();
		} catch (Exception e) {
			throw new JevernoteException("Cannot load packages",e);
		}

		for (Notebook notebook : notebooks) {
			System.out.println("Notebook: " + notebook.getName());
			// TODO ...
		}

		return null;	//TODO
	}

}
