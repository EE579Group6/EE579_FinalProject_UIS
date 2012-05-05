package edu.usc.ee579.group6.uis;

import java.util.*;
import java.util.Map.Entry;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*
 * This activity displays the message inbox for the user.
 * It lists out the names of the users who has sent you
 * the message. Then by clicking on the desired name,
 * you will be taken to the message reading screen
 */

public class MessageReading extends Activity {
		
  	
	public static String[] senderNames;
  	private ArrayAdapter<String> adapter;

  	
  	@Override
	protected void onCreate(Bundle savedInstanceState) {
  		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_reading);

		StringBuffer sendString = new StringBuffer();
		
		Iterator<Entry<String, String>> it = ServerService.rcvdMessagesMap.entrySet().iterator();

	  	while(it.hasNext()) {
	  		//Retrieving the names to users who has sent the messages
			Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
			sendString.append(pairs.getKey()+",");
	  	}
		
		String newString = sendString.toString();
		newString = newString.substring(0,newString.length()-1);
		
		senderNames = newString.split(",");
		//Listing the names of the message senders
		ListView list = (ListView)findViewById(R.id.list);
		adapter = new ArrayAdapter<String>(this, R.layout.msg_reading_display, senderNames);
	    list.setAdapter(adapter);
	    
	    
	    final Intent readMsg = new Intent(this,edu.usc.ee579.group6.uis.ReadRcvdMessage.class);
	    list.setOnItemClickListener(new OnItemClickListener() {
			//Upon clicking a name, the message from that user is displayed on the next screen
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				
				readMsg.putExtra("clickedName", parent.getItemAtPosition(position).toString());	
		    	startActivity(readMsg);
				
			}
		});
 		
  	}
  	
  	
}
