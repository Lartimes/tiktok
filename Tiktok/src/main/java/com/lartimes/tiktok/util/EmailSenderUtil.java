package com.lartimes.tiktok.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 11:34
 */
@Component
public class EmailSenderUtil {
    private static final String HTML_CONTENT = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
               <title>邮箱验证码</title>
            </head>
            <body>
                <p>亲爱的用户，您好！</p>
                <p>感谢您注册我们的服务。为了验证您的邮箱地址，我们需要您输入以下验证码：</p>
                <p style="font-size: 24px; font-weight: bold;">%s</p>
                <p>请在 %d分钟 内输入此验证码，过期将失效。</p>
                <p>如果您没有请求此验证码，请忽略此邮件。</p>
                <p>祝您使用愉快！</p>
            </body>
            </html>
            """;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.code-validation}")
    public Integer minutes;
    @Value("${spring.mail.username:mingdongiloveu2@qq.com}")
    private String fromEmail;

    public EmailSenderUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sentTo(String email, String sixCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail, "Tiktok");
        helper.setTo(email);
        helper.setSubject("Tiktok邮箱验证码");
        String format = String.format(HTML_CONTENT, sixCode, minutes);
        helper.setText(format, true);
        javaMailSender.send(message);
    }

}
