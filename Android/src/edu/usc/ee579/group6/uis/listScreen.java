package edu.usc.ee579.group6.uis;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * This class handles displaying of the list that is received by the android device from the server.
 * Here we are using the List View feature in the android to display the list of the attendees.
 * Also here we are stating an background service. This background service will open a server socket
 * which will keep listening for any incoming message from the server. It is binded to a static port.
 * Here, when we click on any user name, a menu opens displaying options to send a message or request the 
 * contact information of that user.
 */

public class ListScreen extends Activity {
	
	protected Button startButton;
	public static BufferedReader readNames;
	File sdCard = Environment.getExternalStorageDirectory();
	File  readListFile;
	ArrayAdapter<String> adapter;
	
	public final Context context = this;
	
	public String[] names;	
	
	private Button readMsgButton;
	private Button readContactButton;
	
	
	private String serverAddr = InitialisationClass.IP[1];
    public static int serverPort = 9777;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listscreen);
		
		
		// Button to view the stored messages
		readMsgButton = (Button) this.findViewById(R.id.btn1);
		// Button to view the stored contacts
		readContactButton = (Button) this.findViewById(R.id.btn2);
	
		
		// Creating the intent to read the messages from the inbox
		final Intent viewMessages = new Intent(this, edu.usc.ee579.group6.uis.MessageReading.class);
		readMsgButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (ServerService.rcvdMessagesMap.isEmpty())
					Toast.makeText(getApplicationContext(), "You have no messages", Toast.LENGTH_SHORT);
				else
				startActivity(viewMessages);
				
			}
		});
		
		// Creating the intent to read the contacts from the inbox
		final Intent viewContacts = new Intent(this, edu.usc.ee579.group6.uis.SeeContactInfo.class);
		readContactButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ServerService.rcvdContactInfoMap.isEmpty())
					Toast.makeText(getApplicationContext(), "You have no contacts", Toast.LENGTH_SHORT);
				else
				startActivity(viewContacts);
				
			}
		});
		
		//Reading the list from the local file stored in the previous class.
		readListFile = new File(sdCard, "list.txt");
		
		 
			try {
				readNames = new BufferedReader(new FileReader(readListFile));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}		
				
			try {
				names = readNames.readLine().split(",");
				readNames.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}
			
			//Listing the read contacts using the List View function
			ListView list = (ListView)findViewById(R.id.list);
			adapter = new ArrayAdapter<String>(this, R.layout.listdisplay, names);		
		    list.setAdapter(adapter);
		    registerForContextMenu(list);  
		    
		    // Starting the background server service to listen for any incoming messages
		    Intent serverIntent = new Intent(getApplicationContext(), ServerService.class);
		    context.startService(serverIntent);

	}
	

	// Creating the context menu for displaying the options upon clicking the attendee name
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
     if (v.getId()==R.id.list) {
		     AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		     menu.setHeaderTitle(names[info.position]);
		     String[] menuItems = getResources().getStringArray(R.array.menu);
		     for (int i = 0; i<menuItems.length; i++) {
		    	 menu.add(Menu.NONE, i, i, menuItems[i]);
	     					}
     		}
     
    }
	
	public static String listItemName;
	
	// This block describes the action to be performed upon clicking the name i.e
	// 1. Send Message
	// 2. Request contact information
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		  int menuItemIndex = item.getItemId();
		  String[] menuItems = getResources().getStringArray(R.array.menu);
		  String menuItemName = menuItems[menuItemIndex];
		  listItemName = names[info.position];
		  
		  if (menuItemName.equals("Send Message"))
		  {
			  // Calling the intent to take to message typing screen
			  Intent messageScreen = new Intent(this,edu.usc.ee579.group6.uis.SendMessage.class);
		      startActivity(messageScreen);
		  }
		  
		  else if (menuItemName.equals("Get Contact Info"))
		  {
			  
			  try {
				Socket contactRequestSocket = new Socket(serverAddr,serverPort);
				
				PrintWriter outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(contactRequestSocket.getOutputStream())), true);
				//The format for sending the get contact information request
				outStream.println("getContactInfo,"+ InitialisationClass.myName + "," + listItemName);
				contactRequestSocket.close();
				
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	  
		  }
		  return true;
	}

}
