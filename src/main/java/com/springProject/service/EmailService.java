package com.springProject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    //아이디 찾기 결과 이메일 발송
    public void sendUserLoginIdEmail(String toEmail, String loginId) {
        String subject = "아이디 찾기 결과";
        String content = "아이디 찾기 결과 = " + loginId + " 입니다.";
        sendEmail(toEmail, subject, content);
    }

    //임시 비밀번호 발급 이메일 발송
    public void sendTempPasswordEmail(String toEmail, String tempPassword) {
        String subject = "임시 비밀번호 발급";
        String content = "임시 비밀번호는 " + tempPassword + " 입니다.";
        sendEmail(toEmail, subject, content);
    }

    //공통 메서드(이메일)
    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // 보내는 사람 이메일 아이디
        message.setTo(to); // 받는 사람 이메일 아이디
        message.setSubject(subject); // 이메일 제목
        message.setText(content); // 이메일 내용
        javaMailSender.send(message); // 이메일 전송
    }
}
