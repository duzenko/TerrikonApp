package name.duzenko.terrikon;

import name.duzenko.terrikon.logic.CommentsAdapter;
import name.duzenko.terrikon.logic.Gl;
import name.duzenko.terrikon.logic.HtmlLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class DetailedActivity extends Activity {

	public WebView webView;
	public boolean online;
	
	public String pageurl;
	
	@SuppressLint("NewApi")
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_detail);
		Gl.check(this);

		pageurl = getIntent().getStringExtra("url");//Gl.sharedPreferences.getString("lastUrl", null);
	    if (pageurl == null) {
	    	Toast.makeText(this, "¬нутренн€€ ошибка", Toast.LENGTH_SHORT).show();
	    	finish();
	    	return;
	    }
		online = pageurl.contains("/matches/");

		webView = (WebView) findViewById(R.id.webView);
		
	    if (android.os.Build.VERSION.SDK_INT >= 14) 
	    	webView.getSettings().setTextZoom(Gl.getScaling());
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.setWebViewClient(webClient);
	    new HtmlLoader(this).execute();
	}
	
	WebViewClient webClient = new WebViewClient() {
		
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println(url);
			if (url.startsWith("http://terrikon.com/posts")) { 
				startActivity(new Intent(DetailedActivity.this, DetailedActivity.class).putExtra("url", url));		
				return true;
			}
//			if (url.startsWith("http://terrikon.oll.tv")) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
				return true;
//			}
//			return false;
		};
		
	};
	
	OnItemLongClickListener commentClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Gl.commentToAnswer = adapter.comments.get(position);
			postComment();
			return true;
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public int page = 1;
	
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.detail, menu);
		menu.findItem(R.id.action_refresh).setVisible(online);
		return true;
	};
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_post:
			Gl.commentToAnswer = null;
			postComment();
			break;
		case R.id.action_refresh:
			new HtmlLoader(this).execute();
//			new AwareCommentsLoader().execute(); 
			break;
		case R.id.action_zoomin:
			Gl.setScaling(Gl.getScaling() + 2);
		    if (android.os.Build.VERSION.SDK_INT >= 14) 
		    	webView.getSettings().setTextZoom(Gl.getScaling());
			break;
		case R.id.action_zoomout:
			Gl.setScaling(Gl.getScaling() - 2);
		    if (android.os.Build.VERSION.SDK_INT >= 14) 
		    	webView.getSettings().setTextZoom(Gl.getScaling());
			break;
		case R.id.action_full:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pageurl));
			startActivity(browserIntent);
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void postComment() {
		if (online)
			startActivityForResult(new Intent(this, PostActivity.class).putExtra("url", commentsUrl + "/add"), 0);
		else
			startActivityForResult(new Intent(this, PostActivity.class).putExtra("url", pageurl.replace("posts", "comments") + "/add"), 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	CommentsAdapter adapter = new CommentsAdapter(DetailedActivity.this);
	
	boolean commentsLoading = false;
	

	
	public String commentsUrl;
	
}
