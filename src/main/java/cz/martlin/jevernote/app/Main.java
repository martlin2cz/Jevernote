package cz.martlin.jevernote.app;

import java.io.PrintStream;

import cz.martlin.jevernote.dataobj.misc.CommandLineData;

public class Main {

	public static void main(String[] args) {
		args = new String[]{"--base-dir", "/home/martin/jevernote-2", "--debug", "synchronize"};
		
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

	public static void runCommand(String[] args) {
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
		out.println("jevernote -h|--help     (prints help)");
		out.println("jevernote -v|--version  (prints version)");
		out.println();
		out.println("Common flags:");
		out.println(" --base-dir <PATH>      (works with PATH instead of current dir)");
		out.println(" --verbose              (displays info what's been done)");
		out.println(" --debug                (displays far more info, implies --verbose)");
		out.println(" --interactive          (before each change asks for confirm)");
		out.println(" --dry-run              (no changes will be performed, use with --verbose)");
		out.println(" --save                 (if possible, before each item update)");
		out.println();
		out.println("Initialisation commands:");
		out.println("jevernote init <AUTH TOKEN>             (just initializes empty local storage)");
		out.println("jevernote clone <AUTH TOKEN>            (initializes local storage and downloads content)");
		out.println();
		out.println("Synchronisation commands:");
		out.println("jevernote push [--weak|--force]         (pushes data from local to remote)");
		out.println("jevernote pull [--weak|--force]         (pulls data from remote to local)");
		out.println(" --weak                 (only adds, no removing, no overriding changes)");
		out.println(" --force                (adds, removes, and overrides all changes)");
		out.println(" (not specified)        (adds and removes, updates only newer)");
		out.println("jevernote synchronize   (merges changes (adds, not removes, changes keeps newer))");
		out.println("jevernote status                        (just displays differences, use with --verbose)");
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
