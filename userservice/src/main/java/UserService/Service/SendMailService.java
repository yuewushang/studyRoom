package UserService.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface SendMailService {
    //发送注册时验证码邮件
    public String SendMail(String qq) throws MessagingException, UnsupportedEncodingException;
    //发送系统提醒邮件
    public void SendRemindMail(String qq,String name) throws MessagingException, UnsupportedEncodingException;
    //发送提醒签到邮件
    public void SendRemindQianDaoMail(String qq,String name) throws MessagingException, UnsupportedEncodingException;
}
