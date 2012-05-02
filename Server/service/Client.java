package edu.usc.ee579.group6.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


// Class Defined to start initial communication with the android users
// after they have been seated in the office room. The Server acts as a client and broadcasts
// message to the android devices which in turn respond to the MultiThreadedServer.

public class Client {
	
	public PrintWriter outToClient = null;
	private Socket clientSocketServer = null;
  private final String defaultBroadcastIP = "192.168.1.102";	
  private final int defaultAndroidPort = 21111;
  
  String message;
  
  public Client() throws IOException {
    initializeClient(defaultBroadcastIP, defaultAndroidPort);
  }
	
  public Client(String androidIP, int androidPort) throws IOException {    

    initializeClient(androidIP, androidPort);
  }

  public void initializeClient(String androidIP, int androidPort) throws IOException {
  	
  	// Initial message for android phone user verification
  	String msgToAndroid = "GetUserInfo";
  	// Setting up Sockets
  	clientSocketServer = new Socket(androidIP,androidPort);
  	System.out.println("Broadcasting to the android Devices");
  	sendMessage(msgToAndroid);
  }

  //Function to send Message to client for requesting initial user information.
  public void sendMessage(String msg) throws IOException{
  	
  	System.out.println("Sending Message");
  	outToClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocketServer.getOutputStream())), true);
  	outToClient.println(msg);
  	
  	// Close client socket communication
  	boolean isDisconnected = closeConnection();
    if(isDisconnected)
      System.out.println("Connection with Android as client closed successfully");
    else
      System.out.println("Something went wrong while closing the connection");	
    
  }
  
  public boolean closeConnection() {
    try {
      clientSocketServer.close();
      outToClient.close(); 
      return true;
    }catch(IOException e) {
      e.printStackTrace();
      return false;
    }
 
	}
}
