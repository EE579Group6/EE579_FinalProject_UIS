package edu.usc.ee579.group6.uis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

/*
 * This is the service class that starts a server socket which keeps listening for any incoming
 * message from the server. This service runs in the background and displays an notification to the user
 * upon any activity.
 */

public class serverService extends Service {
	
	//Creating a hash map to store the messages received from the users. These are stored in <Sender's Name,Message> pairs
	public static Map<String,String> rcvdMessagesMap = new HashMap<String,String>();
	
	//Creating a hash map to store the contacts received from the users. These are stored in <Sender's Name,Contact info> pairs
	public static Map<String,String> rcvdContactInfoMap = new HashMap<String,String>();
	private static final int ANDROID_SERVER_PORT = 12345;
	

	File list = Environment.getExternalStorageDirectory();
    File  listFile;
    FileWriter listWriter;
    
    public BufferedWriter listWriteFile;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
		
	}
	
	public void onCreate (){
		
		new Thread(new Runnable() {
	        public void run() {
	        	
	        	ServerSocket androidServerSocket = null;
	    		try {
	    			androidServerSocket =  new ServerSocket(ANDROID_SERVER_PORT);
	    		} catch (IOException e2) {
	    			e2.printStackTrace();
	    		}
	    		Socket childSocket = null;
	    		
	    		while(true){
	    		       
	    		 	try {
	    		 		// Listening for any message packet from the server
	    				childSocket = androidServerSocket.accept();
	    			} catch (IOException e) {
	    			
	    				e.printStackTrace();
	    			}
	    		 	
	    		 	//Creating a new thread for handling the packet received.
	    	        doCommunication conn_c= new doCommunication(childSocket);
	    	        Thread t = new Thread(conn_c);
	    	        t.start();
	    	      }
	        	
	        }
	    }).start();
	}
	
	
	public String contactRequestorName;
	public String contactRejectorName;
	public static String[] message;
	
	// Message handing thread
	class doCommunication implements Runnable {
		
		  private Socket clientHandler;
		  public String temp= null;
		  public String[] message;
			
		  doCommunication(Socket server) {
		    this.clientHandler=server;
		  }
		  
		  public void run () {
		  
			  BufferedReader inMsg;
			try {
				
				inMsg = new BufferedReader(new InputStreamReader(clientHandler.getInputStream()));
			
				String incomingMsg = inMsg.readLine();
				/*
				 * Received message format
				 * message[0] = Type of message
				 * message[1] = Sender's name
				 * 
				 */
				message = incomingMsg.split(",");
				
				
				if (message[0].equals("sendMessage")){
					/*
					 * Incoming Msg format
					 * sendMessage,sender's name,~message
					 * 
					 * So we are splitting it at ~ then we using only the message 
					 * part as the value against the name.
					 */
					
					String [] rcvdMessage = incomingMsg.split("~");
				
					rcvdMessagesMap.put(message[1],rcvdMessage[1]);					
					// calling the notification function
				    displayNotification("You have received a message", 1);
				}
				else if (message[0].equals("getContactInfo"))
				{
					// message[1] = Contact requestor's name
					contactRequestorName = message[1];
					displayNotification("Your contact has been requested", 2);
	
				}
				else if (message[0].equals("replyToContact"))
				{
					/*
					 * message[2] = contact sender's name
					 * message[3] = contact's phone number
					 * message[4] = contact's email id
					 */
					rcvdContactInfoMap.put(message[2],(message[2]+","+message[3]+","+message[4]));
					displayNotification("You have received a contact reply", 3);
				}
				else if (message[0].equals("negReplyToContact"))
				{
					contactRejectorName = message[1];
					displayNotification("You have received a contact reply", 4);
					
				}
				
				else if (message[0].equals("update"))
				{
					/*
					 * Here we are receiving the updated the list from the server.
					 * This list is updated after the interaction between the two 
					 * remote servers.
					 */
					
					String [] updateMessage = incomingMsg.split("~");
					 if(list.canWrite()){
				        	listFile = new File(list, "list.txt");
				    		  {
								try {
									listWriter = new FileWriter(listFile);
								} catch (IOException e) {
									
									e.printStackTrace();
								}
								listWriteFile = new BufferedWriter(listWriter);
							} 
				    	}
					 
					 listWriteFile.write(updateMessage[1]);
					listWriteFile.close();
					
					displayNotification("You have received an updated contact list", 5);
	
				}
						
			
				} catch (IOException e) {
						e.printStackTrace();
						}
					  
			  
		  	}  
		  
		}
	
	public void displayNotification(String msg, int type)
	{
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.mail, msg, System.currentTimeMillis());
		PendingIntent contentIntent = null;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
	switch (type){
	case 1:
		// The PendingIntent will launch the message inbox screen
		contentIntent = PendingIntent.getActivity(this, 1, new Intent(this, edu.usc.ee579.group6.uis.messageReading.class), android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, "You have an message", ".", contentIntent);
		break;
	case 2:
		
		Intent contactReq = new Intent(this, edu.usc.ee579.group6.uis.contactRequest.class);
		
		contactReq.putExtra("contactRequestor", contactRequestorName);
		// The PendingIntent will launch the contact request alert screen
		contentIntent = PendingIntent.getActivity(this, 1, contactReq, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		
		notification.setLatestEventInfo(this, "Your contact is requested", ".", contentIntent);
		
		break;
	case 3:
		// The PendingIntent will launch the contact inbox screen
		contentIntent = PendingIntent.getActivity(this, 1, new Intent(this, edu.usc.ee579.group6.uis.seeContactInfo.class), android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, "You have an contact reply", ".", contentIntent);
		break;
		
	case 4:
		
		Intent negContactReq = new Intent(this, edu.usc.ee579.group6.uis.negContactRequest.class);
		negContactReq.putExtra("contactRejector", contactRejectorName );
		// The PendingIntent will launch the contact rejection alert
		contentIntent = PendingIntent.getActivity(this, 1, negContactReq, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, "Your contact is requested", ".", contentIntent);
		break;
		
	case 5:
		// The PendingIntent will launch the updated list screen
		contentIntent = PendingIntent.getActivity(this, 1, new Intent(this, edu.usc.ee579.group6.uis.listScreen.class), android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(this, "You have an updated contact list", ".", contentIntent);
		break;

	}
		manager.notify(2, notification);

	}

}
