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

/*
 * This activity will handle the part when the android
 * user is not an authorized attendee. This will display an 
 * alert message to the user saying the 
 * "You are not on the attendees list.". 
 * Then after pressing close, the application will shut 
 * down.
 */
public class notAuthorized extends Activity {
	
	AlertDialog.Builder builder;
    AlertDialog alert;
    
	private String serverAddr = initialisationClass.IP[1];
    public int serverPort = 9777;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        builder =new AlertDialog.Builder(this);
	        builder.setTitle( "NOT AUTHORIZED!");
	        builder.setMessage( "Sorry! You are not on the attendees list." );
	        builder.setCancelable(false);
	        
	        final Intent showListScreen = new Intent(this,edu.usc.ee579.group6.uis.listScreen.class);
	        builder.setPositiveButton("CLOSE", new OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	                
	            	
	            }
	        });
 
	        alert = builder.create();
	        alert.show();
	        
	       
	    }

}
