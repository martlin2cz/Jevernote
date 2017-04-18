package cz.martlin.jevernote.dataobj.misc;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import cz.martlin.jevernote.misc.FileSystemUtils;

public class Config {
	public static final String CONFIG_FILE_NAME = ".jevernote.properties";
	private static final String COMMENT = "jevernote configuration file";

	private String authToken;
	// add more here ...

	public Config() {
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	// add more here ...

	///////////////////////////////////////////////////////////////////////////

	public boolean existsConfigFile(File baseDir) {
		File configFile = configFile(baseDir);
		return configFile.isFile();
	}

	public void load(File baseDir) throws IOException {
		File file = configFile(baseDir);
		Properties props = FileSystemUtils.loadProperties(file);

		fromProperties(props);
	}

	public void save(File baseDir) throws IOException {
		Properties props = new Properties();
		toProperties(props);

		File file = configFile(baseDir);
		FileSystemUtils.saveProperties(file, props, COMMENT);
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
