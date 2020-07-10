package support;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.Gmail.Users.Messages.Attachments;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

// ...

public class MailReader {
	public static XML_functions xmlFunkcije = new XML_functions();
  // ...


  /**
   * List all Messages of the user's mailbox matching the query.
   *
   * @param service Authorized Gmail API instance.
   * @param userId User's email address. The special value "me"
   * can be used to indicate the authenticated user.
   * @param query String used to filter the Messages listed.
   * @throws IOException
   */
  public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
      String query, long pageSize, boolean onlyFirstPage) throws IOException {
    ListMessagesResponse response = service.users().messages().list(userId).setQ(query).setMaxResults(pageSize).execute();

    List<Message> messages = new ArrayList<Message>();
    while (response.getMessages() != null) {
      messages.addAll(response.getMessages());
      
	  if (response.getNextPageToken() != null && !onlyFirstPage) {
		  String pageToken = response.getNextPageToken();
		  response = service.users().messages().list(userId).setQ(query)
				  .setPageToken(pageToken).execute();
	  } else {
		  break;
	  }
    }

    //for (Message message : messages) {
    //   System.out.println(message.toPrettyString());
    //}

    return messages;
  }

  /**
   * List all Messages of the user's mailbox with labelIds applied.
   *
   * @param service Authorized Gmail API instance.
   * @param userId User's email address. The special value "me"
   * can be used to indicate the authenticated user.
   * @param labelIds Only return Messages with these labelIds applied.
   * @throws IOException
   */
  public static List<Message> listMessagesWithLabels(Gmail service, String userId,
      List<String> labelIds, long maxResults, boolean onlyFirstPage) throws IOException {
    ListMessagesResponse response = service.users().messages().list(userId)
        .setLabelIds(labelIds).setMaxResults(maxResults).execute();

    List<Message> messages = new ArrayList<Message>();
    while (response.getMessages() != null) {
      messages.addAll(response.getMessages());

      if (response.getNextPageToken() != null && !onlyFirstPage) {
        String pageToken = response.getNextPageToken();
        response = service.users().messages().list(userId).setLabelIds(labelIds)
            .setPageToken(pageToken).execute();
      } else {
        break;
      }
    }

    /*for (Message message : messages) {
      System.out.println(message.toPrettyString());
    }*/

    return messages;
  }
  
  public static Message getMessage(Gmail service, String userId, String messageId)
  {
      try
      {
          return service.users().messages().get(userId, messageId).execute();
      }
      catch (Exception e)
      {
           System.out.println("An error occurred: " + e.getStackTrace());
      }

      return null;
  }

  public static MimeMessage getMimeMessage(Gmail service, String userId, String messageId)
	      throws IOException, MessagingException {
	    Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

	    Base64 base64Url = new Base64(true);
	    @SuppressWarnings("static-access")
		byte[] emailBytes = base64Url.decodeBase64(message.getRaw());

	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

	    return email;
	  }

  public static void getAttachments(Gmail service, String userId, String messageId)
		  throws IOException {
	    Message message = service.users().messages().get(userId, messageId).execute();
	    List<MessagePart> parts = message.getPayload().getParts();
	    for (MessagePart part : parts) {
	      if (part.getFilename() != null && part.getFilename().length() > 0) {
	        String filename = part.getFilename();
	        String attId = part.getBody().getAttachmentId();
	        MessagePartBody attachPart = service.users().messages().attachments().
	            get(userId, messageId, attId).execute();

	        Base64 base64Url = new Base64(true);
	        byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());
	        FileOutputStream fileOutFile =
	            new FileOutputStream(xmlFunkcije.getPathDoFajla()+"read/" + filename);
	        fileOutFile.write(fileByteArray);
	        fileOutFile.close();
	      }
	    }
  }
}

