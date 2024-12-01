package com.lartimes;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/1 22:16
 */
@SpringBootApplication
public class TiktokApplication  implements ApplicationRunner {
    @Autowired
    private JavaMailSender javaMailSender;
    public static void main(String[] args)  {
        SpringApplication.run(TiktokApplication.class, args);
    }



    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();

        // 创建 MimeMessageHelper，指定 boolean multipart 参数为 true
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 发件人邮箱和名称
        helper.setFrom("3376997088@qq.com", "springdoc");
        // 收件人邮箱
        helper.setTo("lartimes2004@163.com");
        // 邮件标题
        helper.setSubject("Spring 中文文档");
        // 邮件正文，第二个参数表示是否是HTML正文
        helper.setText("你好，这是 Spirng 的中文文档！<br/> 请尽快下载！", true);

//        // 添加一个附件，指定附件名称、文件的 Inputstream 流 以及 Content-Type
//        helper.addAttachment("spring-framework 中文文档.pdf",
//                () -> Files.newInputStream(Paths.get("C:\\Users\\KevinBlandy\\Desktop\\spring-framework 中文文档.pdf")),
//                "application/octet-stream");

        // 发送
        javaMailSender.send(message);
    }
}
