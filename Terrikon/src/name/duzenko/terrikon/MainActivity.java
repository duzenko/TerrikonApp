package name.duzenko.terrikon;

import java.util.ArrayList;

import name.duzenko.terrikon.logic.Gl;
import name.duzenko.terrikon.logic.MatchItem;
import name.duzenko.terrikon.logic.MatchLoader;
import name.duzenko.terrikon.logic.RssItem;
import name.duzenko.terrikon.logic.SwipeListener;
import name.duzenko.terrikon.logic.WebUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity {

	String feedUrl = "";
	ListView rssListView = null;
	GridView gridView;
	RssAdapter newsAdapter;
	MatchAdapter matchAdapter;
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Gl.check(this);

	    tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    tabHost.setup();
        
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Новости");
        tabSpec.setContent(R.id.listViewNews);
        tabHost.addTab(tabSpec);
        
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Матчи");
        tabSpec.setContent(R.id.gridMatches);        
        tabHost.addTab(tabSpec);
        
        rssListView = (ListView) findViewById(R.id.listViewNews);
		rssListView.setOnItemClickListener(listClicker);
		newsAdapter = new RssAdapter(this, R.layout.list_item_news);
		rssListView.setAdapter(newsAdapter);
		
		gridView = (GridView) findViewById(R.id.gridMatches);
		matchAdapter = new MatchAdapter(this, R.layout.list_item_match);
		gridView.setAdapter(matchAdapter);
		gridView.setOnItemClickListener(matchClicker);
		
		refreshRssList();

		SwipeListener sl = new SwipeListener(tabHost);
        gridView.setOnTouchListener(sl);
        rssListView.setOnTouchListener(sl);
	}
	
	AdapterView.OnItemClickListener listClicker = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View view, int index, long arg3) {
			RssItem selectedRssItem = newsAdapter.getItem(index);
			openNewsPage(selectedRssItem.link);
		}
	};
	
	AdapterView.OnItemClickListener matchClicker = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View view, int index, long arg3) {
			MatchItem item = matchAdapter.getItem(index);
			if (item.webUrl.endsWith("online"))
				openNewsPage("http://terrikon.com" + item.webUrl);
			else
				openNewsPage("http://terrikon.com" + item.webUrl + "/online");
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://terrikon.com" + item.webUrl)));
		}
	};
	
	void openNewsPage(String url) {
		/*Editor editor = Gl.sharedPreferences.edit();
		editor.putString("lastUrl", url);
		editor.commit();*/
		startActivity(new Intent(MainActivity.this, DetailedActivity.class).putExtra("url", url));		
	}
	
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	};
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshRssList();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void refreshRssList() {
		new RssLoader().execute();
		new MatchLoader() {
			@Override
			protected void onPostExecute(Void result) {
				MainActivity.this.matchAdapter.clear();
				for (int i = 0; i < newItems.size(); i++) {
					MainActivity.this.matchAdapter.add(newItems.get(i));
				}
			}
		}.execute();
	}
	
	class RssAdapter extends ArrayAdapter<RssItem> {

		public RssAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = View.inflate(MainActivity.this, R.layout.list_item_news, null);
			}
			RssItem item = getItem(position);
			ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
			if (!item.imageUrl.equals(imageView.getTag())) {
				imageView.setTag(item.imageUrl);
				if (item.imageUrl.length()>0) {
					view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
					imageView.setImageBitmap(null);
					new ImageLoader().execute(imageView);
				} else
					imageView.setImageResource(R.drawable.no_image);
			}
			((TextView)view.findViewById(R.id.textDate)).setText(item.dateString);
			((TextView)view.findViewById(R.id.textTitle)).setText(item.title);
			((TextView)view.findViewById(R.id.textDesc)).setText(item.description);
			return view;
		}
		
	}

	class MatchAdapter extends ArrayAdapter<MatchItem> {

		public MatchAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = View.inflate(MainActivity.this, R.layout.list_item_match, null);
			}
			MatchItem item = getItem(position);
			((TextView)view.findViewById(R.id.textHomeTeam)).setText(item.homeTeam);
			((TextView)view.findViewById(R.id.textHomeScore)).setText(item.homeScore);
			((TextView)view.findViewById(R.id.textAwayTeam)).setText(item.awayTeam);
			((TextView)view.findViewById(R.id.textAwayScore)).setText(item.awayScore);
			((TextView)view.findViewById(R.id.textBottom1)).setText(item.status);
			((TextView)view.findViewById(R.id.textBottom2)).setText(item.tv);
			if (item.tv == null) {
				view.findViewById(R.id.image).setVisibility(View.GONE);				
			}
			//view.setBackgroundResource(R.drawable.border);
			GradientDrawable bgShape = (GradientDrawable)view.getBackground();
			if (item.displayClass.equals("play  "))
				bgShape.setColor(Color.WHITE);
			if (item.displayClass.equals("play green "))
				bgShape.setColor(Color.GREEN);
			if (item.displayClass.equals("play dark "))
				bgShape.setColor(Color.LTGRAY);
			return view;
		}
		
	}

	class RssLoader extends AsyncTask<Void, Integer, Void> {

		private ArrayList<RssItem> newItems;
		ProgressDialog dialog = new ProgressDialog(MainActivity.this);		

		@Override
		protected void onPreExecute() {
			dialog.setTitle("Загрузка новостей...");
			dialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			newItems = RssItem.getRssItems("http://terrikon.com/rss/all");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			newsAdapter.clear();
			for (int i = 0; i < newItems.size(); i++) {
				newsAdapter.add(newItems.get(i));
			}
			dialog.dismiss();
		}
		
	}
	
	class ImageLoader extends AsyncTask<ImageView, Integer, Bitmap> {

		Bitmap bitmap;
		private ImageView imageView;
		private String imageUrl;
		
		@Override
		protected Bitmap doInBackground(ImageView... params) {
			imageUrl = (String) params[0].getTag();
			imageView = (ImageView) params[0];
			bitmap = WebUtil.downloadBitmap(imageUrl);
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			((View) imageView.getParent()).findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
			imageView.setImageBitmap(bitmap);
		}

	}
	
}