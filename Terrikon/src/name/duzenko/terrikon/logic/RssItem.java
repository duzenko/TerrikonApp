package name.duzenko.terrikon.logic;

import java.util.*;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import android.text.format.DateFormat;

public class RssItem {

	public String title, description, link, imageUrl = "", dateString;
	public Date pubDate;
	
	@SuppressWarnings("deprecation")
	public RssItem(String title, String description, Date pubDate, String link) {
		this.title = title;
		this.description = description;
		this.pubDate = pubDate;
		this.link = link;
		if (pubDate.getDate() == Calendar.getInstance().get(Calendar.DATE))
			dateString = DateFormat.format("kk:mm", pubDate).toString();
		else
			dateString = DateFormat.format("dd.MM kk:mm", pubDate).toString();
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<RssItem> getRssItems(String feedUrl) {

		ArrayList<RssItem> rssItems = new ArrayList<RssItem>();

		try {
			// open an URL connection make GET to the server and
			// take xml RSS data
			URL url = new URL(feedUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();

				// DocumentBuilderFactory, DocumentBuilder are used for
				// xml parsing
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// using db (Document Builder) parse xml data and assign
				// it to Element
				Document document = db.parse(is);
				Element element = document.getDocumentElement();

				// take rss nodes to NodeList
				NodeList nodeList = element.getElementsByTagName("item");

				if (nodeList.getLength() > 0) {
					for (int i = 0; i < nodeList.getLength(); i++) {

						// take each entry (corresponds to <item></item> tags in
						// xml data

						Element entry = (Element) nodeList.item(i);

						Element _titleE = (Element) entry.getElementsByTagName(
								"title").item(0);
						Element _descriptionE = (Element) entry
								.getElementsByTagName("description").item(0);
						Element _pubDateE = (Element) entry
								.getElementsByTagName("pubDate").item(0);
						Element _linkE = (Element) entry.getElementsByTagName(
								"link").item(0);
						Element enclosure = (Element) entry.getElementsByTagName(
								"enclosure").item(0);

						String _title = _titleE.getFirstChild().getNodeValue();
						String _description = _descriptionE.getFirstChild()
								.getNodeValue();
						Date _pubDate = new Date(_pubDateE.getFirstChild()
								.getNodeValue());
						String _link = _linkE.getFirstChild().getNodeValue();

						// create RssItemObject and add it to the ArrayList
						RssItem rssItem = new RssItem(_title, _description,
								_pubDate, _link);
						if(enclosure!=null) {
							String s = enclosure.getAttribute("url");
							if (s != null && s.startsWith("//"))
								s = "http:" + s;								
							rssItem.imageUrl = s;
						}

						rssItems.add(rssItem);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rssItems;
	}

}