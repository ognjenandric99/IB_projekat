package support;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

// ...

public class MailWritter {
	
  // ...

  /**
   * Send an email from the user's mailbox to its recipient.
   *
   * @param service Authorized Gmail API instance.
   * @param userId User's email address. The special value "me"
   * can be used to indicate the authenticated user.
   * @param email Email to be sent.
   * @throws MessagingException
   * @throws IOException
   */
  public static void sendMessage(Gmail service, String userId, MimeMessage email)
      throws MessagingException, IOException {
    
	Message message = createMessageWithEmail(email);
    message = service.users().messages().send(userId, message).execute();

    System.out.println("Message id: " + message.getId());
    System.out.println(message.toPrettyString());
  }
  
  
  private static Message createMessageWithEmail(MimeMessage email)
      throws MessagingException, IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    email.writeTo(baos);
    String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }

  
  public static MimeMessage createEmailWithAttachment(String to,
          String from,
          String subject,
          String bodyText,
          File file)
throws MessagingException, IOException {
Properties props = new Properties();
Session session = Session.getDefaultInstance(props, null);

MimeMessage email = new MimeMessage(session);

email.setFrom(new InternetAddress(from));
email.addRecipient(javax.mail.Message.RecipientType.TO,
new InternetAddress(to));
email.setSubject(subject);

MimeBodyPart mimeBodyPart = new MimeBodyPart();
mimeBodyPart.setContent(bodyText, "text/plain");

Multipart multipart = new MimeMultipart();
multipart.addBodyPart(mimeBodyPart);

mimeBodyPart = new MimeBodyPart();
DataSource source = new FileDataSource(file);

mimeBodyPart.setDataHandler(new DataHandler(source));
mimeBodyPart.setFileName(file.getName());

multipart.addBodyPart(mimeBodyPart);
email.setContent(multipart);

return email;
}


public static Message sendMessageWithAttachment(Gmail service,
  String userId,
  MimeMessage emailContent)
throws MessagingException, IOException {
Message message = createMessageWithEmail(emailContent);
message = service.users().messages().send(userId, message).execute();

System.out.println("Message id: " + message.getId());
System.out.println(message.toPrettyString());
return message;
}
/**
* Create a Message from an email
*
* @param email Email to be set to raw of message
* @return Message containing base64url encoded email.
* @throws IOException
* @throws MessagingException
*/
  // ...

}