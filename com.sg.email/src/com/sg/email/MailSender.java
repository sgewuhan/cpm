package com.sg.email;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.util.BASE64EncoderStream;

public class MailSender {

	private Properties props;
	private Session session;
	private MimeMultipart mimeMultiPart;
	private MimeMessage mimeMessage;
	private String html = "";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	public MailSender() {
		props = new Properties();
		props.setProperty("mail.smtp.auth", Activator.getSmtpAuth());
		props.setProperty("mail.smtp.host", Activator.getSmtpHost());
		props.setProperty("mail.smtp.port", "" + Activator.getPort());
		
		if(Activator.useSSL()){
			props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.socketFactory.port", Activator.getPort());
		}
		
		// ����ʼ��Ự����
		session = Session.getDefaultInstance(props,
				new SmtpAuthenticator(Activator.getSenderAccount(),
						Activator.getSenderPassword()));


		mimeMultiPart = new MimeMultipart("related");// related��ζ�ſ��Է���html��ʽ���ʼ�

		// ����MIME�ʼ�����
		mimeMessage = new MimeMessage(session);
	}

	public void setFrom(String from) throws AddressException,
			MessagingException {
		mimeMessage.setFrom(new InternetAddress(from));
	}

	public void setTo(String to) throws AddressException, MessagingException {
		mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(
				to));
	}

	public void setSubject(String subject) throws MessagingException {
		mimeMessage.setSubject(subject);
	}

	public void addAttachFile(String fileNameWithPath)
			throws MessagingException {
		BodyPart attachBodyPart = new MimeBodyPart();// ��ͨ����
		FileDataSource fds = new FileDataSource(fileNameWithPath);
		attachBodyPart.setDataHandler(new DataHandler(fds));
		attachBodyPart.setFileName("=?GBK?B?"
				+ BASE64EncoderStream.encode(fds.getName().getBytes()) + "?=");// �����������������
		mimeMultiPart.addBodyPart(attachBodyPart);
	}

	public void addMailBody(String htmlString) throws MessagingException {
		BodyPart bodyPart = new MimeBodyPart();// ����
		bodyPart.setDataHandler(new DataHandler(htmlString,
				"text/html;charset=GBK"));// ��ҳ��ʽ
		mimeMultiPart.addBodyPart(bodyPart);
	}

	public void addBodyImageAttachment(String fileNameWithPath,
			String fileName, String cidTagName) throws MessagingException {
		MimeBodyPart imgBodyPart = new MimeBodyPart(); // ����ͼ��
		byte[] bytes = readFile(fileNameWithPath);
		ByteArrayDataSource fileds = new ByteArrayDataSource(bytes,
				"application/octet-stream");
		imgBodyPart.setDataHandler(new DataHandler(fileds));
		imgBodyPart.setFileName(fileName);
//		imgBodyPart.setHeader("Content-ID", "<IMG1></IMG1>");// ��html��ʹ�ø�ͼƬ����src="cid:IMG1"
		mimeMultiPart.addBodyPart(imgBodyPart);
	}

	public void send() throws Exception {

		mimeMessage.setContent(mimeMultiPart);// �����ʼ����ݶ���
		mimeMessage.setSentDate(new Date());// ��������
		mimeMessage.setContent(html,"text/html;charset = gbk");// �����ʼ����ݶ���

		Transport.send(mimeMessage);// �����ʼ�

	}
	
//	public void send2() throws Exception {
//
//		mimeMessage.setSentDate(new Date());// ��������
//
//		Transport.send(mimeMessage);// �����ʼ�
//
//	}

	/**
	 * ��ȡ�ļ�
	 * 
	 * @param file
	 *            �ļ�·��
	 * @return ���ض���������
	 */
	public static byte[] readFile(String file) {
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			fis = new FileInputStream(file);
			bos = new ByteArrayOutputStream();
			int bytesRead;
			byte buffer[] = new byte[1024 * 1024];
			while ((bytesRead = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, bytesRead);
				Arrays.fill(buffer, (byte) 0);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
	
	
}

/**
 * Smtp��֤
 */
class SmtpAuthenticator extends Authenticator {
	String username = null;
	String password = null;

	// SMTP�����֤
	public SmtpAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.username, this.password);
	}

}

class ByteArrayDataSource implements DataSource {

	private final String contentType;
	private final byte[] buf;
	private final int len;

	public ByteArrayDataSource(byte[] buf, String contentType) {
		this(buf, buf.length, contentType);
	}

	public ByteArrayDataSource(byte[] buf, int length, String contentType) {
		this.buf = buf;
		this.len = length;
		this.contentType = contentType;
	}

	public String getContentType() {
		if (contentType == null)
			return "application/octet-stream";
		return contentType;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(buf, 0, len);
	}

	public String getName() {
		return null;
	}

	public OutputStream getOutputStream() {
		throw new UnsupportedOperationException();
	}
}