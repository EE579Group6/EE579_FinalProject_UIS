package edu.usc.ee579.group6.uis;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * This activity displays the alert message to the user saying that
 * the contact requested that was made by you has been denied
 * by the other user.
 */

public class negContactRequest extends Activity {
	
	protected Button closeButton;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.neg_contact_display);
			TextView message = (TextView) this.findViewById(R.id.TextView01);
			//Rejection message
			String negReply = getIntent().getStringExtra("contactRejector") + " has denied your request for contact information!";
			message.setText(negReply);
			
			closeButton = (Button) findViewById(R.id.close_button);
			final Intent showListScreen = new Intent(this,edu.usc.ee579.group6.uis.listScreen.class);
			//Upon clicking the close button, the user is taken back to the attendees list screen
			closeButton.setOnClickListener(new OnClickListener() {
		 
				public void onClick(View v) {
					startActivity(showListScreen);
					
				}
			});
	        
	 }

}
