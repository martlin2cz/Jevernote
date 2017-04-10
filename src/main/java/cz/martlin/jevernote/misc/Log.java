package cz.martlin.jevernote.misc;

public class Log {
	
	private static boolean verbose = false;
	
	private Log() {
	}
	
	
	public static void warn(String what) {
		System.err.println(what);
	}


	public static void write(String what) {
		System.out.println(what);
	}
	
	
	
	

}
