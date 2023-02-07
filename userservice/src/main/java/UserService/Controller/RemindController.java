package UserService.Controller;

import UserService.Domain.User;
import UserService.Service.SendMailService;
import UserService.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 提醒控制器
 */
@Component
@Slf4j
public class RemindController {

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private  UserService userService;

    //添加定时任务，每天早上7点执行,给用户发送早安提醒信息
    @Scheduled(cron = "0 15 11 * * ?")
    public void SendRemindMessage() throws UnsupportedEncodingException, MessagingException, InterruptedException {
        //获取当前日期
        LocalDateTime now = LocalDateTime.now();
        //创建年月日，如2023-02-02的时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //将时间转换为上述的格式
        String time = now.format(dateTimeFormatter);
        //查询用户名单
        List<User> list = userService.list();
        //遍历之，发送提醒邮件
        for(int i=0;i<list.size();i++){
            String qqMail = list.get(i).getQqMail();
            String userName = list.get(i).getUserName();
            //发送邮件
            log.info("正在向用户： "+userName+" ,邮箱为 "+qqMail+"发送邮件...------"+time);
            sendMailService.SendRemindMail(qqMail,userName);
            log.info("用户 "+userName+" ，邮箱地址为："+qqMail+" 的邮件发送成功。-------"+time);
            //睡眠20s
            Thread.sleep(1000*20);
        }

    }

}
