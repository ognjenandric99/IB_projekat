package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import keystore.KeyStoreReader;
import model.mailclient.MailBody;

import org.apache.xml.security.utils.JavaUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import support.MailHelper;
import support.MailReader;
import util.Base64;
import util.GzipUtil;

public class ReadMailClient extends MailClient {

	public static long PAGE_SIZE = 3;
	public static boolean ONLY_FIRST_PAGE = true;
	
	private static final String KEY_FILE = "./data/session.key";
	private static final String IV1_FILE = "./data/iv1.bin";
	private static final String IV2_FILE = "./data/iv2.bin";
	
	private static final String KEY_STORE_PASS_FOR_PRIVATE_KEY = "1234";
	private static final String KEY_STORE_FILE = "./data/userb.jks";
	private static final String KEY_STORE_PASS = "1234";
	private static final String KEY_STORE_ALIAS = "user b";
	
	public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, MessagingException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();
        ArrayList<MimeMessage> mimeMessages = new ArrayList<MimeMessage>();
        
        String user = "me";
        String query = "is:unread label:INBOX";
        
        List<Message> messages = MailReader.listMessagesMatchingQuery(service, user, query, PAGE_SIZE, ONLY_FIRST_PAGE);
        for(int i=0; i<messages.size(); i++) {
        	Message fullM = MailReader.getMessage(service, user, messages.get(i).getId());
        	
        	MimeMessage mimeMessage;
			try {
				
				mimeMessage = MailReader.getMimeMessage(service, user, fullM.getId());
				
				System.out.println("\n Message number " + i);
				System.out.println("From: " + mimeMessage.getHeader("From", null));
				System.out.println("Subject: " + mimeMessage.getSubject());
				System.out.println("Body: " + MailHelper.getText(mimeMessage));
				System.out.println("\n");
				
				mimeMessages.add(mimeMessage);
	        
			} catch (MessagingException e) {
				e.printStackTrace();
			}	
        }
        
        System.out.println("Select a message to decrypt:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        
	    String answerStr = reader.readLine();
	    Integer answer = Integer.parseInt(answerStr);
	    
		MimeMessage chosenMessage = mimeMessages.get(answer);
	    
        //TODO: Decrypt a message and decompress it. The private key is stored in a file.
		Cipher aesCipherDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
		//SecretKey secretKey = new SecretKeySpec(JavaUtils.getBytesFromFile(KEY_FILE), "AES");
		String str = MailHelper.getText(chosenMessage);
		MailReader.getAttachments(service, "me", answer.toString());
//		MailBody mb = new MailBody(str);
//		String secretKeyStr = mb.getEncKey();
//		KeyStore keyStore = KeyStoreReader.readKeyStore(KEY_STORE_FILE, KEY_STORE_PASS.toCharArray());
//		PrivateKey privateKey = KeyStoreReader.getPrivateKeyFromKeyStore(keyStore,KEY_STORE_ALIAS,KEY_STORE_PASS_FOR_PRIVATE_KEY.toCharArray());
//		try {
//			Security.addProvider(new BouncyCastleProvider());
//			Cipher rsaCipherDec = Cipher.getInstance("RSA/ECB/PKCS1Padding","BC");
//			rsaCipherDec.init(Cipher.DECRYPT_MODE, privateKey);
//			System.out.println(privateKey);
//			byte[] secretKeyByte = rsaCipherDec.doFinal(Base64.decode(secretKeyStr));
//			SecretKey secretKey = new SecretKeySpec(secretKeyByte, "AES");
//			
//			String iv1Str = mb.getIV1();
//			
//			IvParameterSpec ivParameterSpec1 = new IvParameterSpec(Base64.decode(iv1Str));
//			aesCipherDec.init(Cipher.DECRYPT_MODE,secretKey, ivParameterSpec1);
//			
//			byte[] telo = Base64.decode(mb.getEncMessage());
//			String receivedBodyText = new String(aesCipherDec.doFinal(telo));
//			String decompressedBodyText = GzipUtil.decompress(Base64.decode(receivedBodyText));
//			System.out.println("Body txt : "+decompressedBodyText);
//			
//			String iv2str = mb.getIV2();
//			IvParameterSpec ivParameterSpec2 = new IvParameterSpec(Base64.decode(iv2str));
//			aesCipherDec.init(Cipher.DECRYPT_MODE,secretKey, ivParameterSpec2);
//			
//			String decryptedSubjectTxt = new String(aesCipherDec.doFinal(Base64.decode(chosenMessage.getSubject())));
//			String decompressedSubjectTxt = GzipUtil.decompress(Base64.decode(decryptedSubjectTxt));
//			System.out.println("Subject text: " + new String(decompressedSubjectTxt));
//			
//			
//		} catch (NoSuchAlgorithmException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
