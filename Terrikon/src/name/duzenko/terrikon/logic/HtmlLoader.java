package name.duzenko.terrikon.logic;

import name.duzenko.terrikon.DetailedActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class HtmlLoader extends AsyncTask<Void, Integer, Void> {

	/**
	 * 
	 */
	private final DetailedActivity detailedActivity;

	/**
	 * @param detailedActivity
	 */
	public HtmlLoader(DetailedActivity detailedActivity) {
		this.detailedActivity = detailedActivity;
		dialog = new ProgressDialog(this.detailedActivity);
		dialog.setCancelable(false);
	}

	ProgressDialog dialog;
	private String data;	
	boolean empty;

	@Override
	protected void onPreExecute() {
		dialog.setTitle("Загрузка новости...");
		dialog.show();
	}
	
	static final String newsMarkerStart = "<div class=\"news-head\">", newsMarkerTags = "<div class=\"tag-string\">", newsMarkerEnd = "<div style=\"width:",
			onlineMarkerStart = "<div class=\"game-lineups\">", onlineMarkerEnd = "<div class=\"col right\">",
			divScore = "<div class=\"txt\">"; 
	@Override
	protected Void doInBackground(Void... params) {
		data = WebUtil.downloadHttp(this.detailedActivity.url);
		if (data == null)
			return null;
		if (this.detailedActivity.online) {
			int i = data.indexOf("<a href=\"/comments/") + 10, j = data.indexOf("\">", i);
			if (i > 10 && j > i) {
				this.detailedActivity.commentsUrl = "http://terrikon.com/" + data.substring(i, j);
				this.detailedActivity.new AwareCommentsLoader().execute();
			}
		}
		int mainStart = data.indexOf(this.detailedActivity.online ? onlineMarkerStart : newsMarkerStart), tags = data.indexOf(newsMarkerTags)+1, 
				mainEnd = data.indexOf(this.detailedActivity.online ? onlineMarkerEnd : newsMarkerEnd, tags);
		if (mainStart < 0 || mainEnd < 0) {
			empty = true;
			data = null;
			return null;
		}
		int scoreStart = data.indexOf(divScore) + divScore.length(), scoreEnd = data.indexOf("<div style=\"width", scoreStart); 
		data = "<head><link href=\"http://st.terrikon.info/terrikon.1.46.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" /></head><body>"
			+ (this.detailedActivity.online ? ("<div class=\"game-head\">" + data.substring(scoreStart, scoreEnd).replace("class=\"txt\"", "class=\"txt\" style=\"padding: 0 0\"") + "</div>"/*.replace("class=\"score\"", "style=\"font-size:40px\"")*/ /*+ "</div></div>"*/) : "")
			+ data.substring(mainStart, mainEnd)
			+ "</body>";
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (data!=null)
			this.detailedActivity.webView.loadDataWithBaseURL("http://terrikon.com/", data, "text/html", "UTF-8", "");
		else {
			if (empty)
				Toast.makeText(this.detailedActivity, "Здесь ещё ничего нет. Зайдите позже. Извините.", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this.detailedActivity, "Нет интернета?", Toast.LENGTH_SHORT).show();
			this.detailedActivity.finish();
		}
		try {
			dialog.dismiss();
		} catch (Exception e) {
		}
	}
	
}