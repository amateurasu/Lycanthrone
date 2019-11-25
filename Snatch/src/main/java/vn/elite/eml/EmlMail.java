package vn.elite.eml;

import org.jsoup.Jsoup;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmlMail {
    private static SimpleDateFormat time_up = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
    private static SimpleDateFormat time_down = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private final File emlFile;
    private MimeMessage message;

    public EmlMail(Path path) {
        this.emlFile = path.toFile();
        construct();
    }

    public EmlMail(String path) {
        this.emlFile = new File(path);
        construct();
    }

    public static String getAddresses(Address[] addresses) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (Address address : addresses) {
            String text = decode(address.toString());
            result.append(filter(text)).append("; ");
        }
        return result.toString();
    }

    public static String decode(String code) throws UnsupportedEncodingException {
        return MimeUtility.decodeText(code);
    }

    private static String filter(String text) {
        return text
                .replace("\n", "")
                .replace("\r", "")
                .replace("\"", "");
    }

    public static void main(String[] args) {
        EmlMail mail1 = new EmlMail("E:\\Projects\\folder\\test.eml");
        EmlMail mail2 = new EmlMail("E:\\Projects\\folder\\How about this_.eml");

        test(mail1);
        test(mail2);
    }

    public static void test(EmlMail mail) {
        try {
            System.out.println("Mail test");
            System.out.println("Senders     : " + getAddresses(mail.getSenders()));
            System.out.println("Recipients  : " + getAddresses(mail.getRecipients()));
            System.out.println("Subject     : " + mail.getSubject());
            System.out.println("Attachments : " + mail.getAttachments().toString());
            System.out.println("Time        : " + mail.getTimeUp());
            System.out.println("Time        : " + mail.getTimeDown());
            System.out.println("Content     : " + mail.getContent());
            System.out.println("Snippet     : " + mail.getSnippet());
            System.out.println("------------------------------------------------");
        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(EmlMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void construct() {
        System.setProperty("mail.mime.charset", "utf8");

        Properties props = System.getProperties();
        InputStream source = null;
        try {
            props.put("mail.host", "smtp.dummydomain.com");
            props.put("mail.transport.protocol", "smtp");
            Session mailSession = Session.getInstance(props, null);
            source = new FileInputStream(emlFile);
            message = new MimeMessage(mailSession, source);
        } catch (FileNotFoundException | MessagingException ex) {
            Logger.getLogger(EmlMail.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EmlMail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getSubject() throws MessagingException {
        return message.getSubject();
    }

    public String getSender() throws MessagingException, UnsupportedEncodingException {
        return getAddresses(message.getFrom());
    }

    public Address[] getSenders() throws MessagingException {
        return message.getFrom();
    }

    public String getRecipient() throws MessagingException, UnsupportedEncodingException {
        return getAddresses(message.getAllRecipients());
    }

    public Address[] getRecipients() throws MessagingException {
        return message.getAllRecipients();
    }

    private String getTime(SimpleDateFormat sdf) {
        try {
            return sdf.format(message.getSentDate());
        } catch (MessagingException ex) {
            return "";
        }
    }

    public String getTimeDown() {
        return getTime(time_down);
    }

    public String getTimeUp() {
        return getTime(time_up);
    }

    public String getContent() {
        try {
            Object mailContent = message.getContent();
            if (message.isMimeType("text/html")) {
                return Jsoup.parse(mailContent.toString()).text();
            } else {
                return getTexMultipart((MimeMultipart) mailContent);
            }
        } catch (Exception ignored) {
            return "";
        }
    }

    private String getTexMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        for (int i = 0, count = mimeMultipart.getCount(); i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTexMultipart((MimeMultipart) bodyPart.getContent()));
            }
            //result += "\n";
        }
        return result.toString();
    }

    public int getCountAttachments() {
        try {
            if (message.isMimeType("multipart/mixed")) {
                Multipart multipart = (Multipart) message.getContent();
                return multipart.getCount();
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(EmlMail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List getAttachments() {
        List attachments = new ArrayList();
        try {
            if (!message.isMimeType("multipart/mixed")) {return attachments;}
            Multipart multipart = (Multipart) message.getContent();
            int count = multipart.getCount();
            if (count < 1) {return attachments;}

            for (int j = 0; j < count; j++) {
                MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(j);
                String disposition = bodyPart.getDisposition();
                DataHandler handler = bodyPart.getDataHandler();

                if (disposition != null && disposition.equalsIgnoreCase("ATTACHMENT")) {
                    attachments.add(decode(handler.getName()));
                }
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(EmlMail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return attachments;
    }

    public String getSnippet() {
        String content = this.getContent();
        int position = 0;
        int found = 0;
        while (position < 50 && position < content.length()) {
            found = content.indexOf(" ", position + 1);
            if (found != -1) {
                position = found;
            } else {
                break;
            }
        }
        return content.substring(0, position) + (found != -1 ? "..." : "");
    }
}
