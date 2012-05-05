package edu.usc.ee579.group6.uis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * This activity handles the message typing interface form the user.
 * Here a text box is displayed where the user can type the message.
 * There is a send button. Once the user is finished typing, he/she 
 * can click the send button. After sending, the user will be taken 
 * back to attendees list screen.
 */

public class SendMessage extends Activity {
	
	public void onCreate(Bundle savedInstanceState)
	{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.sendmessage);
			                                
			final EditText textMessage;
			final Button sendButton;
			        
			textMessage = (EditText) findViewById(R.id.edittext);
			sendButton = (Button) findViewById(R.id.sendButton);
			textMessage.setSelection(0);
			
			
			final Intent backToListScreen = new Intent(this,edu.usc.ee579.group6.uis.ListScreen.class);
			final Intent backToWriteScreen = new Intent(this,edu.usc.ee579.group6.uis.SendMessage.class);
			sendButton.setOnClickListener(new OnClickListener()
			 {
				 public void onClick(View v)
				 {
				    String text=textMessage.getText().toString();
				    // If the user is sending an empty text, the message will not be sent and the 
				    // user will stay on this screen
				    if (text.equals(null))
				    	startActivity(backToWriteScreen);
				    
				    Socket sendMessageSocket;
					try {
						// Opening a socket to the server to sent the mesage packet
						sendMessageSocket = new Socket(InitialisationClass.IP[1],ListScreen.serverPort);					
					
					PrintWriter outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sendMessageSocket.getOutputStream())), true);
					// Packet format for sending the mesaeg to the desired user
					outStream.println("sendMessage,"+ InitialisationClass.myName + "," + ListScreen.listItemName + ",~" + text);
					
					BufferedReader in = new BufferedReader(new InputStreamReader(sendMessageSocket.getInputStream()));
					String incomingMsg = in.readLine();				    
					sendMessageSocket.close();
					// Once sent, the user is taken back to the attendees list screen
					startActivity(backToListScreen);
										
					} catch (UnknownHostException e) {						
						e.printStackTrace();
					} catch (IOException e) {						
						e.printStackTrace();
					}
    
				  }
			 });

	}
	
}
