package edu.usc.ee579.group6.uis;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * This handles the displaying of the message from other users.
 * This screen is invoked when the user clicks on the name of the 
 * message sender. Here, the corresponding message of the clicked name 
 * is extracted and then it is displayed on the screen.
 */

public class ReadRcvdMessage extends Activity {
	
	
	
	String senderName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.read_msg_screen);
		TextView message = (TextView) this.findViewById(R.id.TextView01);
		
		Button ok = (Button) findViewById(R.id.ok);
        
		Button delete = (Button) findViewById(R.id.delete);
		
		senderName = getIntent().getStringExtra("clickedName");
		String msg = ServerService.rcvdMessagesMap.get(senderName);
		
		message.setText(msg);
		
		//On clicking OK button, you will be taken back to the attendees list screen
		final Intent msgListScreen = new Intent(this,edu.usc.ee579.group6.uis.ListScreen.class);
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(msgListScreen);
				
			}
		});
		
		//Upon clicking delete button, the message and name of the user is deleted from the message inbox 
		//and you are taken back to the attendees list screen.
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ServerService.rcvdMessagesMap.remove(senderName);
				startActivity(msgListScreen);
				
			}
		});

	}

}
