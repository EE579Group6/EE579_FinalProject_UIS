package edu.usc.ee579.group6.uis;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*  This class defines the initial setup of the android application.
 *  It sets up the start up screen for the application and then
 *  listens for a initialization message from the static local server.
 *  Then it sends the contact information to the server and then waits for 
 *  authentication from it. Once authenticated, it receives the list of the
 *  attendees.
 */

public class InitialisationClass extends Activity {
	
	File list = Environment.getExternalStorageDirectory();
    File  listFile;
    FileWriter listWriter;
    
    File Root = Environment.getExternalStorageDirectory();
    File  contactInfo;
    FileWriter contactInfoWriter;
    public static BufferedReader readContactFile;
    public static BufferedWriter contactOutputFile;
    public static BufferedWriter listWriteFile;
    public static String myName = "Superman";
    public static String myInfo = "Superman,2135642327,superman@krypton.com";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
       
        starter startClass = new starter();
        Thread t = new Thread (startClass);
        t.start();
     
    }
    
    class starter implements Runnable{
		public void run() {
			// Creating the contact information file
			 String contact = "initialSetup,Superman,2135642327,superman@krypton.com";
		        
		        // Opening a a local file and writing the above contact information to it
		        if(Root.canWrite()){
		        	contactInfo = new File(Root, "contactInfo.txt");
		    		  {
						try {
							contactInfoWriter = new FileWriter(contactInfo);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						contactOutputFile = new BufferedWriter(contactInfoWriter);
					} 
		    	}
		        
		        //Opening a local file to store the incoming attendees list
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
		        
		        if (contactOutputFile != null){
				    try {
				    	contactOutputFile.write(contact);
				    	contactOutputFile.close();
				    	
					} catch (IOException e) {
						
						e.printStackTrace();
					}
			    } 
		        
		        //Starting the server thread on the android to listen for server initialization message
		    	TcpServer();
		    	
			
		}
	}
    
    private static final int TCP_SERVER_PORT = 21111;
    private static final int REMOTE_SERVER_PORT = 9777;
    public static String[] IP;
    
    //Android server thread
    private void TcpServer() {
    	ServerSocket serverSocket = null;
    	try {
    		serverSocket = new ServerSocket(TCP_SERVER_PORT);
			
			Socket childSocket = serverSocket.accept();
			
			//Reading the incoming initialization message
			BufferedReader in = new BufferedReader(new InputStreamReader(childSocket.getInputStream()));
			String incomingMsg = in.readLine() + System.getProperty("line.separator");			
			String serverIP = childSocket.getInetAddress().toString() + System.getProperty("line.separator");
			// Storing the IP address of the server for future communication
			IP = serverIP.split("/");
			childSocket.close();
			
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// Sending the contact information to the server
			Socket replySocket = new Socket(IP[1],REMOTE_SERVER_PORT);
			
			PrintWriter outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(replySocket.getOutputStream())), true);
			readContactFile = new BufferedReader(new FileReader(contactInfo));
			outStream.println(readContactFile.readLine());
			
			
			//Listening for the Attendees list from the server
			BufferedReader listReply = new BufferedReader(new InputStreamReader(replySocket.getInputStream()));
			String listMsg = listReply.readLine();
			
			
			Intent nextScreen = new Intent(this,edu.usc.ee579.group6.uis.ListScreen.class);
			Intent notAuthorizedScreen = new Intent(this,edu.usc.ee579.group6.uis.NotAuthorized.class);
			
			//Checking if the user is authorized or not
			if (listMsg.equals("invalidUser")){
				listWriteFile.close();
				replySocket.close();
				//If not authorized, this will start the below activity
				startActivity(notAuthorizedScreen);
			}
			else {	
				listWriteFile.write(listMsg);
				listWriteFile.close();
	
				replySocket.close();	
				//Once authorized, this will take to the below mentioned activity to display the list
				startActivity(nextScreen);
			}
			
			
		} catch (InterruptedIOException e) {
			//if timeout occurs
			e.printStackTrace();
    	} catch (IOException e) {
			e.printStackTrace();
		} 

    }
}