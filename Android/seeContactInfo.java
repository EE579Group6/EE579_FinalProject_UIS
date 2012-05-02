package edu.usc.ee579.group6.uis;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/*
 * This activity handle the displaying of the contact inbox.
 * The contact senders list is retrieved from the hash map and 
 * they are displayed as a list. To view the contact, the user 
 * can click on the desired name. this will take the user to the 
 * contact display screen.
 */

public class seeContactInfo extends Activity {
	
	public static String[] contactNames;
	int i=0;

  	private ArrayAdapter<String> adapter;

  	
  	@Override
	protected void onCreate(Bundle savedInstanceState) {
  		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_reading);
		
		StringBuffer sendString = new StringBuffer();
		
		Iterator<Entry<String, String>> it = serverService.rcvdContactInfoMap.entrySet().iterator();
		// Retrieving the names of the contact senders
	  	while(it.hasNext()) {

			Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
			sendString.append(pairs.getKey()+",");
	  	}
		
		String newString = sendString.toString();
		newString = newString.substring(0,newString.length()-1);
		
		contactNames = newString.split(",");
		
		// Listing the contact senders names using the List view function
		ListView list = (ListView)findViewById(R.id.list);
		adapter = new ArrayAdapter<String>(this, R.layout.msg_reading_display, contactNames);
	    list.setAdapter(adapter);
	    final Intent readContact = new Intent(this,edu.usc.ee579.group6.uis.readContactPage.class);
	    list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				// Calling the contact displaying screen for the clicked name where the contact information 
				// of that user is displayed.
				readContact.putExtra("contactClickedName", parent.getItemAtPosition(position).toString());	
		    	startActivity(readContact);
				
			}
		});
 		
  	}
  	

}
