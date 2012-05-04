package edu.usc.ee579.group6.uis;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/*
 * This activity handles the displaying of contact information from the remote user
 * 
 * Format: 
 * 
 * NAME:
 * PHONE:
 * EMAIL:
 */

public class readContactPage extends Activity {
	
	String senderName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_contact_screen);
		TextView name = (TextView) this.findViewById(R.id.TextView01);
		TextView phone = (TextView) this.findViewById(R.id.TextView02);
		TextView email = (TextView) this.findViewById(R.id.TextView03);
		
		senderName = getIntent().getStringExtra("contactClickedName");
		String contact = serverService.rcvdContactInfoMap.get(senderName);
		String[] editContact = contact.split(",");
		name.setText("Name: "+editContact[0]);
		phone.setText("Phone: "+editContact[1]);
		email.setText("Email: "+editContact[2]);
		
		
	}

}
