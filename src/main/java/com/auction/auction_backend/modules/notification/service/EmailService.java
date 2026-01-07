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

  @Async
  public void sendQuestionNotification(String to, String productName, String question, String productLink) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject("Câu hỏi mới về sản phẩm: " + productName);

      String htmlContent = """
          <!DOCTYPE html>
          <html>
          <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
              <div style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                  <h2 style="color: #333;">Bạn có câu hỏi mới!</h2>
                  <p>Xin chào,</p>
                  <p>Một khách hàng đã đặt câu hỏi về sản phẩm <b>%s</b> của bạn:</p>
                  <blockquote style="background: #f9f9f9; border-left: 4px solid #007bff; margin: 10px 0; padding: 10px;">
                      "%s"
                  </blockquote>
                  <p>Vui lòng trả lời sớm để tăng khả năng chốt đơn nhé!</p>
                  <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Xem và Trả lời ngay</a>
                  <p style="margin-top: 20px; font-size: 12px; color: #888;">Nếu nút không hoạt động, hãy copy link sau: %s</p>
              </div>
          </body>
          </html>
          """
          .formatted(productName, question, productLink, productLink);

      helper.setText(htmlContent, true);
      mailSender.send(message);
      log.info("Question Notification Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send Question Notification email to {}", to, e);
    }
  }

  @Async
  public void sendAnswerNotification(String to, String productName, String question, String answer,
      String productLink) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject("Người bán đã trả lời câu hỏi của bạn về: " + productName);

      String htmlContent = """
          <!DOCTYPE html>
          <html>
          <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
              <div style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                  <h2 style="color: #333;">Câu hỏi của bạn đã được trả lời!</h2>
                  <p>Xin chào,</p>
                  <p>Người bán đã phản hồi câu hỏi của bạn về sản phẩm <b>%s</b>:</p>

                  <div style="margin-bottom: 15px;">
                      <b>Câu hỏi của bạn:</b>
                      <div style="color: #555; font-style: italic;">"%s"</div>
                  </div>

                  <div style="margin-bottom: 15px;">
                      <b>Trả lời:</b>
                      <blockquote style="background: #e8f5e9; border-left: 4px solid #4caf50; margin: 5px 0; padding: 10px;">
                          "%s"
                      </blockquote>
                  </div>

                  <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #4caf50; color: white; text-decoration: none; border-radius: 5px;">Xem chi tiết sản phẩm</a>
              </div>
          </body>
          </html>
          """
          .formatted(productName, question, answer, productLink);

      helper.setText(htmlContent, true);
      mailSender.send(message);
      log.info("Answer Notification Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send Answer Notification email to {}", to, e);
    }
  }

  @Async
  public void sendBidPlacedNotification(String to, String productName, java.math.BigDecimal bidAmount,
      String productLink, boolean isSeller) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      String subject = isSeller ? "Sản phẩm của bạn có lượt ra giá mới: " + productName
          : "Ra giá thành công: " + productName;
      helper.setSubject(subject);

      String content = isSeller
          ? "<p>Sản phẩm <b>%s</b> vừa nhận được mức giá mới: <b>%s VNĐ</b>.</p>".formatted(productName,
              bidAmount)
          : "<p>Bạn đã ra giá <b>%s VNĐ</b> thành công cho sản phẩm <b>%s</b>.</p>".formatted(bidAmount,
              productName);

      String htmlContent = """
          <!DOCTYPE html>
          <html>
          <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
              <div style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px;">
                  <h2 style="color: #2c3e50;">%s</h2>
                  %s
                  <p>Xem chi tiết tại: <a href="%s">%s</a></p>
              </div>
          </body>
          </html>
          """
          .formatted(subject, content, productLink, productName);

      helper.setText(htmlContent, true);
      mailSender.send(message);
      log.info("Bid Notification Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send Bid Notification email to {}", to, e);
    }
  }

  @Async
  public void sendOutbidNotification(String to, String productName, java.math.BigDecimal newPrice, String productLink) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject("Bạn đã bị vượt giá: " + productName);

      String htmlContent = """
          <!DOCTYPE html>
          <html>
          <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
              <div style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px;">
                  <h2 style="color: #d32f2f;">Bạn đã bị vượt giá!</h2>
                  <p>Có người vừa ra giá cao hơn cho sản phẩm <b>%s</b>.</p>
                  <p>Giá hiện tại: <b>%s VNĐ</b></p>
                  <p>Hãy ra giá lại ngay để giành chiến thắng!</p>
                  <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #d32f2f; color: white; text-decoration: none; border-radius: 5px;">Đấu giá ngay</a>
              </div>
          </body>
          </html>
          """
          .formatted(productName, newPrice, productLink);

      helper.setText(htmlContent, true);
      mailSender.send(message);
      log.info("Outbid Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send Outbid email to {}", to, e);
    }
  }

  @Async
  public void sendAuctionEndedNotification(String to, String productName, java.math.BigDecimal finalPrice,
      boolean isWinner, String productLink) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      String subject = isWinner ? "Chúc mừng! Bạn đã thắng đấu giá: " + productName
          : "Phiên đấu giá kết thúc: " + productName;
      helper.setSubject(subject);

      String content = isWinner
          ? "<p>Chúc mừng bạn đã thắng sản phẩm <b>%s</b> với giá <b>%s VNĐ</b>.</p><p>Vui lòng thanh toán để hoàn tất đơn hàng.</p>"
              .formatted(productName, finalPrice)
          : "<p>Phiên đấu giá sản phẩm <b>%s</b> của bạn đã kết thúc.</p><p>Giá cuối cùng: <b>%s VNĐ</b>.</p>"
              .formatted(productName, finalPrice);

      String htmlContent = """
          <!DOCTYPE html>
          <html>
          <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
              <div style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px;">
                  <h2 style="color: #2e7d32;">%s</h2>
                  %s
                  <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #2e7d32; color: white; text-decoration: none; border-radius: 5px;">Xem chi tiết</a>
              </div>
          </body>
          </html>
          """
          .formatted(subject, content, productLink);

      helper.setText(htmlContent, true);
      mailSender.send(message);
      log.info("Auction End Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send Auction End email to {}", to, e);
    }
  }

  @Async
  public void sendBidderBlockedNotification(String to, String productName, String sellerName, String productLink) {
    try {
      jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
      org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
          message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject("Thông báo từ chối ra giá: " + productName);

      String htmlContent = """
          <!DOCTYPE html>
          <html>
          <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
              <div style="max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px;">
                  <h2 style="color: #d32f2f;">Bạn đã bị từ chối ra giá</h2>
                  <p>Xin chào,</p>
                  <p>Người bán <b>%s</b> đã chặn bạn tham gia đấu giá sản phẩm <b>%s</b>.</p>
                  <p>Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ người bán.</p>
                  <p>Các lượt ra giá tự động của bạn cho sản phẩm này (nếu có) đã bị hủy.</p>
                  <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #555; color: white; text-decoration: none; border-radius: 5px;">Xem sản phẩm</a>
              </div>
          </body>
          </html>
          """
          .formatted(sellerName, productName, productLink);

      helper.setText(htmlContent, true);
      mailSender.send(message);
      log.info("Blocked Bidder Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send Blocked Bidder email to {}", to, e);
    }
  }
}
