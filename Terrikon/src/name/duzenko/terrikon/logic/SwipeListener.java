package name.duzenko.terrikon.logic;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.TabHost;

public class SwipeListener implements OnTouchListener {
	
	/**
	 * 
	 */
	private final TabHost tabHost;

	/**
	 * @param detailedActivity
	 */
	public SwipeListener(TabHost tabHost) {
		this.tabHost = tabHost;
		gestureDetector = new GestureDetector(tabHost.getContext(), gestureListener);
	}

	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		
		  private static final int SWIPE_MIN_DISTANCE = 66;
		  private static final int SWIPE_MAX_OFF_PATH = 111;
		  private static final int SWIPE_THRESHOLD_VELOCITY = 111;

		  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		   System.out.println(" in onFling() :: ");
		   if (e1 == null || e2 == null) {
			   System.out.println("Null event in SimpleOnGestureListener");
			   return super.onFling(e1, e2, velocityX, velocityY);
		   }
		   if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		    return false;
		   if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			   if (tabHost.getCurrentTab() == 0) {
				tabHost.setCurrentTab(1);
			}
		   } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			   if (tabHost.getCurrentTab() != 0) {
				tabHost.setCurrentTab(0);
			   }
		   }
		   return super.onFling(e1, e2, velocityX, velocityY);
		  }
		  
	};
	
	GestureDetector gestureDetector;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		System.out.println(" in onTouch() :: ");
		return gestureDetector.onTouchEvent(event) || v.onTouchEvent(event);
	}
	
}