// Group 6 - EE 579 Wireless and Mobile Network Design
// Group Members -	Venkatraman Venkatapathy
//									Abhishek Sharma								
//									Sneha Patil
// Initial class. Run this first
// Create Files in the src lib with names file.txt and rcvdNameList.txt
// file.txt is the list of the invited names

package edu.usc.ee579.group6.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import edu.usc.ee579.group6.service.MultiThreadedServer;


public class MyServer {
	
	// Creating two hash_map to store the invited Users corresponding contact Information such as 
	// phone,email and IP address.
	public static Map<String,String> myMap = new HashMap<String,String>();
	public static Map<String,String> myRcvdMap = new HashMap<String,String>();
	
  public static void main(String args[]) throws IOException {
    
  	// Open FileReader to convert given text file of invited guests and numbers to store in HashMap
  	BufferedReader readFile = new BufferedReader(new FileReader("./file.txt"));
  	String line = null;
  	String entries = null;
  	
  	// Populating the Hash map with the invited guest names and initializing them to zero. 
  	while((line = readFile.readLine()) != null)
  	{
  		System.out.println(line);
  		entries = line;
  		myMap.put(entries,"0");
  	}
  	
  	// Writing the List to a file For Test purpose.
  	PrintWriter outFile= new PrintWriter("outFile.txt");
  	Iterator<Entry<String, String>> it = myMap.entrySet().iterator();
  	while (it.hasNext()) {
      Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
      System.out.println(pairs.getKey() + " = " + pairs.getValue());
      outFile.println(pairs.getKey());
  	}
  	outFile.close();

  	// Start as Client to send request to the android devices for their initial verification
  	// new Client();
  	
  	// Start Server to handle requests from multiple clients
  	MultiThreadedServer server = new MultiThreadedServer();
  	server.startServer(); 	
  }
  
  // Function to return Map
  public Map<String,String> getMap(){ 	
  	return myMap;
  }
  
}