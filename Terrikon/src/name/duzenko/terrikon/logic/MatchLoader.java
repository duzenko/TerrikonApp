package name.duzenko.terrikon.logic;

import java.util.ArrayList;

import android.os.AsyncTask;

public class MatchLoader extends AsyncTask<Void, Integer, Void> {

	private static final String url = "http://terrikon.com/football/online";
	protected ArrayList<MatchItem> newItems = new ArrayList<MatchItem>();
	private String html;

	@Override
	protected Void doInBackground(Void... params) {
		html = WebUtil.downloadHttp(url);
		if (html == null) {
			System.out.println("null html in MatchLoader");
			return null;
		}
		int index = -1;
		while ((index = html.indexOf("<a href=\"/football/matches/", index+1)) >= 0) {
			processMatchHtml(index);
		}
		return null;
	}

	String urlEnd = "\" class=\"", teamStart = "<span class=\"left\">", scoreStart = "<span class=\"right\">", tvStart = "<span class=\"tv\">";
	private void processMatchHtml(int index) {
		MatchItem item = new MatchItem();
		int a = html.indexOf("/football/matches/", index), b = html.indexOf(urlEnd, a), c;
		if (b<0)
			return;
		item.webUrl = html.substring(a, b);
		a = b + urlEnd.length();
		b = html.indexOf("\">", a);
		item.displayClass = html.substring(a, b);
		a = html.indexOf(teamStart, b) + teamStart.length();
		b = html.indexOf("</span>", a);
		item.homeTeam = html.substring(a, b);
		a = html.indexOf(scoreStart, b) + scoreStart.length();
		c = html.indexOf("</em>", b);
		if (a < c && a > scoreStart.length()) {
			b = html.indexOf("</span>", a);
			item.homeScore = html.substring(a, b);
		}
		a = html.indexOf(teamStart, b) + teamStart.length();
		b = html.indexOf("</span>", a);
		item.awayTeam = html.substring(a, b);
		a = html.indexOf(scoreStart, b) + scoreStart.length();
		c = html.indexOf("</em>", b);
		if (a < c && a > scoreStart.length()) {
			b = html.indexOf("</span>", a);
			item.awayScore = html.substring(a, b);
		}
		a = html.indexOf("</em>", b) + "</em>".length();
		b = html.indexOf(tvStart, a);
		c = html.indexOf("</a>", a);
		if (b > 0 && b < c) {
			item.status = html.substring(a, b).trim();
			a = b + tvStart.length();
			b = html.indexOf("</span>", a);
			item.tv = html.substring(a, b);
		} else
			item.status = html.substring(a, c).trim();
		newItems.add(item);
	}
	
}