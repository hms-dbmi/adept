package edu.harvard.hms.dbmi.avillach.cliniscope.security;


import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import edu.harvard.hms.dbmi.avillach.cliniscope.ClinicalNoteService;
import edu.harvard.hms.dbmi.avillach.cliniscope.transfer.ClinicalNoteDTO;

@Component
public class ClinicalNoteDecryptor {

	private static final Logger LOGGER = Logger.getLogger(ClinicalNoteDecryptor.class);
	private byte[] key;

	public ClinicalNoteDecryptor() {
		LOGGER.debug("****CREATED DECRYPTION FILTER****");	
		try {
			key = Base64.getDecoder().decode(IOUtils.toString(new FileInputStream("/opt/local/adept/app/encryption_key"), "UTF-8").trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String encryptNoteText(String plaintext) {
		SecureRandom secureRandom = new SecureRandom();
		SecretKey secretKey = new SecretKeySpec(key, "AES");
		byte[] iv = new byte[12]; //NEVER REUSE THIS IV WITH SAME KEY
		secureRandom.nextBytes(iv);
		byte[] cipherText;
		try {
			final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
			cipher.update(plaintext.getBytes());
			cipherText = cipher.doFinal();
			ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
			byteBuffer.putInt(iv.length);
			byteBuffer.put(iv);
			byteBuffer.put(cipherText);
			byte[] cipherMessage = byteBuffer.array();
			return Base64.getEncoder().encodeToString(cipherMessage);
			
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return plaintext;
	}

	public String decryptNoteText(String encrypted) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(encrypted));
		int ivLength = byteBuffer.getInt();
		byte[] iv = new byte[ivLength];
		byteBuffer.get(iv);
		byte[] cipherText = new byte[byteBuffer.remaining()];
		byteBuffer.get(cipherText);
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));
			byte[] plainText= cipher.doFinal(cipherText);
			return new String(plainText);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return encrypted;
	}

}
