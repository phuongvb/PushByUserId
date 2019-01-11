					Push By User Id

Files:
- input.txt: fill information of project
	+ application: it is called AppID. It is below name of application in homepage
	+ auth: open application, on left panel, select API Access, it is token field

	xxx
	{
		"auth":"sU6RBbLEHp8HFrtO093lHeeD8nzgMg39eqU4lHVbyJxqzA7wVqVAn2cpmiObJ0l9qhsRdP7DUgN5LPrRq5vA",
		"application":["25FAA-1ADDC"],
	}

	yyy
	{
		"auth":"q9mT9jczTO3a8CMgqO9PFWoI2LYY4QTxFd2JFf6oc9ChUZB2pL1fAleSo5jAEeNhlJfrsnHkQlOuaViFRo5v",
		"application":["384FF-FA7C3"],
	}
- users.txt: contain list of users, separate by comma
	+ if empty, message will be sent broadcast
	+ each user separate by comma. Ex: 0123, 0493, 9402
- custom_data.txt: custom data to user. Ex: open channel


Execute: open cmd/terminal, cd to folder, run: java -jar PushByUserId.jar

Notes: 
- To see response from server, run PushByUserId.sh
- All files have to put in same place

