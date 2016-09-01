/*
 * Copyright 2016 kunpapa. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */
package net.deadplayers.maillibrary;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Class who handle the email connections
 * @author kunpapa
 *
 */
public class MailSender {

	private Plugin instance;

	private String mailTo;
	private String senderName;
	private String host;
	private String port;
	private String emailAccount;
	private String emailPassword;

	private Properties props;
	Session session;

	/**
	 * Constructor with the plugin as input
	 * @param plugin
	 */
	public MailSender(Plugin plugin){
		instance = plugin;
	}

	/**
	 * Begin the connection to the email
	 */
	public void initConnection(){
		props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", String.valueOf(port));
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", String.valueOf(port));

		session = Session.getInstance(props, 
				new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(emailAccount, emailPassword);
			}
		});
	}
	
	/**
	 * Method to send an SYNC mail to default email
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMailSync(String subject, String body){
		sendMail(mailTo,subject, body, true);
	}

	/**
	 * Method to send an SYNC mail
	 * @param mailTo email to send
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMailSync(String mailTo, String subject, String body){
		sendMail(mailTo,subject, body, true);
	}
	
	/**
	 * Method to send an ASYNC mail to default email
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMail(String subject, String body){
		sendMail(mailTo,subject, body, false);
	}

	/**
	 * Method to send an ASYNC mail
	 * @param mailTo email to send
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMail(String mailTo, String subject, String body){
		sendMail(mailTo,subject, body, false);
	}

	/**
	 * Method to send a email, can be SYNC or ASYNC
	 * @param mailTo email to send
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 * @param sync true to send a sync mail
	 */
	public void sendMail(String mailTo, String subject, String body, boolean sync){
		try {
			final Message message = new MimeMessage(session);
			try {
				message.setFrom(new InternetAddress(emailAccount, senderName));
			} catch (Exception e){
				message.setFrom(new InternetAddress(emailAccount));
			}
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setContent(body, "text/html; charset=utf-8");

			if(sync){
				try {
					Transport.send(message);
				} catch (Exception e){
					e.printStackTrace();
				}
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable(){
					public void run() {
						try {
							Transport.send(message);
						} catch (MessagingException e) {
							e.printStackTrace();
						}
					}
				});
			}
			Bukkit.getLogger().info("Mail sent to: "+mailTo);
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * @param mailTo the mailTo to set
	 */
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	/**
	 * @param senderName the senderName to set
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @param emailAccount the emailAccount to set
	 */
	public void setEmailAccount(String emailAccount) {
		this.emailAccount = emailAccount;
	}
	/**
	 * @param emailPassword the emailPassword to set
	 */
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

}
