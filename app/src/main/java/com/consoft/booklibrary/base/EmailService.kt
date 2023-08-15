package com.consoft.booklibrary.base

import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailService(vararg val emails: String, val subject: String, val body: String) {

  companion object {
    const val username = "booklibrary2803@gmail.com"
    const val password = "vdjytigxnvmcqvbk"
  }

  fun sendmail() {
    val props = Properties()
    props["mail.smtp.auth"] = "true"
    props["mail.smtp.starttls.enable"] = "true"
    props["mail.smtp.host"] = "smtp.gmail.com"
    props["mail.smtp.port"] = "587"

    val session: Session = Session.getInstance(props,
      object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
          return javax.mail.PasswordAuthentication(username, password)
        }
      }
    )
    val message: Message = MimeMessage(session)
    message.setFrom(InternetAddress(username))
    message.setRecipients(
      Message.RecipientType.TO,
      InternetAddress.parse(emails.reduce { prev, curr -> "$prev,$curr" })
    )
    message.subject = subject
    message.setText(body)
    Transport.send(message)
  }
}