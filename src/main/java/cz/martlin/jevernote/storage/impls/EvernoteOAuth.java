package cz.martlin.jevernote.storage.impls;

import java.io.ByteArrayOutputStream;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.EvernoteApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;

/**
 * https://github.com/evernote/evernote-sdk-java/blob/master/sample/oauth/src/
 * main/webapp/index.jsp
 * 
 * @author martin
 *
 */
public class EvernoteOAuth {
	private static final String CONSUMER_KEY = "martin2cz";
	private static final String CONSUMER_SECRET = "56953dbb615ea66b";
	
	private final EvernoteService EVERNOTE_SERVICE = EvernoteService.SANDBOX;
	private final String CALLBACK = "http://mrs-martlins.rhcloud.com/ws/echo/get-request-body";
	
	private OAuthService service;	//TODO make local var
	private ByteArrayOutputStream baos;

	public EvernoteOAuth() {
	}

	public String authorise() {
		service = initialize();

		Token requestToken = getRequestToken();

		openBrowserAndWait(requestToken);

		String token = getAuthorisationToken(requestToken);
		
		return token;
			
	}

	private OAuthService initialize() {
		Class<? extends EvernoteApi> providerClass = EvernoteApi.Sandbox.class;
		if (EVERNOTE_SERVICE == EvernoteService.PRODUCTION) {
			providerClass = org.scribe.builder.api.EvernoteApi.class;
		}

		baos = new ByteArrayOutputStream(); // XXX just for debugging?

		OAuthService service = new ServiceBuilder() //
				.debugStream(baos) //
				.provider(providerClass) //
				.apiKey(CONSUMER_KEY) //
				.apiSecret(CONSUMER_SECRET) //
				.callback(CALLBACK) //
				.build(); //

		log();

		return service;
	}

	private Token getRequestToken() {
		try {
			return service.getRequestToken();
		} catch (Exception e) {
			log();
			throw e;
		}
	}

	private void openBrowserAndWait(Token requestToken) {
		String url = "https://sandbox.evernote.com/OAuth.action?oauth_token=" + requestToken.getToken();
		// TODO sandbox -> prod
		System.out.println("Open " + url + " and confirm, press enter");

		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		scan.close();
	}

	private String getAuthorisationToken(Token requestToken) {
		String verifier = requestToken.getToken();
		Verifier scribeVerifier = new Verifier(verifier);

		Token scribeAccessToken;
		try {
			scribeAccessToken = service.getAccessToken(requestToken, scribeVerifier);
		} catch (Exception e) {
			log();
			throw e;
		}

		EvernoteAuth evernoteAuth = EvernoteAuth.parseOAuthResponse(EVERNOTE_SERVICE,
				scribeAccessToken.getRawResponse());

		System.err.println("note store url: " + evernoteAuth.getNoteStoreUrl());
		log();

		return evernoteAuth.getToken();
	}

	private void log() {
		System.err.println(new String(baos.toByteArray()));
	}
}
