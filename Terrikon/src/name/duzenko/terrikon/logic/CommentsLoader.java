package name.duzenko.terrikon.logic;

import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.widget.LinearLayout;

public class CommentsLoader extends AsyncTask<String, Integer, Void> {

	String prefix1 = "<div class=\"msgbody\">", suffix1 = "</div>", prefix2 = "<strong >", suffix2 = "</strong>", 
			suffix3 = "|", prefixImg = "<img src=\"", suffixImg1 = "\" alt=", suffixImg2 = "/>",
			pagingStart = "<div class=\"paging\">", pageStart = "/page/";
	protected ArrayList<Comment> data = new ArrayList<Comment>();
	protected String html, url;
	protected int page;
	private String text;
	private int start;
	private int end;
	private SpannableStringBuilder ssb;
	
	protected LinearLayout pagesPanel;
	
	@Override
	protected void onPreExecute() {
	}
	
	@Override
	protected void onPostExecute(Void result) {
	}
	
	void processHtmlStyle(String prefix, String suffix, int code) {
		end = 0;
		while (true) {
			start = text.indexOf(prefix, end);
			if (start < 0)
				break;
			end = text.indexOf(suffix, start);
			Object span = null;
			switch (code) {
			case 0:
				span = new StyleSpan( Typeface.BOLD );
				break;
			case 1:
				span = new StrikethroughSpan();
				break;
			case 2:
				span = new ForegroundColorSpan(Color.DKGRAY);
			case 3:
				span = new StyleSpan( Typeface.ITALIC );
			default:
				break;
			}
			ssb.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
			if (code == 2) {
				ssb.setSpan(new StyleSpan( Typeface.ITALIC ), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );		
				ssb.setSpan(new RelativeSizeSpan(0.8f), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			}
			CharSequence quote = ssb.subSequence(start + prefix.length(), end);
			ssb.replace(start, end + suffix.length(), quote);
			text = text.substring(0, start) + quote + text.substring(end + suffix.length());
			end = start+1;
		}		
	}
	
	void processCommentHtml(int index) {
		start = html.indexOf(prefix1, index) + prefix1.length();
		end = html.indexOf(suffix1, start);
		Comment comment = new Comment();
		text = html.substring(start, end).replace("<br />", "").replace("&nbsp;", " ");
		start = html.indexOf(prefix2, end) + prefix2.length();
		end = html.indexOf(suffix2, start);
		comment.user = html.substring(start, end);
		start = end + suffix2.length();
		end = html.indexOf(suffix3, start);
		comment.etc = html.substring(start, end).trim();
		data.add(comment);
		
		ssb = new SpannableStringBuilder(text);
		
		end = 0;
		while (true) {
			start = text.indexOf(prefixImg, end);
			if (start < 0)
				break;
			end = text.indexOf(suffixImg1, start);
			URL url;
			try {
				url = new URL("http://terrikon.com" + text.substring(prefixImg.length() + start, end));
				Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				end = text.indexOf(suffixImg2, end) + suffixImg2.length();
				ssb.setSpan( new ImageSpan(null, image ), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		processHtmlStyle("<strong class=\"bb\">", "</strong>", 0);
		processHtmlStyle("<del class=\"bb\">", "</del>", 1);
		processHtmlStyle("<blockquote class=\"bb_quote\">", "</blockquote>", 2);
		processHtmlStyle("<i class=\"bb\">", "</i>", 3);
		
		comment.spannable = ssb;			
	}
	
	protected ArrayList<Integer> pages = new ArrayList<Integer>();
	
	void processPaging() {
		int pagingStart = html.indexOf(this.pagingStart);
		if (pagingStart < 0)
			return;
		int pagingEnd = html.indexOf("</div>", pagingStart);
		if (pagingEnd < 0)
			return;
		int pageStart = pagingStart;
		while ((pageStart = html.indexOf(this.pageStart, pageStart + 1)) > pagingStart && pageStart < pagingEnd) {
			int pageEnd = html.indexOf("\"", pageStart);
			pages.add(Integer.valueOf(html.substring(pageStart + this.pageStart.length(), pageEnd)));
		}
	}
	
	@Override
	protected Void doInBackground(String... params) {
		html = WebUtil.downloadHttp(url.replace("posts", "comments") + "/page/" + page);
		if (html == null) {
			System.out.println("null html in CommentsLoader");
			return null;
		}
		processPaging();
		int index = -1;
		while ((index = html.indexOf("<blockquote id", index+1)) >= 0) {
			processCommentHtml(index);
		}
		return null;
	}
	
}