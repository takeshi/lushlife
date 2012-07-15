package common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TW {
	public static void main(String args[]) throws Exception {
		// このファクトリインスタンスは再利用可能でスレッドセーフです
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("y1pqieH6ikgj3vEE0MuACw",
				"DN2V5CdN4G8MM5eLPI6Klrnn1Yk38VvqzdJEx24ogic");
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out
					.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				// if (pin.length() > 0)
				// {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);

				// } else {
				// accessToken = twitter.getOAuthAccessToken();
				// }
			} catch (TwitterException te) {
				te.printStackTrace();
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// 将来の参照用に accessToken を永続化する
		storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
		Status status = twitter.updateStatus("konkicci");
		System.out.println("Successfully updated the status to ["
				+ status.getText() + "].");
		System.exit(0);
	}

	private static void storeAccessToken(long l, AccessToken accessToken) {
		System.out.println(l);
		System.out.println(accessToken.getToken());
		System.out.println(accessToken.getTokenSecret());
	}
}
