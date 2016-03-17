package name.duzenko.terrikon.logic;

import java.util.ArrayList;

import name.duzenko.terrikon.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class CommentsAdapter extends BaseAdapter {

	private final Context context;
	
	public ArrayList<Comment> comments = new ArrayList<Comment>();
	
	public CommentsAdapter(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
			convertView = View.inflate(context, R.layout.comment_item, null);
		Comment comment = (Comment) getItem(position);
		((TextView)convertView.findViewById(R.id.textComment)).setText(comment.spannable, BufferType.SPANNABLE);
		((TextView)convertView.findViewById(R.id.textUser)).setText(comment.user);
		((TextView)convertView.findViewById(R.id.textEtc)).setText(comment.etc);
/*		if (position % 2 != 0)
			convertView.setBackgroundColor(Color.rgb(233, 255, 233));
		else
			convertView.setBackgroundColor(Color.WHITE);*/
		return convertView;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}