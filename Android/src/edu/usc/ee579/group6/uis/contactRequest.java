package edu.usc.ee579.group6.uis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;

/*
 * This class handles the displaying of contact requested notification.
 * This activity is invoked when a user has requested your contact information.
 * A alert message will be displayed asking for your permission to send the details.
 * If  you click Yes, then you information is sent to that user or else if you
 * clicked No, then request denied message will be sent.
 * 
 */

public class ContactRequest extends Activity {
	
	AlertDialog.Builder builder;
    AlertDialog alert;
    
	private String serverAddr = InitialisationClass.IP[1];
    public int serverPort = 9777;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        builder =new AlertDialog.Builder(this);
	        builder.setTitle( "Requesting Contact Information");
	        builder.setMessage( getIntent().getStringExtra("contactRequestor")+" has asked for your contact information. Would you like sent it?" );
	        builder.setCancelable(false);
	        // Action when Yes button is clicked
	        final Intent showListScreen = new Intent(this,edu.usc.ee579.group6.uis.ListScreen.class);
	        builder.setPositiveButton("YES", new OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	               
	            	Socket contactReplySocket;
					try {
						// Opening a socket to send the message to your server
						contactReplySocket = new Socket(serverAddr,serverPort);
									
						PrintWriter outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(contactReplySocket.getOutputStream())), true);
						// Packet format when you want to send your contact information
						outStream.println("replyToContact,"+ getIntent().getStringExtra("contactRequestor") + "," + InitialisationClass.myInfo);
						contactReplySocket.close();
						} catch (UnknownHostException e) {
							
							e.printStackTrace();
						} catch (IOException e) {
							
							e.printStackTrace();
						}
		            	// Once done, you will be taken back to the attendees list screen
						startActivity(showListScreen);
		            }
	        });
	        
	        builder.setNegativeButton("NO", new OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	                
	            	
	            	Socket contactReplySocket;
					try {
						// Opening a socket to send the message to your server
						contactReplySocket = new Socket(serverAddr,serverPort);
					
					
					PrintWriter outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(contactReplySocket.getOutputStream())), true);
					// Packet format when you don't want to send your contact information
					outStream.println("negReplyToContact,"+ getIntent().getStringExtra("contactRequestor")+ "," + InitialisationClass.myName);
					contactReplySocket.close();
					} catch (UnknownHostException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					// Once done, you will be taken back to the attendees list screen
					startActivity(showListScreen);
	                
	            }
	        });
	        
	        alert = builder.create();
	        alert.show();	       
	    }

}
