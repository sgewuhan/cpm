package com.sg.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailTest {
	private String mailServer, From, To, mailSubject, MailContent;

	private String username, password;

	private Session mailSession;

	private Properties prop;

	private Message message;

	// Authenticator auth;//认证
	public MailTest() {
		// 设置邮件相关
		username = "zhonghua";
		password = "zhonghua";
		mailServer = "127.0.0.1";
		From = "zhonghua@zhonghua.cn";
		To = "1036405975@qq.com";
		mailSubject = "Hello Scientist";
		MailContent = "How are you today!";
	}

	public void send() {
		EmailAuthenticator mailauth = new EmailAuthenticator(username, password);
		// 设置邮件服务器
		prop = System.getProperties();
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.host", mailServer);
		// 产生新的Session服务
		mailSession = mailSession.getDefaultInstance(prop,
				(Authenticator) mailauth);
		message = new MimeMessage(mailSession);

		try {
			message.setFrom(new InternetAddress(From)); // 设置发件人
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(
					To));// 设置收件人
			message.setSubject(mailSubject);// 设置主题
			message.setContent(MailContent, "text/plain");// 设置内容
			message.setSentDate(new Date());// 设置日期
			Transport tran = mailSession.getTransport("smtp");
			tran.connect(mailServer, username, password);
			tran.send(message, message.getAllRecipients());
			tran.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MailTest mail;
		mail = new MailTest();
		System.out.println("sending......");
		mail.send();
		System.out.println("finished!");
	}

}

class EmailAuthenticator extends Authenticator {
	private String m_username = null;

	private String m_userpass = null;

	void setUsername(String username) {
		m_username = username;
	}

	void setUserpass(String userpass) {
		m_userpass = userpass;
	}

	public EmailAuthenticator(String username, String userpass) {
		super();
		setUsername(username);
		setUserpass(userpass);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(m_username, m_userpass);
	}
}
