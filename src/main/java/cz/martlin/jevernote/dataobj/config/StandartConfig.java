package cz.martlin.jevernote.dataobj.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import cz.martlin.jevernote.misc.JevernoteException;

public class StandartConfig {
	public static final String CONFIG_FILE_NAME = ".jevernote.properties";

	private String authToken;

	public StandartConfig() {

	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public static StandartConfig load() throws JevernoteException {
		// TODO basepath

		File file = new File(CONFIG_FILE_NAME);

		StandartConfig config = new StandartConfig();

		String token;
		try {
			// TODO properties
			token = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			throw new JevernoteException(e);
		}
		config.setAuthToken(token);

		return config;
	}

	public static void save(StandartConfig config) throws JevernoteException {
		// TODO basepath

		File file = new File(CONFIG_FILE_NAME);

		String token = config.getAuthToken();
		try {
			// TODO properties
			Files.write(file.toPath(), token.getBytes());
		} catch (IOException e) {
			throw new JevernoteException(e);
		}
	}

}
