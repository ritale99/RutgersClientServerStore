package com.RUStore;

/* any necessary Java packages here */
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Set;

public class RUStoreServer {

	 /* any necessary class members here */
	private static BufferedReader fromClient = null; 
	private static DataOutputStream toClient = null; 
	private static ObjectOutputStream out = null; 
	private static ServerSocket svc = null; 
	private static Socket conn = null;
		
	//hashmap to store object data
	static HashMap<String, byte[]> mapData = new HashMap<String, byte[]>();
	
	/* any necessary helper methods here */

	/**
	 * RUObjectServer Main(). Note: Accepts one argument -> port number
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException{
		
		System.out.println("Starting server");
		
		// Check if at least one argument that is potentially a port number
		if(args.length != 1) {
			System.out.println("Invalid number of arguments. You must provide a port number.");
			return;
		}

		// Try and parse port # from argument
		int port = Integer.parseInt(args[0]);

		// listen on specified port
		svc = new ServerSocket(port);  
		
		// wait for a connection
		conn = svc.accept();	 
		
		fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		toClient = new DataOutputStream(conn.getOutputStream());
		out = new ObjectOutputStream(conn.getOutputStream());
		
		//sample 3 letter request, to get size of it 
		String requestNum = "get";
		int numBytes = requestNum.getBytes().length;

		String line;
		while  ((line = fromClient.readLine()) != null) {	// read the data from the client
			// show what we got
			//System.out.println("got line \"" + line + "\"");	
			
			if(line.length()>=3) {
				//extract the request
				String request = line.substring(0, numBytes);
			
				//this is the put for strings 
				if(request.equals("put")) {
					String key = line.substring(numBytes, 1024+numBytes);
					System.out.println("putting " + key);
					byte [] data = line.substring(numBytes+1024, line.length()).getBytes();
					put(key, data);
					continue;
				}
				
				//we use a different workflow for sending over files 
				if(request.equals("puf")) {
					String key = line.substring(numBytes, 1024+numBytes);
					System.out.println("putting " + key);
					putFile(key);
					continue;
				}
				
				//get workflow for getting files
				if(request.equals("gef")) {
					String key = line.substring(numBytes, 1024+numBytes); 
					System.out.println("getting " + key);
					getFile(key);
					continue; 
					
				}
				
				if(request.equals("get")) {
					
					String key = line.substring(numBytes, 1024+numBytes);
					System.out.println("getting " + key);
										
					//send the response
					toClient.write(mapData.get(key));
					
					//grab the newLine Character
					byte[] newline = System.getProperty("line.separator").getBytes();
					
					//send over the newline character to signify end of the response
					toClient.write(newline);
					
					continue;
					
				}
				
				if(request.equals("rem")) {
					String key = line.substring(numBytes, 1024+numBytes); 
					mapData.remove(key);
					continue;
				}
				
				//this is to disconnect
				if(request.equals("dis")) {
					System.out.println("closing the connection ");
					conn.close();		// close connection
						
				}
				
				if(request.equals("lis")) {
					//first check if it is empty
					
					
					System.out.println("sending list of keys");
					
					Set<String> keys = mapData.keySet();
					String [] keyArr = keys.toArray(new String[keys.size()]);
			
					
					out.writeObject(keyArr);
					
					
					continue;
				}
			}
			
		}
		
		System.out.println("closing the connection");
		conn.close();		// close connection
		

	}
	
	private static int put(String key, byte[] data) {
		//place the data using the specified key into a hashmap
		mapData.put(key, data);
		return 1;
	}
	
	private static int putFile(String key) throws IOException {
		DataInputStream din = new DataInputStream(conn.getInputStream());
		int length = din.readInt(); 
		if(length > 0 ) {
			byte [] message = new byte [length];
			din.readFully(message, 0, message.length);
			mapData.put(key, message);
		}
		
		else {
			return -1; 
		}
	
		return 1; 
	}
	
	
	//want to send over the file to the client
	private static int getFile(String key) throws IOException {
		byte [] message = mapData.get(key);
		toClient.writeInt(message.length);
		toClient.write(message);
		return 1; 
	}
	

	
	

}
