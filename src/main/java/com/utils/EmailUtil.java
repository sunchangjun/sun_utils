package com.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author ：suncj
 * @date ：2019/9/17 18:23
 */
public class EmailUtil {



    public static void sendMail(String outEmail, String subject, String emailMsg,String my_email,String password_code) throws AddressException, MessagingException {
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "SMTP");
        //如果是其他邮箱就把qq改为相应的邮箱后缀
        props.setProperty("mail.host", "smtp.qq.com");
        // 指定验证为true
        props.setProperty("mail.smtp.auth", "true");
        // 创建验证器
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //发邮件的账号和密码，此处需要到QQ邮箱官网开启SMTP,P0P3,IMAP服务，然后获取授权码作为第三方登录密码
                return new PasswordAuthentication(my_email, password_code);
            }
        };
        //不同与request的session
        Session session = Session.getInstance(props, auth);
        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);
        // 设置发送者
        message.setFrom(new InternetAddress(my_email));
        //message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email)); // 设置发送方式与接收者
        //设置收件邮箱,多个收件人用逗号分隔
        String[] emails=outEmail.split(",");
        InternetAddress[] internetAddress=new InternetAddress[emails.length];
        for (int i = 0; i <emails.length ; i++) {
            internetAddress[i]=new InternetAddress(emails[i]) ;
        }
        message.setRecipients(Message.RecipientType.TO,internetAddress);
        message.setSubject(subject);
        message.setContent(emailMsg, "text/html;charset=utf-8");
        // 3.创建 Transport用于将邮件发送
        Transport.send(message);
    }


    public static void sendMailFiles(String outEmail, String subject, String emailMsg, List<File> files,String my_email, String password_code) throws AddressException, MessagingException {
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "SMTP");
        //如果是其他邮箱就把qq改为相应的邮箱后缀
        props.setProperty("mail.host", "smtp.qq.com");
        // 指定验证为true
        props.setProperty("mail.smtp.auth", "true");
        // 创建验证器
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                //发邮件的账号和密码，此处需要到QQ邮箱官网开启SMTP,P0P3,IMAP服务，然后获取授权码作为第三方登录密码
                return new PasswordAuthentication(my_email, password_code);
            }
        };
        //不同与request的session
        Session session = Session.getInstance(props, auth);
        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);
        // 设置发送者
        message.setFrom(new InternetAddress(my_email));
        //message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email)); // 设置发送方式与接收者
        //设置收件邮箱,多个收件人用逗号分隔
        String[] emails=outEmail.split(",");
        InternetAddress[] internetAddress=new InternetAddress[emails.length];
        for (int i = 0; i <emails.length ; i++) {
            internetAddress[i]=new InternetAddress(emails[i]) ;
        }
        message.setRecipients(Message.RecipientType.TO,internetAddress);
        message.setSubject(subject);
        //创建多重消息
        MimeMultipart mm = new MimeMultipart();
        //创建文件消息
        for (final File f : files) {
            MimeBodyPart attachment = new MimeBodyPart();
            final String filePath =f.getPath();
            //根据附件文件创建文件数据源
            final DataSource ds = new FileDataSource(filePath);
            attachment.setDataHandler(new DataHandler(ds));
            //为附件设置文件名
            attachment.setFileName(ds.getName());
            mm.addBodyPart(attachment);
        }
        // 创建文本消息部分
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(emailMsg);
        mm.addBodyPart(messageBodyPart);

        // 发送完整消息
        message.setContent(mm);


        // 3.创建 Transport用于将邮件发送
        Transport.send(message);
    }
}
