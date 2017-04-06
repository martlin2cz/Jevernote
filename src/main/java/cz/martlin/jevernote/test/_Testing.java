package cz.martlin.jevernote.test;

import cz.martlin.jevernote.impl.EvernoteWrapper;
import cz.martlin.jevernote.impl.JevernoteException;

public class _Testing {

	public static void main(String[] args) {
		// TODO 
		testEvernote();

	}
	
	private static void testEvernote() {
		final String token = "xxxx";
		
		try {
		EvernoteWrapper evernote = new EvernoteWrapper(token);

		evernote.listPackages();
		
		} catch (JevernoteException e) {
			e.printStackTrace();
		}
	}

}
