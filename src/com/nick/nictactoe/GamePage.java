package com.nick.nictactoe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class GamePage extends Activity {
	
	final static int PLAYER_BLUE = 0;
	final static int PLAYER_RED = 1;
	final static int PLAYER_NONE = -1;
	
	final static String PLAYER_BLUE_ICON = "O";
	final static String PLAYER_RED_ICON = "X";

	LinearLayout layoutPageContents;
	TableLayout layoutGameBoard;
	View viewPlayerBlue;
	View viewPlayerRed;
	
	int currentPlayer = -1;
	int playerWhoStarted;
	
	int filledBoxes;
	
	Animation animation250;
	Animation animation500;
	View currentFlashing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_game_page);
		
		animation250 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
	    animation250.setDuration(250); // duration - half a second
	    animation250.setInterpolator(new LinearInterpolator()); // do not alter animation250 rate
	    animation250.setRepeatCount(Animation.INFINITE); // Repeat animation250 infinitely
	    animation250.setRepeatMode(Animation.REVERSE); // Reverse animation250 at the end so the button will fade back in
		
	    animation500 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
	    animation500.setDuration(500); // duration - half a second
	    animation500.setInterpolator(new LinearInterpolator()); // do not alter animation250 rate
	    animation500.setRepeatCount(Animation.INFINITE); // Repeat animation250 infinitely
	    animation500.setRepeatMode(Animation.REVERSE); // Reverse animation250 at the end so the button will fade back in
		
		layoutPageContents = (LinearLayout) findViewById(R.id.Layout_page_contents);
		layoutGameBoard = (TableLayout) findViewById(R.id.Layout_game_board);
		viewPlayerBlue = findViewById(R.id.View_blue_player);
		viewPlayerRed = findViewById(R.id.View_red_player);
		
		layoutGameBoard.setAnimation(null);
		
		if (currentPlayer == PLAYER_NONE) {
			playerWhoStarted = PLAYER_RED;
		}
		
		if (playerWhoStarted == PLAYER_RED) {
			currentPlayer = PLAYER_RED;
		} else if (playerWhoStarted == PLAYER_BLUE) {
			currentPlayer = PLAYER_BLUE;
		}
		
		filledBoxes = 0;
		
		flashPlayerColor();
	}
	
	public void flashPlayerColor() {
		if (currentFlashing != null) {
			currentFlashing.setAnimation(null);
		}
		
		if (currentPlayer == PLAYER_RED) {
			viewPlayerRed.startAnimation(animation250);
			currentFlashing = viewPlayerRed;
		} else if (currentPlayer == PLAYER_BLUE) {
			viewPlayerBlue.startAnimation(animation250);
			currentFlashing = viewPlayerBlue;
		}
	}

	@Override
	protected void onResume() {
		layoutPageContents.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		super.onResume();
	}
	
	public void boxClicked(View v) {
		filledBoxes++;
		v.setClickable(false);
		
		TextView tv = (TextView) v;
		if (currentPlayer == PLAYER_RED) {
			tv.setText(PLAYER_RED_ICON);
			tv.setTextColor(getResources().getColor(R.color.red));
			currentPlayer = PLAYER_BLUE;
		} else if (currentPlayer == PLAYER_BLUE) {
			tv.setText(PLAYER_BLUE_ICON);
			tv.setTextColor(getResources().getColor(R.color.blue));
			currentPlayer = PLAYER_RED;
		} else {
			tv.setText("-");
		}

		if (filledBoxes == 9) {
			currentFlashing.setAnimation(null);
			layoutGameBoard.startAnimation(animation500);
			return;
		}
		
		flashPlayerColor();
	}
	
	public void newGame(View v) {
		if (playerWhoStarted == PLAYER_RED) {
			playerWhoStarted = PLAYER_BLUE;
		} else if (playerWhoStarted == PLAYER_BLUE) {
			playerWhoStarted = PLAYER_RED;
		}
		onCreate(null);
		onResume();
	}
	
	public void exitApp(View v) {
		finish();
	}
	
}
