package com.husc.lms.service;


import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.ConfirmationCode;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ConfirmationCodeRepository;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
	private AccountRepository accountRepository;
	
    @Autowired
	private PasswordEncoder passwordEncoder;
	
    
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @Transactional
    public void sendConfirmationEmail(String toEmail) throws UnsupportedEncodingException {
        try {
        	
        	if (!isHuscEmail(toEmail)) {
        	    throw new AppException(ErrorCode.INVALID_EMAIL_DOMAIN);
        	}

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đăng ký tài khoản - HUSC LMS");
            helper.setFrom(new InternetAddress("husclms@gmail.com", "HUSC LMS"));

            if(accountRepository.existsByUsername(toEmail)) {
    			throw new AppException(ErrorCode.EMAIL_ALREADY_USED);
    		} 

            if (confirmationCodeRepository.existsByEmail(toEmail)) {
				confirmationCodeRepository.deleteByEmail(toEmail);
			}
            
            String confirmationCode = generateRandomCode();
            ConfirmationCode code = ConfirmationCode.builder()
            		.code(confirmationCode)
            		.email(toEmail)
            		.createdAt(LocalDateTime.now())
            		.build();
            confirmationCodeRepository.save(code);	
            
            String htmlContent = buildHtmlContent(confirmationCode);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // hoặc log lỗi nếu dùng logger
        }
    }
  
    public void sendPasswordEmail(String toEmail) throws UnsupportedEncodingException {
    	try {
    		
    		if (!isHuscEmail(toEmail)) {
    			throw new AppException(ErrorCode.INVALID_EMAIL_DOMAIN);
    		}
    		
    		if(!accountRepository.existsByUsername(toEmail)) {
    			throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
    		}
    		
    		MimeMessage message = mailSender.createMimeMessage();
    		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    		helper.setTo(toEmail);
    		helper.setSubject("Xác nhận đặt lại mật khẩu cho tài khoản - HUSC LMS");
    		helper.setFrom(new InternetAddress("husclms@gmail.com", "HUSC LMS"));
    		String newPassword = generateRandomPassword();
    		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(toEmail).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
    		account.setPassword(passwordEncoder.encode(newPassword));
    		accountRepository.save(account);
    		String htmlContent = buildHtmlContentPassword(newPassword);
    		helper.setText(htmlContent, true); // true = HTML
    		mailSender.send(message);
    	} catch (MessagingException e) {
    		e.printStackTrace(); 
    	}
    }

    
    
    
    public String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    public String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allChars = upper + lower + digits + special;
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        // Đảm bảo có đủ 4 loại ký tự
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Thêm các ký tự ngẫu nhiên còn lại
        for (int i = 4; i < 10; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Đảo ngẫu nhiên chuỗi để tránh thứ tự predictable
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars);

        StringBuilder finalPassword = new StringBuilder();
        for (char c : passwordChars) {
            finalPassword.append(c);
        }

        return finalPassword.toString();
    }

    
    
    public boolean isHuscEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@husc\\.edu\\.vn$";
        return email != null && email.matches(regex);
    }


    
    public boolean verifyCode(String email, String code) {
    	ConfirmationCode confirmationCode = confirmationCodeRepository.findByEmail(email);
    	boolean isVerify = false;
    	if (confirmationCode == null) {
    		return false; // Không tìm thấy mã xác nhận cho email này
    	}
    	
    	if (confirmationCode.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(10))) {
    		throw new AppException(ErrorCode.TIME_LIMITED);
    	}
    	if(confirmationCode.getCode().equals(code)) {
    		isVerify = true;
    		confirmationCode.setVerify(isVerify);
    		confirmationCodeRepository.save(confirmationCode);
    	}
    	
    	
    	return isVerify;
    }
    
    private String buildHtmlContent(String confirmationCode) {
        return """
            <html>
            <head>
                <style>
                    .container {
                        max-width: 600px;
                        margin: auto;
                        font-family: Arial, sans-serif;
                        background-color: #f9f9f9;
                        padding: 20px;
                        border: 1px solid #ddd;
                        border-radius: 8px;
                    }
                    .title {
                        font-size: 20px;
                        font-weight: bold;
                        margin-bottom: 20px;
                    }
                    .code {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #007bff;
                        color: white;
                        font-size: 18px;
                        font-weight: bold;
                        border-radius: 5px;
                        letter-spacing: 2px;
                        margin-top: 10px;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #777;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="title">Mã xác nhận của bạn</div>
                    <p>Xin chào,</p>
                    <p>Đây là mã xác nhận mà bạn đã yêu cầu:</p>
                    <div class="code">%s</div>
                    <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>
                    <div class="footer">
                        © 2025 HUSC LMS - 4T75 Team .
                    </div>
                </div>
            </body>
            </html>
            """.formatted(confirmationCode);
    }
    private String buildHtmlContentPassword(String newPassword) {
    	return """
            <html>
            <head>
                <style>
                    .container {
                        max-width: 600px;
                        margin: auto;
                        font-family: Arial, sans-serif;
                        background-color: #f9f9f9;
                        padding: 20px;
                        border: 1px solid #ddd;
                        border-radius: 8px;
                    }
                    .title {
                        font-size: 20px;
                        font-weight: bold;
                        margin-bottom: 20px;
                    }
                    .code {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #007bff;
                        color: white;
                        font-size: 18px;
                        font-weight: bold;
                        border-radius: 5px;
                        letter-spacing: 2px;
                        margin-top: 10px;
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #777;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="title">Mật khẩu mới của bạn</div>
                    <p>Xin chào,</p>
                    <p>Đây là mật khẩu mới cho tài khoản của bạn</p>
                    <div class="code">%s</div>
                    <p>Vui lòng thực hiện đổi mật khẩu sau khi đăng nhập</p>
                    <p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
                    <div class="footer">
                        © 2025 HUSC LMS - 4T75 Team .
                    </div>
                </div>
            </body>
            </html>
            """.formatted(newPassword);
    }


}