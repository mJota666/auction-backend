package com.auction.auction_backend.modules.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender mailSender;

  @Async
  public void sendEmail(String to, String subject, String body) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
      log.info("Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send email to {}", to, e);
    }
  }

  @Async
  public void sendOtpEmail(String to, String otp) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject("Mã xác thực tài khoản - AUTOBID Smart Auctions");

      String htmlContent = """
          <!DOCTYPE html>
          <html lang="vi">
          <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width,initial-scale=1" />
            <title>AUTOBID OTP</title>
          </head>
          <body style="margin:0;padding:0;background:#EEF2FF;">
            <div style="display:none;max-height:0;overflow:hidden;opacity:0;color:transparent;">
              Mã OTP AUTOBID của bạn là %s (hiệu lực 5 phút).
            </div>

            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="background:#EEF2FF;padding:28px 12px;">
              <tr>
                <td align="center">
                  <table role="presentation" cellpadding="0" cellspacing="0" width="600" style="max-width:600px;width:100%%;background:#FFFFFF;border-radius:18px;overflow:hidden;box-shadow:0 14px 40px rgba(17,24,39,.12);border:1px solid rgba(99,102,241,.12);">
                    <tr>
                      <td style="padding:24px 22px;background:linear-gradient(135deg,#6C63FF 0%%,#22C55E 120%%);">
                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%%">
                          <tr>
                            <td align="left" style="vertical-align:middle;">
                              <div style="display:inline-block;padding:10px 14px;border-radius:999px;background:rgba(255,255,255,.18);border:1px solid rgba(255,255,255,.35);">
                                <span style="font-family:Arial,Helvetica,sans-serif;font-weight:800;letter-spacing:.5px;color:#FFFFFF;font-size:14px;">
                                  AUTOBID • SMART AUCTIONS
                                </span>
                              </div>
                            </td>
                            <td align="right" style="vertical-align:middle;">
                              <span style="font-family:Arial,Helvetica,sans-serif;color:rgba(255,255,255,.92);font-size:12px;">
                                Bảo mật tài khoản
                              </span>
                            </td>
                          </tr>
                        </table>

                        <div style="margin-top:14px;font-family:Arial,Helvetica,sans-serif;color:#FFFFFF;">
                          <div style="font-size:22px;font-weight:800;line-height:1.25;">
                            Xác thực email của bạn
                          </div>
                          <div style="margin-top:6px;font-size:14px;opacity:.92;line-height:1.5;">
                            Dùng mã OTP bên dưới để hoàn tất đăng ký.
                          </div>
                        </div>
                      </td>
                    </tr>

                    <tr>
                      <td style="padding:26px 22px 10px 22px;">
                        <div style="font-family:Arial,Helvetica,sans-serif;color:#111827;">
                          <p style="margin:0 0 10px 0;font-size:14px;line-height:1.7;">Xin chào,</p>
                          <p style="margin:0 0 14px 0;font-size:14px;line-height:1.7;color:#374151;">
                            Cảm ơn bạn đã đăng ký tại <b style="color:#111827;">AUTOBID</b>. Vui lòng nhập mã OTP này để xác thực:
                          </p>

                          <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="margin:18px 0 12px 0;">
                            <tr>
                              <td align="center">
                                <div style="display:inline-block;padding:16px 26px;border-radius:14px;background:linear-gradient(180deg,#F5F3FF 0%%,#ECFEFF 100%%);border:1px solid rgba(99,102,241,.25);box-shadow:0 10px 22px rgba(99,102,241,.14);">
                                  <div style="font-family:Arial,Helvetica,sans-serif;font-size:11px;letter-spacing:2px;color:#6B7280;font-weight:700;text-transform:uppercase;">
                                    Your OTP
                                  </div>
                                  <div style="margin-top:6px;font-family:Arial,Helvetica,sans-serif;font-size:34px;letter-spacing:8px;font-weight:900;color:#6C63FF;">
                                    %s
                                  </div>
                                </div>
                              </td>
                            </tr>
                          </table>

                          <p style="margin:14px 0 0 0;font-size:13px;line-height:1.7;color:#374151;">
                            Mã có hiệu lực trong <b>5 phút</b>. Vui lòng không chia sẻ mã này cho bất kỳ ai.
                          </p>

                          <div style="margin-top:14px;padding:12px 14px;border-radius:12px;background:#F9FAFB;border:1px solid #E5E7EB;">
                            <div style="font-family:Arial,Helvetica,sans-serif;font-size:12px;line-height:1.6;color:#6B7280;">
                              Nếu bạn không yêu cầu mã này, hãy bỏ qua email này.
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>

                    <tr>
                      <td style="padding:0 22px;">
                        <div style="height:1px;background:linear-gradient(90deg,rgba(99,102,241,0) 0%%,rgba(99,102,241,.25) 50%%,rgba(99,102,241,0) 100%%);"></div>
                      </td>
                    </tr>

                    <tr>
                      <td style="padding:16px 22px 22px 22px;">
                        <div style="font-family:Arial,Helvetica,sans-serif;font-size:12px;line-height:1.6;color:#6B7280;text-align:center;">
                          <div style="font-weight:700;color:#4B5563;">© 2026 AUTOBID Smart Auctions</div>
                          <div style="margin-top:4px;">Đây là email tự động, vui lòng không trả lời.</div>
                        </div>
                      </td>
                    </tr>

                  </table>
                </td>
              </tr>
            </table>
          </body>
          </html>
          """
          .formatted(otp, otp);

      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("HTML OTP Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send HTML email to {}", to, e);
    }
  }
}
