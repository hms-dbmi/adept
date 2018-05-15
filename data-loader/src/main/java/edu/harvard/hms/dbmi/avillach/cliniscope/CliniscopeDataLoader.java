package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import edu.harvard.hms.dbmi.avillach.cliniscope.entities.ClinicalNote;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesGroupRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.CTakesHitRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.ClinicalNoteMappingRepo;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.ClinicalNoteRepository;
import edu.harvard.hms.dbmi.avillach.cliniscope.repositories.PatientMappingRepo;

@Component // Main is a Spring-managed bean too, since it have @Autowired property
public class CliniscopeDataLoader {
	private static byte[] key;
	@Autowired
	public CTakesOutputLoader ctakesLoader;
	@Autowired
	public ClinicalNoteRepository clinicalNoterepo;
	@Autowired
	public CTakesGroupRepository ctakesGroupRepo;
	@Autowired
	public CTakesHitRepository ctakesHitRepo;
	@Autowired
	public PatientMappingRepo patientMappingRepo;
	@Autowired
	public ClinicalNoteMappingRepo noteMappingRepo;
	
	private static String inputPath;

	public static void main(String [] args) {
		try {
			key = Base64.getDecoder().decode(IOUtils.toString(new FileInputStream("encryption_key"), "UTF-8").trim());
		} catch (FileNotFoundException e) {
			System.out.println("You must create and save a Base64 encoded key in a file called encryption_key "
					+ "and it must be in the same folder this application is run from and be  readable by the user this application runs as.");
			throw new RuntimeException(e);
		} catch (IOException e) {
			System.out.println("Unknown IOException thrown while attempting to read /opt/local/cliniscope/encryption_key.");
			throw new RuntimeException(e);
		}
		
		if(args.length < 1) {
			System.out.println("You must provide a path to the input files as the only command line argument.");
		}
		try {
			if(new File(args[0]).isDirectory()) {
				inputPath = args[0];
			}else {
				throw new RuntimeException("The input path must be a directory.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		ApplicationContext ctx = 
				new ClassPathXmlApplicationContext("beans.xml"); // Use annotated beans from the specified package

		CliniscopeDataLoader main = ctx.getBean(CliniscopeDataLoader.class);

		main.loadData();
	}
	
	/**
	 * This method loads note text for all clinical notes. If a note file is not present on the system then
	 * the note is overwritten to be blank at this time. This should be fixed to be smarter.
	 */
	public void loadAllClinicalNotes(){
		for(ClinicalNote note : clinicalNoterepo.listNotes()){
			String noteText = loadNote(note);
			String encryptedNoteText = encryptNoteText(noteText);
			note.setNoteText(encryptedNoteText);
			clinicalNoterepo.ensureExists(ImmutableList.of(note));
			System.out.println("loaded " + note.getClinicalNoteId());
		}
	}

	private String loadNote(ClinicalNote note) {
		String filename = patientMappingRepo.getById(note.getPatientId()).getSourceId()+"_"+noteMappingRepo.getById(note.getClinicalNoteId()).getSourceId()+".txt";
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File(inputPath + "/Notes/" + filename));
			return IOUtils.toString(fileReader);
		} catch (FileNotFoundException e) {
			System.out.println("filename");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("filename");
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fileReader);
		}
		return "";
	}

	public void loadData() {
		long startTime = System.currentTimeMillis();
		ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*4);
		for(File file : new File(inputPath + "/Summary_relations").listFiles()){
			ex.execute(new Runnable(){
				final File files = file;
				@Override
				public void run() {
					FileInputStream ctakesRelationsFile = null;
					try{
						ctakesRelationsFile = new FileInputStream(files);
						System.out.println(files.getName() + " loading");
						ctakesLoader.loadRelationsFile(ctakesRelationsFile);			
						System.out.println(files.getName() + " loaded");							
					}catch(Exception e){
						System.out.println(files.getName());
						e.printStackTrace();
					}finally{
						if(ctakesRelationsFile!=null){
							try {
								ctakesRelationsFile.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			});

		}
		for(File file : new File(inputPath + "/Summary_notinrelation").listFiles()){
			ex.execute(new Runnable(){
				final File files = file;
				@Override
				public void run() {
					FileInputStream ctakesRelationsFile = null;
					try{
						ctakesRelationsFile = new FileInputStream(files);	
						System.out.println(files.getName() + " loading");
						ctakesLoader.loadUnrelatedMentionsFile(files.getName(), ctakesRelationsFile);			
						System.out.println(files.getName() + " loaded");							
					}catch(Exception e){
						System.out.println(files.getName());
						e.printStackTrace();
					}finally{
						if(ctakesRelationsFile!=null){
							try {
								ctakesRelationsFile.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
		try {
			ex.shutdown();
			while(!ex.awaitTermination(5, TimeUnit.SECONDS)){
				System.out.println("Waiting for load to complete : " + ((System.currentTimeMillis() - startTime)/1000) + " seconds so far.");
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		loadAllClinicalNotes();
	}

	private String encryptNoteText(String plaintext) {
		SecureRandom secureRandom = new SecureRandom();
		SecretKey secretKey = new SecretKeySpec(key, "AES");
		byte[] iv = new byte[12]; //NEVER REUSE THIS IV WITH SAME KEY
		secureRandom.nextBytes(iv);
		byte[] cipherText;
		try {
			final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
			System.out.println("Length of plaintext : " + plaintext.length());
			byte[] plaintextBytes = plaintext.getBytes("UTF-8");
			cipherText = new byte[cipher.getOutputSize(plaintextBytes.length)];
			cipher.doFinal(plaintextBytes, 0, plaintextBytes.length, cipherText, 0);
			System.out.println("Length of cipherText : " + cipherText.length);
			ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
			byteBuffer.putInt(iv.length);
			byteBuffer.put(iv);
			byteBuffer.put(cipherText);
			byte[] cipherMessage = byteBuffer.array();
			return Base64.getEncoder().encodeToString(cipherMessage);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | ShortBufferException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return plaintext;
	}



}