package name.duzenko.terrikon.logic;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class PostCommentTask extends AsyncTask<String, Void, Void> {

	protected boolean result = false;
	String url;
	
	public PostCommentTask(String url) {
		this.url = url;
	}

	@Override
	protected Void doInBackground(String... params) {
	    HttpPost httpPost = new HttpPost(url);
	    ArrayList<NameValuePair> postParameters;
	    postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("msg", params[0]));
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
	    try {
		    httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			HttpResponse httpResponse = Gl.httpClient.execute(httpPost);

		    if (httpResponse.getLastHeader("Set-Cookie") != null)
		    {

		        //CookieStorage.getInstance().getArrayList().remove(0);
		        //CookieStorage.getInstance().getArrayList().add(httpResponse.getLastHeader("Set-Cookie").getValue());
		    }
		    //Log.i("arrayList",(CookieStorage.getInstance().getArrayList().toString()));
			result = true;

		} catch (Exception e) {
			e.printStackTrace();
		}		
	    return null;
	}
	
}
