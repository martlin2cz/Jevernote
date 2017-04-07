package cz.martlin.jevernote.app;

import java.io.PrintStream;

public class Main {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println("TODO ...");
	}
	
	
	public void printUsage(PrintStream out) {
		out.println("Create item locally:");
		out.println("jevernote mk <ITEM or PACKAGE name> <initial content if ITEM>");
		out.println("jevernote mv <old ITEM or PACKAGE name> <new ITEM or PACKAGE name>");
		out.println("jevernote rm <ITEM or PACKAGE name>");
	}

}
