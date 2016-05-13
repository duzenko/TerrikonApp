package name.duzenko.terrikon;

import java.util.ArrayList;

import name.duzenko.terrikon.logic.AnimatedTabHostListener;
import name.duzenko.terrikon.logic.CommentsAdapter;
import name.duzenko.terrikon.logic.CommentsLoader;
import name.duzenko.terrikon.logic.Gl;
import name.duzenko.terrikon.logic.HtmlLoader;
import name.duzenko.terrikon.logic.SwipeListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class DetailedActivity extends Activity {

	public WebView webView;
	TabHost tabHost;
	ListView listView;
	final String tabText = "Комментарии Террикона";
	GridView gridPages;
	PagesAdapter pagesAdapter;
	public boolean online;
	
	@SuppressLint("NewApi")
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_detail);
		Gl.check(this);

		url = getIntent().getStringExtra("url");//Gl.sharedPreferences.getString("lastUrl", null);
	    if (url == null) {
	    	Toast.makeText(this, "Внутренняя ошибка", Toast.LENGTH_SHORT).show();
	    	finish();
	    	return;
	    }
		online = url.contains("/matches/");

	    tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    tabHost.setup();
        
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Материал");
        tabSpec.setContent(R.id.webView);
        tabHost.addTab(tabSpec);
        
		webView = (WebView) findViewById(R.id.webView);
		gridPages = (GridView) findViewById(R.id.gridPages);
		pagesAdapter = new PagesAdapter();

		gridPages.setAdapter(pagesAdapter);

		tabSpec = tabHost.newTabSpec("tag2");
		tabSpec.setIndicator(tabText);
		tabSpec.setContent(R.id.tab2);
		tabHost.addTab(tabSpec);
		SwipeListener sl = new SwipeListener(tabHost);
		tabHost.setOnTouchListener(sl);
		tabHost.setOnTabChangedListener(new AnimatedTabHostListener(tabHost));

		webView.setOnTouchListener(sl);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnTouchListener(sl);
		listView.setDividerHeight(9);
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(commentClickListener);

//		webView.setHorizontalScrollBarEnabled(false);
        
	    final float scale = getResources().getDisplayMetrics().xdpi;
	    if (android.os.Build.VERSION.SDK_INT >= 14) 
	    	webView.getSettings().setTextZoom((int) (30000/scale));
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.setWebViewClient(webClient);
	    new HtmlLoader(this).execute();
	    if (!online)
	    	new AwareCommentsLoader().execute(); 
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
	
	OnClickListener pageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Button button = (Button) v;
			int newPage = Integer.valueOf(button.getText().toString());
			if (page != newPage)
				listView.smoothScrollToPosition(0);
			page = newPage;
			new AwareCommentsLoader().execute(); 
		}
	};
	
	OnItemLongClickListener commentClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Gl.commentToAnswer = adapter.comments.get(position);
			postComment();
			return true;
		}
		
	};
	
	class PagesAdapter extends BaseAdapter {
		
		protected ArrayList<Integer> pages = new ArrayList<Integer>();
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
    		if (convertView == null)
    			convertView = View.inflate(getBaseContext(), R.layout.grid_item_page, null);
    		Button button = (Button)convertView; 
    		button.setText(getItem(position).toString());
    		button.setOnClickListener(pageClickListener);
    		return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			return pages.get(position);
		}
		
		@Override
		public int getCount() {
			return pages.size();
		}
	}
	
	public String url;
	
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
			new AwareCommentsLoader().execute(); 
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
			startActivityForResult(new Intent(this, PostActivity.class).putExtra("url", url.replace("posts", "comments") + "/add"), 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		new AwareCommentsLoader().execute(); 
	}

	CommentsAdapter adapter = new CommentsAdapter(DetailedActivity.this);
	
	boolean commentsLoading = false;
	
	public class AwareCommentsLoader extends CommentsLoader {
		
		public AwareCommentsLoader() {
			this.page = DetailedActivity.this.page;
			if (online)
				url = commentsUrl;
			else
				this.url = DetailedActivity.this.url;
			pagesPanel = (LinearLayout) DetailedActivity.this.findViewById(R.id.pagePanel);
			//btn1 = (Button) DetailedActivity.this.findViewById(R.id.button1);
			//btn2 = (Button) DetailedActivity.this.findViewById(R.id.button2);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pagesPanel.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pagesPanel.setVisibility(View.VISIBLE);
			pagesAdapter.pages = this.pages;
			pagesAdapter.notifyDataSetChanged();
			if (html == null && DetailedActivity.this.page > 1)
				Toast.makeText(DetailedActivity.this, "Нет интернета?", Toast.LENGTH_LONG).show();
			adapter.comments = this.data;
			adapter.notifyDataSetChanged();
			((TextView)DetailedActivity.this.findViewById(R.id.textPage)).setText("№" + DetailedActivity.this.page);
			TextView textView = (TextView)((ViewGroup)tabHost.getTabWidget().getChildAt(1)).getChildAt(1);
			if (page == pages.size())
				textView.setText(tabText + " (" + (int)((this.pages.size()-1)*20 + adapter.getCount()) + ")");
			else
				if (pages.size() != 0)
					textView.setText(tabText + " (" + (this.pages.size()-1)*20 + "+)");
				else
					textView.setText(tabText + " (0)");
		}
		
	}
	
	public String commentsUrl;
	
}
