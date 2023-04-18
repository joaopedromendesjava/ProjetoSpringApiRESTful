package com.projetoSprirngApiRestfull.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {
	
	private String userName = "joaopedroprojectsjava@gmail.com";
	private String senha = "wjoivlejuilyjmyk";

	public void enviarEmail(String assunto, String emailDestino, String mensagem) throws MessagingException {
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.ssl", "*");
		properties.put("mail.smtp.auth", "true"); //Autorização
		properties.put("mail.smtp.starttls", "true"); //Autenticação
		properties.put("mail.smtp.host", "smtp.gmail.com"); //Servidor google
		properties.put("mail.smtp.port", "465"); // porta do servidor
		properties.put("mail.smtp.socketFactory.port", "465"); //Especifica a porta socket
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Classe de conexão socket
		
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
		
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(userName, senha); // email que enviará novo acesso para o usuario
			}
		});
		
		Address[] toUser = InternetAddress.parse(emailDestino);
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName));	// Quem esta enviando
		message.setRecipients(Message.RecipientType.TO, toUser); // para quem vai o email
		message.setSubject(assunto); //Assunto do email
		message.setText(mensagem); // Mensagem do email
		
		Transport.send(message);
		
		
	}
}
