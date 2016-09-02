package name.duzenko.terrikon.logic;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;

public class Gl {
	
	public static SharedPreferences sharedPreferences;
	public static CookieStore cookieStore = new BasicCookieStore();
	public static DefaultHttpClient httpClient = new DefaultHttpClient();
	public static Comment commentToAnswer;
	
	static double screenInches;
	
	public static void check(Activity context) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(null, Context.MODE_PRIVATE);
		    httpClient.setCookieStore(cookieStore);
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			double x = Math.pow(dm.widthPixels/dm.xdpi,2);
			double y = Math.pow(dm.heightPixels/dm.ydpi,2);
			screenInches = Math.sqrt(x+y);
		}
	}
	
	public static int getScaling() {
		return sharedPreferences.getInt("scaling", (int) (60*Math.sqrt(screenInches)));
	}
	
	public static void setScaling(int value) {
		Editor editor = sharedPreferences.edit();
		editor.putInt("scaling", value);
		editor.commit();
	}

}
