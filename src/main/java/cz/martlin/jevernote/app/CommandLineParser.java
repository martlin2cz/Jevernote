package cz.martlin.jevernote.app;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.misc.CommandLineData;

public class CommandLineParser {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public CommandLineParser() {
	}

	public CommandLineData parse(String[] args) {
		Queue<String> params = toQueue(args);
		return parse(params);
	}

	private CommandLineData parse(Queue<String> params) {
		CommandLineData data = new CommandLineData();

		parseGlobalFlags(params, data);

		String command = parseCommand(params);
		if (command == null) {
			return null;
		}

		data.setCommand(command);

		boolean succ = parseCommandFlags(command, params, data);
		if (!succ) {
			return null;
		}

		return data;
	}

	///////////////////////////////////////////////////////////////////////////

	private void parseGlobalFlags(Queue<String> params, CommandLineData data) {
		while (!params.isEmpty()) {

			String next = params.peek();
			if (!next.startsWith("-")) {
				return;
			}

			next = params.poll();
			parseGlobalFlag(next, params, data);
		}
	}

	private void parseGlobalFlag(String next, Queue<String> params, CommandLineData data) {
		switch (next) {
		case "--verbose":
			data.setVerbose(true);
			break;

		case "--debug":
			data.setDebug(true);
			break;

		case "--interactive":
			data.setInteractive(true);
			break;
		case "--safe":
			data.setSave(true);
			break;
		case "--base-dir":
			String dirName = params.poll();
			if (dirName == null) {
				LOG.error("Missing dir name value, ignoring flag");
			} else {
				File baseDir = new File(dirName);
				data.setBaseDir(baseDir);
			}
			break;
		case "--dry-run":
			data.setDryRun(true);
			break;
		default:
			LOG.warn("Unknown flag " + next + ", ignoring");
		}

	}

	private String parseCommand(Queue<String> params) {
		if (params.isEmpty()) {
			LOG.error("Unspecified command");
			return null;
		}

		String command = params.poll();
		switch (command) {
		case "init":
		case "clone":
		case "push":
		case "pull":
		case "synchronize":
		case "status":
			return command;
		case "ad":
		case "mk":
		case "mv":
		case "rm":
			return command;
		case "easter":
			System.out.println("Happy easter!");
			return null;
		default:
			LOG.error("Uknown command: " + command);
			return null;
		}
	}

	private boolean parseCommandFlags(String command, Queue<String> params, CommandLineData data) {
		switch (command) {
		case "init":
		case "clone":
			return parseInitCloneFlags(params, data);
		case "push":
		case "pull":
			return parsePushPullFlags(params, data);
		case "synchronize":
		case "status":
			return params.isEmpty();
		case "ad":
		case "mk":
		case "mv":
		case "rm":
			return parseLocalCommandFlags(command, params, data);
		default:
			throw new IllegalArgumentException("Unknown command:" + command);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	private boolean parseLocalCommandFlags(String command, Queue<String> params, CommandLineData data) {
		if (params.isEmpty()) {
			LOG.error("Missing item or pack");
			return false;
		}
		String firstItemOrPack = params.poll();
		data.setFirstItemOrPack(firstItemOrPack);
		switch (command) {
		case "mk":
			if (!params.isEmpty()) {
				String content = params.poll();
				data.setInitialText(content);
			}
			if (!params.isEmpty()) {
				LOG.warn("Uneccessary params after intial content, ignoring");
			}
			return true;
		case "mv":
			if (params.isEmpty()) {
				LOG.error("Missing second item or pack");
				return false;
			}
			String secondItemOrPack = params.poll();
			data.setSecondItemOrPack(secondItemOrPack);
			
			if (!params.isEmpty()) {
				LOG.warn("Uneccessary params after second item/pack, ignoring");
			}
			return true;
		case "ad":
		case "rm":
			if (!params.isEmpty()) {
				LOG.warn("Uneccessary params after item/pack, ignoring");
			}
			return true;
		default:
			throw new IllegalArgumentException("Unknown local command:" + command);
		}
	}

	private boolean parseInitCloneFlags(Queue<String> params, CommandLineData data) {
		if (params.size() < 1) {
			LOG.error("Missing remote token");
			return false;
		}

		if (params.size() > 1) {
			LOG.warn("Uneccessary params after remote token");
		}

		String token = params.poll();
		data.setRemoteToken(token);

		return true;
	}

	private boolean parsePushPullFlags(Queue<String> params, CommandLineData data) {
		while (!params.isEmpty()) {
			String next = params.poll();
			switch (next) {
			case "--weak":
				data.setWeak(true);
				break;
			case "--force":
				data.setForce(true);
				break;
			default:
				LOG.warn("Unknown flag " + next);
				break;
			}
		}

		return true;
	}

	///////////////////////////////////////////////////////////////////////////

	private LinkedList<String> toQueue(String[] args) {
		return new LinkedList<String>(Arrays.asList(args));
	}

}
