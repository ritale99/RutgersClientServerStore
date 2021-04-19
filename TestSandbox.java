package com.RUStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * This TestSandbox is meant for you to implement and extend to 
 * test your object store as you slowly implement both the client and server.
 * 
 * If you need more information on how an RUStorageClient is used
 * take a look at the RUStoreClient.java source as well as 
 * TestSample.java which includes sample usages of the client.
 */
public class TestSandbox{

	public static void main(String[] args) throws IOException {

		System.out.println("Start of sandbox program");
		
		// Create a new RUStoreClient
		RUStoreClient client = new RUStoreClient("localhost", 12345);

		// Open a connection to a remote service
		System.out.println("Connecting to object server...");
		try {
			client.connect();
			System.out.println("Established connection to server.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to connect to server.");
		}
				
		//test storing and getting of strings
		String stringValue = "yerr";
		client.put("tTest", stringValue.getBytes());

		byte[] x = client.get("tTest");
		String respon = new String(x);
		

		System.out.println("got back "+respon);
		
		client.put("ttt", "gamg".getBytes());
		client.list();
		
		String fileKey = "chapter1.txt";
		String inputPath = "./inputfiles/lofi.mp3";
		String outputPath = "./outputfiles/lofi_.mp3";

		//client.put(fileKey, inputPath);
		//client.list();
		//client.get(fileKey, outputPath);
		
		//client.list(); 
		
		
		
		// PUT File
		try {
			System.out.println("Putting file \"" + inputPath + "\" with key \"" + fileKey + "\"");
			int ret = client.put(fileKey, inputPath);
			if(ret == 0) {
				System.out.println("Successfully put file!");
				client.list();
			}else {
				System.out.println("Failed to put file \"" + inputPath + "\" with key \"" + fileKey + "\". Key already exists. (INCORRECT RETURN)");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Failed to put file \"" + inputPath + "\" with key \"" + fileKey + "\". Exception occured.");
		} 

		// GET File
		try {
			System.out.println("Getting object with key \"" + fileKey + "\"");
			int ret = client.get(fileKey, outputPath);
			if(ret == 0) {
				File fileIn = new File(inputPath);
				File fileOut = new File(outputPath);
				if(fileOut.exists()) {
					byte [] fileInBytes = Files.readAllBytes(fileIn.toPath());
					byte[] fileOutBytes = Files.readAllBytes(fileOut.toPath());
					if(Arrays.equals(fileInBytes, fileOutBytes)) {
						System.out.println("File contents are equal! Successfully Retrieved File");
					}else {
						System.out.println("File contents are not equal! Got garbage data. (BAD FILE DOWNLOAD)");
					}
					System.out.println("Deleting downloaded file.");
					Files.delete(fileOut.toPath());
				}else {
					System.out.println("No file downloaded. (BAD FILE DOWNLOAD)");
				}
			}else {
				System.out.println("Failed getting object with key \""  + "\". Key doesn't exist. (INCORRECT RETURN)");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Failed getting object with key \"" + "\" Exception occured.");
		}
		
		
		client.list();
		

		 fileKey = "html";
		 inputPath = "./inputfiles/dummysite.html";
		 outputPath = "./outputfiles/solutions.html";
		
		
		
		// PUT File
				try {
					System.out.println("Putting file \"" + inputPath + "\" with key \"" + fileKey + "\"");
					int ret = client.put(fileKey, inputPath);
					if(ret == 0) {
						System.out.println("Successfully put file!");
						client.list();
					}else {
						System.out.println("Failed to put file \"" + inputPath + "\" with key \"" + fileKey + "\". Key already exists. (INCORRECT RETURN)");
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("Failed to put file \"" + inputPath + "\" with key \"" + fileKey + "\". Exception occured.");
				} 

				// GET File
				try {
					System.out.println("Getting object with key \"" + fileKey + "\"");
					int ret = client.get(fileKey, outputPath);
					if(ret == 0) {
						File fileIn = new File(inputPath);
						File fileOut = new File(outputPath);
						if(fileOut.exists()) {
							byte [] fileInBytes = Files.readAllBytes(fileIn.toPath());
							byte[] fileOutBytes = Files.readAllBytes(fileOut.toPath());
							if(Arrays.equals(fileInBytes, fileOutBytes)) {
								System.out.println("File contents are equal! Successfully Retrieved File");
							}else {
								System.out.println("File contents are not equal! Got garbage data. (BAD FILE DOWNLOAD)");
							}
							System.out.println("Deleting downloaded file.");
							//Files.delete(fileOut.toPath());
						}else {
							System.out.println("No file downloaded. (BAD FILE DOWNLOAD)");
						}
					}else {
						System.out.println("Failed getting object with key \""  + "\". Key doesn't exist. (INCORRECT RETURN)");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("Failed getting object with key \"" + "\" Exception occured.");
				}
		
		client.list();
		System.out.println("sandbox testing concluded");
	}

}
