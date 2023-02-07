package UserService.Service.ServiceImpl;

import UserService.Service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private JavaMailSender javaMailSender;


    /**
     * 发送邮件函数
     * @param qq
     */
    @Override
    public String SendMail(String qq) throws MessagingException, UnsupportedEncodingException {
        //创建邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //创建邮件修饰器，并与邮件绑定
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        //设置发件人QQ和名称
        helper.setFrom("2669287863@qq.com","寒窗自习室官方");
        //设置收件人地址
        helper.setTo(qq);
        String code="";
        Random random=new Random();
        for(int i=0;i<4;i++){
            code+=random.nextInt(10);
        }
        //发送普通文本邮件
        helper.setText("欢迎使用我们的寒窗自习室，您的验证码为： "+code+"  ,有效时间为5分钟");
        //设置邮件主题
        helper.setSubject("寒窗自习室官方信息");
        //发送邮件
        javaMailSender.send(mimeMessage);
        return code;
    }

    /**
     * 发送系统提醒邮件
     * @param qq
     */
    @Override
    public void SendRemindMail(String qq,String name) throws MessagingException, UnsupportedEncodingException {
        //创建一个邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //创建邮件修饰器
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        //设置发送人邮箱，和名称
        helper.setFrom("2669287863@qq.com","寒窗自习室");
        //设置收件人地址
        helper.setTo(qq);
        //设置邮件主题
        helper.setSubject("每日提醒服务");
        //设置邮件内容
        helper.setText("亲爱的： "+name+" ，早上好呀~，今天也要继续努力哦。 -----寒窗自习室");
        //发送邮件
        javaMailSender.send(mimeMessage);
    }
}
