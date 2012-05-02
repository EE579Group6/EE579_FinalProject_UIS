package edu.usc.ee579.group6.service;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


import edu.usc.ee579.group6.main.MyServer;

public class MultiThreadedServer{
	
	 private static int port= 9777;
	 
	 
	  // Listen for incoming connections and handle them
	  public void startServer() {

	    try{
	      ServerSocket listener = new ServerSocket(port);
	      Socket server;
	      System.out.println("Server Soket opened");
	      
	      // Start as Client to send request to the android devices for their initial verification
	      new Client();
	      
	      while(true){
	       
	        server = listener.accept();
	        doCommunication conn_c= new doCommunication(server);
	        Thread t = new Thread(conn_c);
	        t.start();
	      }
	    } catch (IOException ioe) {
	      System.out.println("IOException on socket listen: " + ioe);
	      ioe.printStackTrace();
	    }
	  }
}

// Class to start a thread to do communication with the clients


class doCommunication implements Runnable {
  
	private Socket clientHandler;
  public String temp= null;
  public String[] message;
  public int recipientPort = 12345;
  public int serverPort = 9777;
  // IP address of the other Server
  private static String commServerIP = "192.168.1.107";
  private static String clientIP = "192.168.1.102";
  
  private PrintWriter outToClient = null;
	private BufferedReader inFromBuffer = null;
	
  doCommunication(Socket server) {
    this.clientHandler=server;
  }

  public void run () {
  	try{
				outToClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientHandler.getOutputStream())), true);
				inFromBuffer = new BufferedReader(new InputStreamReader(clientHandler.getInputStream()));
				
				// Temp stores the received message
				temp = recvMessage();
				System.out.println(temp);
				message = temp.split(",");
				System.out.println("From client>" + message[0]);
				
				// This blocks handle the initial Setup with the client.
				// The server receives the contact information from the client and stores it in its hash map.
				// The server then authenticates the user against the provided list and and sends the list of invited guests to the client

				if(message[0].equals("initialSetup"))
				{
					// message[1] == CLients Name
					// message[2] == CLients Phone Number
					// message[3] == CLients email address
					
					System.out.println("From client userName>" + message[1]);
					
					String clientIP = clientHandler.getInetAddress().toString();
          String[] clientIPAddr = clientIP.split("/");
          
					System.out.println("Client Ip Address:" + clientIP);
					boolean checkValidUser = MyServer.myMap.containsKey(message[1]);
					
					// If user is valid update the map and send list
					if(checkValidUser){			
						MyServer.myMap.put(message[1],message[2]+","+message[3]+","+clientIPAddr[1]);
						BufferedReader readFile = new BufferedReader(new FileReader("./file.txt"));
				  	String line = null;
				  	StringBuffer sendString = new StringBuffer();
				  	while((line = readFile.readLine()) != null)
				  	{
				  		sendString.append(line+",");
				  	}
				  	String newString = sendString.toString();
				  	newString = newString.substring(0,newString.length()-1);
				  	System.out.println(newString);
				  	outToClient.println(newString);		
					}
					else{
						sendMessage("invalidUser");
					}
					
					// Check if all the invited Guest have been authorized and then pass the list of users
					// to the other Server.
			  	int allFilled = 1;
			  	Iterator<Entry<String, String>> it = MyServer.myMap.entrySet().iterator();
					while (it.hasNext()) {
			      Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
			       if(pairs.getValue().equals(0)){
			      	 allFilled = 0;
			       }
			      
			  	}
					// Send my file to the other server when all users have logged on the app
					if(allFilled == 1){
						
						Socket recipientSock = new Socket(commServerIP,serverPort);
						PrintWriter outToRecipient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(recipientSock.getOutputStream())), true);					
						StringBuffer nameList = new StringBuffer();
						
						Iterator<Entry<String, String>> itr = MyServer.myMap.entrySet().iterator();
						while (itr.hasNext()) {
				      Map.Entry<String, String> pairs = (Map.Entry<String, String>)itr.next();
				      nameList.append(pairs.getKey()+"#"+ pairs.getValue()+"~");
				    }
						
						String sendNameList = nameList.toString();
						sendNameList = sendNameList.substring(0,sendNameList.length()-1);
						System.out.println("nameList"+sendNameList);
						outToRecipient.println("attendanceList"+",>"+sendNameList);
						outToRecipient.close();
						recipientSock.close();
					}		
				}
				
				//To handle name List provided by the other Server
				if(message[0].equals("attendanceList"))
				{
					// Splitting the received names into key value pairs and storing it in a hash map
					String[] splitMsg = temp.split(">");
					String[] rcvdMapList = splitMsg[1].split("~");
					StringBuffer nameList = new StringBuffer();
					for(int i=0; i < rcvdMapList.length;i++)
					{	
						String[] keyValuepair = rcvdMapList[i].split("#");
						System.out.println("Key Value Pair : "+keyValuepair[0]+"="+ keyValuepair[1]);
						MyServer.myRcvdMap.put(keyValuepair[0], keyValuepair[1]);
						nameList.append(keyValuepair[0]+",");
					}
					String sendNameList = nameList.toString();
					sendNameList = sendNameList.substring(0,sendNameList.length()-1);
					String[] rcvdList = sendNameList.split(",");
					
					System.out.println("Length of the rcvd Name List is "+ rcvdList.length);
					PrintWriter writeToFile = new PrintWriter(new FileWriter("./rcvdNameList.txt"));
					for(int i=0; i < rcvdList.length;i++)
					{	
						writeToFile.println(rcvdList[i]);
					}
					writeToFile.close();
					
					BufferedReader readRcvdFile = new BufferedReader(new FileReader("./rcvdNameList.txt"));
			  	String line = null;
			  	StringBuffer sendString = new StringBuffer();
			  	while((line = readRcvdFile.readLine()) != null)
			  	{
			  		sendString.append(line+",");
			  	}
			  	BufferedReader readMyFile = new BufferedReader(new FileReader("./file.txt"));
			  	String line1 = null;
			  	while((line1 = readMyFile.readLine()) != null)
			  	{
			  		sendString.append(line1+",");
			  	}
			  	
			  	String newString = sendString.toString();
			  	newString = newString.substring(0,newString.length()-1);
			  	// Send the updated List to the User
			  	System.out.println("Sending Update file "+newString);
			  	
			  	Socket recipientSock = new Socket(clientIP,recipientPort);
					PrintWriter outToRecipient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(recipientSock.getOutputStream())), true);
					outToRecipient.println("update"+",~"+newString);
					outToRecipient.close();
					recipientSock.close();
				}
				
				// To handle client request of send Message
				if(message[0].equals("sendMessage"))
				{
					// In this case the message is received in this format
					// message[1] == Senders Name
					// message[2] == Recipients Name
					// message[3] == Message to send
					
					String[] rcvdMessage = temp.split("~");
					System.out.println("Rcvd Message"+rcvdMessage[1]);
					
					if(MyServer.myMap.containsKey(message[2])||MyServer.myRcvdMap.containsKey(message[2])){
						
						String recipientData;
						if(MyServer.myMap.containsKey(message[2]))
							recipientData = MyServer.myMap.get(message[2]);
						else
							recipientData = MyServer.myRcvdMap.get(message[2]);
							
						String[] recipientDataInfo =  recipientData.split(",");
						System.out.println("Sending Msg to user with IP"+recipientDataInfo[2]);
					
						// Sending Message to the recipient. Creating a new socket to send the message.
						Socket recipientSock = new Socket(recipientDataInfo[2],recipientPort);
						PrintWriter outToRecipient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(recipientSock.getOutputStream())), true);
						outToRecipient.println("sendMessage"+","+message[1]+","+"~"+rcvdMessage[1]);
						outToRecipient.close();
						recipientSock.close();
						System.out.println("Sent Msg to user with IP"+recipientDataInfo[2]);	
						
						// send confirmation msg to the sending client
						sendMessage("Message Sent");		
					}
					else
					{
						sendMessage("Sorry User is not online.");
					}
				}
				
				// To handle client request of Get other contact info.
				if(message[0].equals("getContactInfo"))
				{
					
					// In this case the message is received in this format
					// message[1] == Senders Name
					// message[2] == Recipients Name
					
					if(MyServer.myMap.containsKey(message[2])||MyServer.myRcvdMap.containsKey(message[2])){
						
						String recipientData;
						if(MyServer.myMap.containsKey(message[2]))
							recipientData = MyServer.myMap.get(message[2]);
						else
							recipientData = MyServer.myRcvdMap.get(message[2]);
						
						//String recipientData = MyServer.myMap.get(message[2]);
						String[] recipientDataInfo =  recipientData.split(",");
						System.out.println("Requesting Contact Info of user "+ message[2] + " with IP "+ recipientDataInfo[2]);
					
						// Sending Message to the recipient. Creating a new socket to send the message.
						Socket recipientSock = new Socket(recipientDataInfo[2],recipientPort);
						PrintWriter outToRecipient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(recipientSock.getOutputStream())), true);
						outToRecipient.println("getContactInfo"+","+message[1]+","+message[2]);
						outToRecipient.close();
						recipientSock.close();
						
					}		
				}
				
				if(message[0].equals("replyToContact")||message[0].equals("negReplyToContact"))
				{
					
					// In this case the message is received in this format
					// message[1] == Contact Info Requesters Name
					// message[2] == Contact Info Providers Name
					// message[3] == Phone number
				  // message[4] == email address
					
					
					if(MyServer.myMap.containsKey(message[1])||MyServer.myRcvdMap.containsKey(message[1])){
						
						
						String recipientData;
						if(MyServer.myMap.containsKey(message[1]))
							recipientData = MyServer.myMap.get(message[1]);
						else
							recipientData = MyServer.myRcvdMap.get(message[1]);
						//String recipientData = MyServer.myMap.get(message[1]);
						String[] recipientDataInfo =  recipientData.split(",");
						System.out.println("Sending Contact Info of user "+ message[2] + "to user" + message[1]);
					
						// Sending Message to the recipient. Creating a new socket to send the message.
						Socket recipientSock = new Socket(recipientDataInfo[2],recipientPort);
						PrintWriter outToRecipient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(recipientSock.getOutputStream())), true);
						if(message[0].equals("replyToContact")){
							outToRecipient.println("replyToContact"+","+message[1]+","+message[2]+","+message[3]+","+message[4]);
						}else{
							outToRecipient.println("negReplyToContact"+","+message[1]+","+message[2]);
						}
			
						outToRecipient.close();
						recipientSock.close();
						
					}		
				}
				
				inFromBuffer.close();
				outToClient.close();
				clientHandler.close();
				
				
		  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  	
  }
  
  public void sendMessage(String msg) throws IOException{	
		outToClient.println(msg);
  }
  
  public String recvMessage() {
  	
  	String rcvdMessage = null;
		
  	try {
			rcvdMessage = inFromBuffer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    return rcvdMessage;
  }	 
}
