package name.duzenko.terrikon.logic;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class LoginTask extends AsyncTask<Void, Void, Void> {

	protected boolean result = false;
	
	String login, password;
	
	public LoginTask(String string, String string2) {
		login = string;
		password = string2;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Gl.cookieStore.clear();
	    HttpPost httpPost = new HttpPost("http://terrikon.com/user/login");
	    ArrayList<NameValuePair> postParameters;
	    postParameters = new ArrayList<NameValuePair>();
	    postParameters.add(new BasicNameValuePair("username", login));
	    postParameters.add(new BasicNameValuePair("password", password));
	    try {
		    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
			HttpResponse httpResponse = Gl.httpClient.execute(httpPost);
//			System.out.println(response);
		    //Header[] head = httpResponse.getAllHeaders();
//		    System.out.println(head);

		    if (httpResponse.getLastHeader("Set-Cookie")!=null)
		    {

		        //CookieStorage.getInstance().getArrayList().remove(0);
		        //CookieStorage.getInstance().getArrayList().add(httpResponse.getLastHeader("Set-Cookie").getValue());
		    }
		    //Log.i("arrayList", (CookieStorage.getInstance().getArrayList().toString()));
			result = Gl.cookieStore.getCookies().size() > 1;

		    //HttpEntity httpEntity = httpResponse.getEntity();
		    //InputStream is = httpEntity.getContent();

		} catch (Exception e) {
			e.printStackTrace();
		}		
	    return null;
	}
	
}
