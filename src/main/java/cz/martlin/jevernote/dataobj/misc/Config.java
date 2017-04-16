package cz.martlin.jevernote.dataobj.misc;

import java.io.File;
import java.util.Properties;

import cz.martlin.jevernote.misc.FileSystemUtils;
import cz.martlin.jevernote.misc.JevernoteException;

public class Config {
	public static final String CONFIG_FILE_NAME = ".jevernote.properties";

	private String authToken;

	public Config() {

	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	///////////////////////////////////////////////////////////////////////////

	public void load(File baseDir) throws JevernoteException {
		File file = configFile(baseDir);
		Properties props = FileSystemUtils.loadProperties(file);

		fromProperties(props);
	}

	public void save(File baseDir) throws JevernoteException {
		Properties props = new Properties();
		toProperties(props);

		File file = configFile(baseDir);
		FileSystemUtils.saveProperties(file, props);
	}

	public void fromProperties(Properties props) {
		this.setAuthToken(props.getProperty("authToken"));
		// add more here ...
	}

	private void toProperties(Properties props) {
		props.setProperty("authToken", this.getAuthToken());
		// add more here ...
	}

	private File configFile(File baseDir) {
		return new File(baseDir, CONFIG_FILE_NAME);
	}

}
