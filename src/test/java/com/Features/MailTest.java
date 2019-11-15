package com.Features;

import com.alibaba.fastjson.JSONObject;
import com.utils.EmailUtil;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @author ：suncj
 * @date ：2019/9/17 14:52
 */
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class MailTest {
    @Test
    public void test(){
        try {
//            EmailUtil.sendMail("1678609093@qq.com","测试1主题","内容,测试邮件工具:http://175.6.223.37:8108/iptv-hndx/","1833896677@qq.com","hyfxmskvlubbbija");
            List<File> fileList= new ArrayList<>(Arrays.asList(new File("D:\\data\\excel\\exportSearch.csv"),new File("D:\\data\\excel\\data.zip")));
//           sendMail("1678609093@qq.com,sunchangjunn@outlook.com","主题","内容",fileList);
            EmailUtil.sendMailFiles("1678609093@qq.com","测试1主题","内容,测试邮件工具:http://175.6.223.37:8108/iptv-hndx/",fileList,"1833896677@qq.com","hyfxmskvlubbbija");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMail(String email, String subject, String emailMsg, List<File> files)
            throws AddressException, MessagingException {
        // 1.创建一个程序与邮件服务器会话对象 Session

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "SMTP");

    //如果是其他邮箱就把qq改为相应的邮箱后缀
        props.setProperty("mail.host", "smtp.qq.com");

    // 指定验证为true
        props.setProperty("mail.smtp.auth", "true");

        // 创建验证器
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {

    //发邮件的账号和密码，此处需要到QQ邮箱官网开启SMTP,P0P3,IMAP服务，然后获取授权码作为第三方登录密码
                return new PasswordAuthentication("1833896677@qq.com", "hyfxmskvlubbbija");
            }
        };

        //不同与request的session
        Session session = Session.getInstance(props, auth);

        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress("1833896677@qq.com")); // 设置发送者
        String[] emails=email.split(",");
        InternetAddress[] ias=new InternetAddress[emails.length];
        for (int i = 0; i <emails.length ; i++) {
            ias[i]=new InternetAddress(emails[i]) ;
        }
         message.setRecipients(Message.RecipientType.TO,ias);

        //message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com")});
        //message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email)); // 设置发送方式与接收者

        message.setSubject(subject);
        // message.setText("这是一封激活邮件，请<a href='#'>点击</a>");

        message.setContent(emailMsg, "text/html;charset=utf-8");
        //创建多重消息
//        final Multipart multipart = new MimeMultipart();
        MimeMultipart mm = new MimeMultipart();
        for (final File f : files) {
            MimeBodyPart   attachment = new MimeBodyPart();
            final String filePath =f.getPath();
            //根据附件文件创建文件数据源
            final DataSource ds = new FileDataSource(filePath);
            attachment.setDataHandler(new DataHandler(ds));
            //为附件设置文件名
            attachment.setFileName(ds.getName());
            mm.addBodyPart(attachment);
        }
        // 发送完整消息
        message.setContent(mm);

        // 3.创建 Transport用于将邮件发送

        Transport.send(message);
    }
}
