package name.duzenko.terrikon.logic;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;

public class Gl {
	
	public static SharedPreferences sharedPreferences;
	public static CookieStore cookieStore = new BasicCookieStore();
	public static DefaultHttpClient httpClient = new DefaultHttpClient();
	public static Comment commentToAnswer;
	
	public static void check(Context context) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(null, Context.MODE_PRIVATE);
		    httpClient.setCookieStore(cookieStore);
		}
	}

}
