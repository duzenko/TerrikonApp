package name.duzenko.terrikon.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WebUtil {
	
	final static DefaultHttpClient client = new DefaultHttpClient();
	
	public static String downloadHttp(String url) {
    	HttpEntity entity = openHttpStream(url);
    	if (entity == null)
    		return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
			entity.writeTo(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return out.toString();
	}
	
	public static HttpEntity openHttpStream(String url) {
    	//System.out.println(url);
        final HttpGet getRequest = new HttpGet(url);
        try {

            HttpResponse response = client.execute(getRequest);

            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving data from " + url);
                return null;

            }

            return response.getEntity();
        } catch (Exception e) {
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving data from " + url + e.toString());
        }
        return null;
	}

    public static Bitmap downloadBitmap(String url) {
    	InputStream inputStream = null;
		try {
			inputStream = openHttpStream(url).getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    	return bitmap;
    }
    
}
