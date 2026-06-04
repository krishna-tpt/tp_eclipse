package com.einvoice.inboundfilesread;

public class InBoundFileReadMain {

	public static void main(String[] args) {
		System.out.println("DB object created");
		DB db = new DB();
		System.out.println("verifyFilesProcessed Started");
		
		db.verifyFilesProcessed();
		
		System.out.println("verifyFilesProcessed ended");
		System.out.println("readJsonFiles Started");
		
		db.readJsonFiles();
		
		System.out.println("readJsonFiles Ended");
		System.out.println("processingFiles Started");
		
		db.processingFiles();
		System.out.println("Total Files KSEF NO Updated : "+db.totalFilesUpdatedKSEF);
		
		System.out.println("processingFiles Ended");
	}
}
