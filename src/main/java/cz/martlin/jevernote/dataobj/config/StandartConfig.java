package cz.martlin.jevernote.dataobj.config;

import cz.martlin.jevernote.misc.JevernoteException;

public class StandartConfig {

	private String authToken;

	public StandartConfig() {

	}

	public String getAuthToken() {
		return authToken;
	}

	public static StandartConfig load() throws JevernoteException {
		// TODO
		return new StandartConfig();
	}

	public static void save(StandartConfig config) throws JevernoteException {
		// TODO
	}

}
