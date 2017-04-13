package cz.martlin.jevernote.app;

import java.io.PrintStream;

public class Main {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println("TODO ...");
	}
	
	
	public void printUsage(PrintStream out) {
		out.println("Starting:");
		out.println("jevernote init <AUTH TOKEN>");
		out.println("jevernote clone <AUTH TOKEN>");
		
		out.println("Synchronisation:");
		out.println("jevernote push [--weak|--force]");
		out.println("jevernote pull [--weak|--force]");
		out.println("jevernote ... ?");	//TODO sync?
		
		
		out.println("Working with local:");
		out.println("jevernote ad <ITEM or PACK>              (adds existing item or package to index)");
		out.println("jevernote mk <ITEM> [<initial content>]  (creates item)");
		out.println("jevernote mk <PACK>                      (creates package)");
		out.println("jevernote mv <old ITEM or PACK> <new ITEM or PACK>        (moves/renames item or package)");
		out.println("jevernote rm <ITEM or PACK>              (removes item or package)");
		
	}

}
