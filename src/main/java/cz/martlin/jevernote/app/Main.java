package cz.martlin.jevernote.app;

import java.io.PrintStream;

import cz.martlin.jevernote.dataobj.misc.CommandLineData;

public class Main {

	public static void main(String[] args) {
		boolean yep = checkHelpAndVersion(args);
		if (yep) {
			return;
		}

		runCommand(args);
	}

	private static boolean checkHelpAndVersion(String[] args) {

		if (args.length == 1) {
			String arg = args[0];
			if ("-h".equals(arg) || "--help".equals(arg)) {
				printHelp(System.out);
				System.exit(0);
				return true;
			}
			if ("-v".equals(arg) || "--version".equals(arg)) {
				printVersion(System.out);
				System.exit(0);
				return true;
			}
		}

		return false;
	}

	private static void runCommand(String[] args) {
		CommandLineParser parser = new CommandLineParser();

		CommandLineData data = parser.parse(args);
		if (data == null) {
			System.exit(1);
			return;
		}

		ConsoleDataProcessor performer = new ConsoleDataProcessor();
		boolean succ = performer.process(data);
		
		if (!succ) {
			System.exit(2);
			return;
		}
	}

	private static void printVersion(PrintStream out) {
		out.println("jevernote " + tryInferVersion());
		out.println("made by m@rtlin, 6.4.2017 - 16.4.2017");

	}

	private static void printHelp(PrintStream out) {
		out.println("jevernote " + tryInferVersion());
		out.println("Console client for evernote written in Java, with git-like usage");
		out.println();
		printUsage(out);
	}

	public static void printUsage(PrintStream out) {
		out.println("Usage:");
		out.println("jevernote [COMMON FLAGS] <COMMAND> [PARAMS]");
		out.println("jevernote -h|--help");
		out.println("jevernote -v|--version");
		out.println();
		out.println("Common flags:");
		out.println(" --verbose");
		out.println(" --debug");
		out.println(" --interactive");
		out.println();
		out.println("Initialisation commands:");
		out.println("jevernote init <AUTH TOKEN>");
		out.println("jevernote clone <AUTH TOKEN>");
		out.println();
		out.println("Operations:");
		out.println("jevernote push [--weak|--force]");
		out.println("jevernote pull [--weak|--force]");
		out.println("jevernote synchronize local|remote");
		out.println("jevernote status");
		out.println();
		out.println("Working with local:");
		out.println("jevernote ad <ITEM or PACK>              (adds existing item or package to index)");
		out.println("jevernote mk <ITEM> [<initial content>]  (creates item)");
		out.println("jevernote mk <PACK>                      (creates package)");
		out.println("jevernote mv <old ITEM or PACK> <new ITEM or PACK>        (moves/renames item or package)");
		out.println("jevernote rm <ITEM or PACK>              (removes item or package)");
		out.println();
	}

	private static String tryInferVersion() {
		String version = Main.class.getPackage().getImplementationVersion();

		if (version != null) {
			return version;
		} else {
			return "1.0?";
		}
	}

}
