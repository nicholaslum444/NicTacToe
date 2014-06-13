package com.nick.nictactoe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
	
	final static int PLAYER_BLUE = -1;
	final static int PLAYER_RED = 1;
	final static int PLAYER_NONE = 0;
	
	final static String PLAYER_BLUE_ICON = "O";
	final static String PLAYER_RED_ICON = "X";

	LinearLayout layoutPageContents;
	TableLayout layoutGameBoard;
	View viewPlayerBlue;
	View viewPlayerRed;
	
	int currentPlayer = PLAYER_NONE;
	int playerWhoStarted;
	
	int[][] gameBoardArray;
	int filledBoxes;
	int clickedColumnX;
	int clickedRowY;
	
	boolean winRow;
	boolean winColumn;
	boolean winUpDiagonal;
	boolean winDownDiagonal;
	boolean gameEnded;
	
	Animation animation250;
	Animation animation500;
	View currentFlashing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_game_page);
		getActionBar().hide();
		
		winRow = false;
		winColumn = false;
		winUpDiagonal = false;
		winDownDiagonal = false;
		gameEnded = false;
		filledBoxes = 0;
		gameBoardArray = new int[3][3];
		
		if (currentPlayer == PLAYER_NONE) {
			playerWhoStarted = PLAYER_RED;
		}
		
		currentPlayer = playerWhoStarted;
		
		animation250 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
	    animation250.setDuration(300); // duration - half a second
	    animation250.setInterpolator(new LinearInterpolator()); // do not alter animation250 rate
	    animation250.setRepeatCount(Animation.INFINITE); // Repeat animation250 infinitely
	    animation250.setRepeatMode(Animation.REVERSE); // Reverse animation250 at the end so the button will fade back in
		
	    animation500 = new AlphaAnimation(1, (float) 0.3); // Change alpha from fully visible to invisible
	    animation500.setDuration(500); // duration - half a second
	    animation500.setInterpolator(new LinearInterpolator()); // do not alter animation250 rate
	    animation500.setRepeatCount(9); // Repeat animation250 infinitely
	    animation500.setRepeatMode(Animation.REVERSE); // Reverse animation250 at the end so the button will fade back in
		
		layoutPageContents = (LinearLayout) findViewById(R.id.Layout_page_contents);
		layoutGameBoard = (TableLayout) findViewById(R.id.Layout_game_board);
		viewPlayerBlue = findViewById(R.id.View_blue_player);
		viewPlayerRed = findViewById(R.id.View_red_player);
		
		layoutGameBoard.setAnimation(null);
		layoutGameBoard.setClickable(true);
		
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

	@SuppressLint("InlinedApi")
	@Override
	protected void onResume() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			layoutPageContents.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
		super.onResume();
	}
	
	public void boxClicked(View v) {
		if (gameEnded) {
			return;
		}
		
		filledBoxes++;
		v.setClickable(false);
		
		fillUpArray(v);
		
		TextView tv = (TextView) v;
		if (currentPlayer == PLAYER_RED) {
			tv.setText(PLAYER_RED_ICON);
			tv.setTextColor(getResources().getColor(R.color.red));
		} else if (currentPlayer == PLAYER_BLUE) {
			tv.setText(PLAYER_BLUE_ICON);
			tv.setTextColor(getResources().getColor(R.color.blue));
		} else {
			tv.setText("-");
		}
		
		if (isCurrentPlayerWinner()) {
			currentFlashing.setAnimation(null);
			layoutGameBoard.startAnimation(animation500);
			
			if (currentPlayer == PLAYER_RED) {
				layoutGameBoard.setBackgroundColor(getResources().getColor(R.color.red_faded));
			} else if (currentPlayer == PLAYER_BLUE) {
				layoutGameBoard.setBackgroundColor(getResources().getColor(R.color.blue_faded));
			}
			
			gameEnded = true;
			return;
		}

		if (filledBoxes == 9) {
			currentFlashing.setAnimation(null);
			layoutGameBoard.startAnimation(animation500);
			layoutGameBoard.setClickable(false);
			layoutGameBoard.setBackgroundColor(getResources().getColor(R.color.grey_faded));
			gameEnded = true;
			return;
		}
		
		
		switchPlayer();
		flashPlayerColor();
	}
	
	public void fillUpArray(View v) {
		clickedColumnX = Integer.parseInt((String) v.getTag());
		clickedRowY = Integer.parseInt((String) ((View) v.getParent()).getTag());
		Log.w("index", clickedColumnX +""+ clickedRowY);
		gameBoardArray[clickedColumnX][clickedRowY] = currentPlayer;
	}
	
	public boolean isCurrentPlayerWinner() {
		return checkRow() || checkColumn() || checkUpDiagonal() || checkDownDiagonal();
	}
	
	public boolean checkRow() {
		for (int i = 0; i < 3; i++) {
			if (gameBoardArray[i][clickedRowY] != currentPlayer) {
				return false;
			}
		}
		Log.w("row", "q");
		winRow=true;
		return true;
	}
	
	public boolean checkColumn() {
		for (int i = 0; i < 3; i++) {
			if (gameBoardArray[clickedColumnX][i] != currentPlayer) {
				return false;
			}
		}
		Log.w("col", "q");
		winColumn=true;
		return true;
	}
	
	public boolean checkUpDiagonal() { /* "/" */
		// check if within the 3 squares that is possible for diag
		if ((clickedRowY == 2 && clickedColumnX == 0) ||
			(clickedRowY == 1 && clickedColumnX == 1) ||
			(clickedRowY == 0 && clickedColumnX == 2)) {
				
			for (int x = 0, y = 2; x < 3; x++, y--) {
				if (gameBoardArray[x][y] != currentPlayer) {
					return false;
				}
			}
			Log.w("up", "q");
			winUpDiagonal=true;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkDownDiagonal() { /* "\" */
		// check if within the 3 squares that is possible for diag
		if ((clickedRowY == 0 && clickedColumnX == 0) ||
			(clickedRowY == 1 && clickedColumnX == 1) ||
			(clickedRowY == 2 && clickedColumnX == 2)) {
				
			for (int x = 0, y = 0; y < 3; y++, x++) {
				if (gameBoardArray[x][y] != currentPlayer) {
					return false;
				}
			}
			Log.w("down", "q");
			winDownDiagonal=true;
			return true;
		}
		return false;
	}
	
	public void switchPlayer() {
		if (currentPlayer == PLAYER_RED) {
			currentPlayer = PLAYER_BLUE; 
		} else if (currentPlayer == PLAYER_BLUE) {
			currentPlayer = PLAYER_RED;
		}
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
