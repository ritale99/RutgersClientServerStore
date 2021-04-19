package com.RUStore;

/* any necessary Java packages here */
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class RUStoreClient {

	/* any necessary class members here */
	private String host;
	private int port;
	private DataOutputStream toServer;
	private BufferedReader fromServer;
	private Socket sock;
	private ObjectInputStream ois;
	
	/**
	 * RUStoreClient Constructor, initializes default values
	 * for class members
	 *
	 * @param host	host url
	 * @param port	port number
	 */
	public RUStoreClient(String host, int port) {

		// Implement here
		this.host = host;
		this.port = port;

	}

	/**
	 * Opens a socket and establish a connection to the object store server
	 * running on a given host and port.
	 *
	 * @return		n/a, however throw an exception if any issues occur
	 * @throws IOException 
	 */
	public void connect() throws IOException{
	
		// Implement here
		sock = new Socket(host, port);	// connect to specified host and port
		toServer = new DataOutputStream(sock.getOutputStream());
		fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		ois = new ObjectInputStream(sock.getInputStream());
		
	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT be 
	 * overwritten
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param data	byte array representing arbitrary data object
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int put(String key, byte[] data) throws IOException {

		//grab the size of the request
		int bytesRequest = "put".getBytes().length;
		
		//combine the request and the key
		String reqKey = "put"+key;
		
		//get the byte array from this request and key
		byte[] requestBytes = reqKey.getBytes();
		
		//create a larger array to copy the key and pad zeros in case it is needed
		byte[] keyBytesPadded = new byte [1024 + bytesRequest];
		
		//copy over the array
		System.arraycopy(requestBytes, 0, keyBytesPadded, 0, requestBytes.length);
		
		//grab the newLine Character
		byte[] newline = System.getProperty("line.separator").getBytes();
		
		//grab the size of the newLine character
		int newLine = newline.length;
		
		//new array for the entire message
		byte[] completeMessage = new byte[keyBytesPadded.length + data.length + newLine];
		
		//copy everything into it
		System.arraycopy(keyBytesPadded, 0, completeMessage, 0, keyBytesPadded.length);
		System.arraycopy(data, 0, completeMessage, keyBytesPadded.length, data.length);
		System.arraycopy(newline, 0, completeMessage, data.length+keyBytesPadded.length, newLine);
		
		//want to send over completeMessage to the server
		toServer.write(completeMessage);

		return 0;

	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT 
	 * be overwritten.
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param file_path	path of file data to transfer
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int put(String key, String file_path) throws IOException {

		//grab the size of the request
		int bytesRequest = "puf".getBytes().length;
						
		//combine the request and the key
		String reqKey = "puf"+key;
				
		//get the byte array from this request and key
		byte[] requestBytes = reqKey.getBytes();
				
		//create a larger array to copy the key and pad zeros in case it is needed
		byte[] keyBytesPadded = new byte [1024 + bytesRequest];
				
		//copy over the array
		System.arraycopy(requestBytes, 0, keyBytesPadded, 0, requestBytes.length);
						
		//grab the newLine Character
		byte[] newline = System.getProperty("line.separator").getBytes();
						
		//grab the size of the newLine character
		int newLine = newline.length;
						
		//new array for the entire message
		byte[] completeMessage = new byte[keyBytesPadded.length + newLine];
				
		//copy everything into it
		System.arraycopy(keyBytesPadded, 0, completeMessage, 0, keyBytesPadded.length);
		System.arraycopy(newline, 0, completeMessage, keyBytesPadded.length, newLine);
				
		//send the message to the server
		toServer.write(completeMessage);
				
		//convert file to byte array
		byte[] fb = Files.readAllBytes(Paths.get(file_path));
		
		//send the length of the file 
		toServer.writeInt(fb.length);
		
		//then send the file bytes
		toServer.write(fb);
	
		System.out.println("placed file in server");
		
		return 0;

	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server.
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		object data as a byte array, null if key doesn't exist.
	 *        		Throw an exception if any other issues occur.
	 * @throws IOException 
	 */
	public byte[] get(String key) throws IOException {

		//grab the size of the request
		int bytesRequest = "get".getBytes().length;
				
		//combine the request and the key
		String reqKey = "get"+key;
		
		//get the byte array from this request and key
		byte[] requestBytes = reqKey.getBytes();
		
		//create a larger array to copy the key and pad zeros in case it is needed
		byte[] keyBytesPadded = new byte [1024 + bytesRequest];
		
		//copy over the array
		System.arraycopy(requestBytes, 0, keyBytesPadded, 0, requestBytes.length);
				
		//grab the newLine Character
		byte[] newline = System.getProperty("line.separator").getBytes();
				
		//grab the size of the newLine character
		int newLine = newline.length;
				
		//new array for the entire message
		byte[] completeMessage = new byte[keyBytesPadded.length + newLine];
		
		//copy everything into it
		System.arraycopy(keyBytesPadded, 0, completeMessage, 0, keyBytesPadded.length);
		System.arraycopy(newline, 0, completeMessage, keyBytesPadded.length, newLine);
		
		//send the message to the server
		toServer.write(completeMessage);
				
		String response = fromServer.readLine();
		while(fromServer.ready()) {
			response = fromServer.readLine();
			if(response!=null) {break;}
		}
		
		//get the response from the server as a string
		//String response = fromServer.readLine();
		//System.out.println("server replied"+response);
		
		//return it as an array of bytes
		return response.getBytes();

	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server and places it in a file. 
	 * 
	 * @param key	key associated with the object
	 * @param	file_path	output file path
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int get(String key, String file_path) throws IOException {

		//grab the size of the request
		int bytesRequest = "gef".getBytes().length;
						
		//combine the request and the key
		String reqKey = "gef"+key;
				
		//get the byte array from this request and key
		byte[] requestBytes = reqKey.getBytes();
				
		//create a larger array to copy the key and pad zeros in case it is needed
		byte[] keyBytesPadded = new byte [1024 + bytesRequest];
				
		//copy over the array
		System.arraycopy(requestBytes, 0, keyBytesPadded, 0, requestBytes.length);
						
		//grab the newLine Character
		byte[] newline = System.getProperty("line.separator").getBytes();
						
		//grab the size of the newLine character
		int newLine = newline.length;
						
		//new array for the entire message
		byte[] completeMessage = new byte[keyBytesPadded.length + newLine];
				
		//copy everything into it
		System.arraycopy(keyBytesPadded, 0, completeMessage, 0, keyBytesPadded.length);
		System.arraycopy(newline, 0, completeMessage, keyBytesPadded.length, newLine);
				
		//send the message to the server
		toServer.write(completeMessage);
		
		//grab the response from the server with this
		DataInputStream din = new DataInputStream(sock.getInputStream()); 
		
		//send over the message
		//toServer.write(completeMessage);
		
		
		
		//problem here is reading the int before the server sends it 
		
		//get the size of the file being received 
		int length = din.readInt();
		
		if(length > 0 ) {
			byte [] message = new byte [length]; 
			din.readFully(message, 0, message.length);
			
			
			File output = new File(file_path);
			output.createNewFile();
			
			try (FileOutputStream outputStream = new FileOutputStream(output)) {
			    outputStream.write(message);
			}
			
		}
		
		
	
		//din.close();
				
		
		return 0;

	}

	/**
	 * Removes data object associated with a given key 
	 * from the object store server. Note: No need to download the data object, 
	 * simply invoke the object store server to remove object on server side
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int remove(String key) throws IOException {

		//grab the size of the request
		int bytesRequest = "rem".getBytes().length;
								
		//combine the request and the key
		String reqKey = "rem"+key;
						
		//get the byte array from this request and key
		byte[] requestBytes = reqKey.getBytes();
						
		//create a larger array to copy the key and pad zeros in case it is needed
		byte[] keyBytesPadded = new byte [1024 + bytesRequest];
						
		//copy over the array
		System.arraycopy(requestBytes, 0, keyBytesPadded, 0, requestBytes.length);
								
		//grab the newLine Character
		byte[] newline = System.getProperty("line.separator").getBytes();
								
		//grab the size of the newLine character
		int newLine = newline.length;
								
		//new array for the entire message
		byte[] completeMessage = new byte[keyBytesPadded.length + newLine];
						
		//copy everything into it
		System.arraycopy(keyBytesPadded, 0, completeMessage, 0, keyBytesPadded.length);
		System.arraycopy(newline, 0, completeMessage, keyBytesPadded.length, newLine);
						
		//send the message to the server
		toServer.write(completeMessage);
		
		return 0;

	}

	/**
	 * Retrieves of list of object keys from the object store server
	 * 
	 * @return		List of keys as string array, null if there are no keys.
	 *        		Throw an exception if any other issues occur.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public String[] list() throws IOException {

		//string for the request
		String req = "lis";
				
		//get the byte array from this request and key
		byte[] requestBytes = req.getBytes();
		
		//grab the newLine Character
		byte[] newline = System.getProperty("line.separator").getBytes();
				
		//grab the size of the newLine character
		int newLine = newline.length;
				
		//new array for the entire message
		byte[] completeMessage = new byte[requestBytes.length + newLine];
		
		//copy everything into it
		System.arraycopy(requestBytes, 0, completeMessage, 0, requestBytes.length);
		System.arraycopy(newline, 0, completeMessage, requestBytes.length, newline.length);
		
		//ois = new ObjectInputStream(sock.getInputStream());
		
		//send over the request to the server
		toServer.write(completeMessage);
		
		String[] result = null;
		try {
			result = (String [])ois.readObject();
	        System.out.println("recieved array:" + Arrays.toString(result));
			return result;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ois.close();
        
        return null;
		

	}

	/**
	 * Signals to server to close connection before closes 
	 * the client socket.
	 * 
	 * @return		n/a, however throw an exception if any issues occur
	 * @throws IOException 
	 */
	public void disconnect() throws IOException {
		
		sock.close();

	}

}
