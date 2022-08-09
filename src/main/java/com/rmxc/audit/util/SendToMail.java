package com.rmxc.audit.util;

import org.apache.tools.ant.taskdefs.SendEmail;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendToMail {

    //配置文件读取
    private static String PROPERTIES_NAME = "email.properties";
    // 发件人 账号和密码
    private static String MY_EMAIL_ACCOUNT = "email.user";
    // 密码,是你自己的设置的授权码
    private static String MY_EMAIL_PASSWORD = "email.password";
    // SMTP服务器(这里用的163 SMTP服务器)
    private static String MEAIL_163_SMTP_HOST = "email.smptHost";
    // 端口号,这个是163使用到的;QQ的应该是465或者875
    private static String SMTP_163_PORT = "email.smptPost";
    // 收件人
    private static String SEND_TO = "email.sendTo";


    public static String SendEmail(String subject,String text) throws AddressException, MessagingException {
        Properties p = new Properties();
        Properties properties = ToolUtils.readPropertiesFile(PROPERTIES_NAME);
        p.setProperty("mail.smtp.host", properties.getProperty(MEAIL_163_SMTP_HOST));
        p.setProperty("mail.smtp.port", properties.getProperty(SMTP_163_PORT));
        p.setProperty("mail.smtp.socketFactory.port", properties.getProperty(SMTP_163_PORT));
        p.setProperty("mail.smtp.auth", "true");
//        p.setProperty("mail.smtp.socketFactory.class", "SSL_FACTORY");
        p.setProperty("mail.smtp.ssl.enable", "true");
        Session session = Session.getInstance(p, new Authenticator() {
            // 设置认证账户信息
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty(MY_EMAIL_ACCOUNT), properties.getProperty(MY_EMAIL_PASSWORD));
            }
        });
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        // 发件人
        message.setFrom(new InternetAddress(properties.getProperty(MY_EMAIL_ACCOUNT)));
        // 收件人
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(properties.getProperty(SEND_TO)));
        message.setSubject(subject);

        message.setContent(text, "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        Transport.send(message);
        return null;
    }



}