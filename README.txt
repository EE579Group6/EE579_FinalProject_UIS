Readme File for User Identifiaction System
-------------------------------------------------------------

This repository contains the open source code for User Identification System that allows you to develop an app for the android phones and Server to enable push messaging without any network features within your local network.This has been built using eclipse and android-sdk. Tested with Mikrotik routers.


To get started with the application Start the app the phone first and then start
the servers. Update the clientIP.txt with IP address of the phones. Once started
the app can be used to enable messaging between users and requesting contact Info.

Description:

User Identification System is a system developed to enable communication between invited guests in a meeting carried over at multiple locations. The unique aspect of the project is that it does not use any network provider which is generally expensive. This system involves android phones and Mikrotik router connected to internet as the base setup. We have developed an android phone app which the users can use to communicate with other guests without having their contact number or email ID. This application also allows us to share contact details based on permission from user. The users can send and receives messages and contact information which can be used during the meeting period. The application communicates with the local server for its handling and verification. The Mikrotik routers help us in creating a local WLAN by assigning the devices an IP from a selected pool range. The EOIP Interface built in them helps the two routers connected across the internet to form a Layer 2 broadcast network. The servers located on either locations exchange the list of invited guests and their contact details to enable user interaction between the two sites. Hence this system finds use in office meetings where common data can be handed over to the users on their phone and the users can communicate with one another via messages and share contact Information.

Files Used:

Files Description:

Server Side:

Files used -  

1.	MyServer.java
	Runs the Server code. 
	Used to initiate the client authentication and wait for client requests
	Populates hash_map from stored file of invited guests. 

2.	Client.java
	Used to start interaction with the clients
	The clients receive message from server requesting for their contact information stored in         their phones
	The IP address of the client can be hard Coded or received from the pool of Mikrotik         Router

3.	MultiThreadedServer.java
	Handles and process’s different kinds of request from clients and server
	Message Types
	Server2Server
	Client2Server

Client Side (Android):

Files used:

1.	initialisationClass.java
	Starts the application
	Defines the start-up screen
	Initiates the communication with the server and waits for authentication
	Retrieves the server IP address for future communication
	Receives the attendees list

2.	notAuthorized.java
	Handles the alert for a non-authorized attendee

3.	listScreen.java
	Displaying of attendees list
	Handles clicking functionality of an attendee’s name
	  Send message
	  Get contact info
	Viewing the messages and contact information already received

4.	serverService.java
	Background service to receive any packet from the server
	Displays the appropriate notification according to the message type received
	Upon clicking the notification, the user is guided to the appropriate screen  

5.	sendMessage.java
	Opens text box for typing the message
	Send button to send the message
	Once sent, returns the user back to attendees list screen

6.	messageReading.java
	Handles the message received inbox 
	Displays the list of names of the users who has sent a message
	Upon clicking the desired name the message is displayed which is defined in         readRcvdMessage.java

7.	contactRequest.java
	Handles the alert for notifying the user about a request for his/her contact info
	If user selects “Yes” then the contact info is sent else if “No” is clicked then the         rejection message is sent

8.	seeContactInfo.java
	Handles the contacts received inbox 
	Displays the list of names of the users who has sent their contacts
	Upon clicking the desired name the contact details is displayed which is defined in         readContactInfo.java

9.	negContactRequest.java
	Handles the screen to let the user know about the rejection of his/her request for other         party’s contact info
	Close button will take the user back to the attendees list screen.


Instruction for Operation:

1.	Power on the Mikrotik router(already set up with a DHCP server)

2.	Create the following files in the project Library-
	file.txt – Store the name of the attendees
	rcvdNameList.txt. 
	clientIP.txt
3.	From the Routers registration table add the IP address’s to the clientIP.txt

4.	Update the following address’s
	In MultiThreadedServer.java 
	   commServerIp (IP address of the Server in the other location)

5.	Run the application UIS on the android phones first.

6.	Start MyServer.java on the two servers.

7.	Now after verification from the server the users can use the app

