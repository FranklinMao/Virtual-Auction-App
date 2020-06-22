Final Project, Virtual Auction Application

Hello, welcome to the Virtual Auction App!
This app is meant to make use of the server-client relationship to facilitate an auction system where users can bid 
on available items online with real-time information.

Description:
	Server- The server manages processing and storage of data such as transactions, items, users, and more. It is the central 
	communication hub for which the virtual auction is hosted. The server maintains a non-volatile history of transactions, and
	synchronizes actions across all clients
	Client- The client displays the UI that the user interacts with and shows the Auction Items that are for sale. It allows the 
	user to check out each item and place bids on ones they like. It also displays important information such as the current price 
	of an item, its sale status, and a history of auction transactions.

Installation:
To install this app on your machine, check out the latest release and download the jar files (server and client) as well as the sqlite file.
Make sure the server jar and sqlite file are in the same directory in order for database connection.
Run the server jar, and then client jar for each user.
As of now, this app only supports localhost connection.

Cloning:
To download this repo run the following command or use the git features of your IDE.

git clone <url of repo>
