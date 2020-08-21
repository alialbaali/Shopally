package com.shopping

import com.auth0.jwt.interfaces.Payload
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.locations.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@KtorExperimentalAPI
fun String.hash(): String {
    val hashKey = hex(System.getenv("SECRET") ?: "4894")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(this.toByteArray(Charsets.UTF_8)))
}

fun Date.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant());

inline val PipelineContext<*, ApplicationCall>.jwtPayload: Payload
    get() = call.principal<JWTPrincipal>()?.payload ?: throw BadRequestException(Errors.INVALID_REQUEST)

//fun User.sendEmail() {
//    try {
//
//        val username = System.getenv("EMAIL")
//        val password = System.getenv("PASSWORD")
//        val recipient = this.userEmail
//
//
//        val host = "smtp.gmail.com"
//        val properties = System.getProperties().also { props ->
//            props["mail.smtp.starttls.enable"] = "true"
//            props["mail.smtp.host"] = host
//            props["mail.smtp.user"] = username
//            props["mail.smtp.password"] = password
//            props["mail.smtp.port"] = "587"
//            props["mail.smtp.auth"] = "true"
//        }
//
//        val session = Session.getInstance(properties)
//
//        val message = MimeMessage(session)
//
//
//        message.setFrom("noreply@noto.com")
//        message.setRecipient(Message.RecipientType.TO, InternetAddress(recipient))
//
//        message.subject = "Verification"
//        message.setText("HELLO")
//        val transport = session.getTransport("smtp")
//        transport.connect(host, username, password)
//        transport.sendMessage(message, message.allRecipients)
//        transport.close()
//
//    } catch (e: Throwable) {
//        println(e.toString())
//    }
//
//}