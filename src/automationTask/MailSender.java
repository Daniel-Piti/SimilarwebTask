package automationTask;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

public class MailSender {
    private final Session session;
    private final String from;
    private final String receiver;

    public MailSender(final String from, final String password, String receiver){
        this.from = from;
        this.receiver = receiver;
        Properties prop = new Properties();

        //Set properties for session
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        //Create session
        this.session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
    }

    public void sendMail(ArrayList<String> allMessages) {
        allMessages.set(0, "*** Please Note: New version releases was detected:\n" + allMessages.get(0));
        String mailMessage = String.join("\n", allMessages);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject("Chrome version notifier");
            message.setText(mailMessage);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}