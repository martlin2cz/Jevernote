package cz.martlin.jevernote.misc;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import cz.martlin.jevernote.storage.impls.FSSWIUsingProperties;

public class FileSystemUtils {

	private FileSystemUtils() {
	}

	///////////////////////////////////////////////////////////////////////////

	public static void writeToFile(String content, File file) throws IOException {
		Path path = file.toPath();
		byte[] bytes = content.getBytes();

		Files.write(path, bytes);
	}

	public static String readFile(File file) throws IOException {
		Path path = file.toPath();

		byte[] bytes = Files.readAllBytes(path);

		return new String(bytes);

	}

	public static List<String> loadLines(File file) throws IOException {
		Path path = file.toPath();

		return Files.readAllLines(path);
	}

	///////////////////////////////////////////////////////////////////////////

	public static Properties loadProperties(File file) throws JevernoteException {

		Properties props = new Properties();

		Reader r = null;
		try {
			r = new FileReader(file);
			props.load(r);
		} catch (IOException e) {
			throw new JevernoteException("Cannot read index file", e);
		} finally {
			FileSystemUtils.closeQuietly(r);
		}

		return props;
	}

	public static void saveProperties(File file, Properties props) throws JevernoteException {

		Writer w = null;
		try {
			w = new FileWriter(file);
			props.store(w, FSSWIUsingProperties.COMMENT);
		} catch (IOException e) {
			throw new JevernoteException("Cannot write index file", e);
		} finally {
			FileSystemUtils.closeQuietly(w);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore, or not?
			}
		}
	}

	public static void deleteRecursive(File file) throws IOException {
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				deleteRecursive(sub);
			}
		}

		Files.delete(file.toPath());
	}
}