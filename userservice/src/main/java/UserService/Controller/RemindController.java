package UserService.Controller;

import UserService.Domain.QianDao;
import UserService.Domain.User;
import UserService.Service.QianDaoService;
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
import java.util.ArrayList;
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

    @Autowired
    private QianDaoService qianDaoService;

    //添加定时任务，每天早上7点执行,给用户发送早安提醒信息
    @Scheduled(cron = "0 0 7 * * ?")
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
            Thread.sleep(1000*30);
        }

    }


    /**
     * 提醒签到信息
     */
    @Scheduled(cron = "0 0 21 * * ?")
    public void SendRemindQianDaoMessage() throws UnsupportedEncodingException, MessagingException, InterruptedException {
        //获取所有签到的信息
        List<QianDao> list = qianDaoService.list();
        //遍历该集合，查找今天的签到信息
        //获取今天的日期
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        List<Long> temList=new ArrayList<>();
        //遍历集合
        for(int i=0;i<list.size();i++){
            String format1 = list.get(i).getCreateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            if(format.equals(format1)){
                //把签到过的人放入一个集合中
                temList.add(list.get(i).getUserId());
            }
            System.out.println(format+"    "+format1);
        }

        System.out.println("签到的id有"+temList);
        //获取所有用户的集合
        List<User> userList = userService.list();
        //结果集
        List<String>qqResult=new ArrayList<>();
        List<String>nameResult=new ArrayList<>();
        //遍历用户集合
        for(int i=0;i<userList.size();i++){
            //如果不在之前的集合名单中
            if(!temList.contains(userList.get(i).getUserId())){
                qqResult.add(userList.get(i).getQqMail());
                nameResult.add(userList.get(i).getUserName());
            }
        }
        //获取当前日期
        LocalDateTime now = LocalDateTime.now();
        //创建年月日，如2023-02-02的时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //将时间转换为上述的格式
        String time = now.format(dateTimeFormatter);
        //向没有签到的用户发送签到提醒
        for(int i=0;i<qqResult.size();i++){
            log.info("向邮箱为: "+qqResult.get(i)+" 的用户: "+nameResult.get(i)+" 发送提醒登录邮件"+"-----"+time);
            sendMailService.SendRemindQianDaoMail(qqResult.get(i),nameResult.get(i));
            //睡眠30s
            Thread.sleep(1000*30);
        }
    }

}
