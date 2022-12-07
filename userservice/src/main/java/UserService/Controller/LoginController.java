package UserService.Controller;

import UserService.Common.R;
import UserService.Domain.StreamResult;
import UserService.Domain.User;
import UserService.Domain.UserPlan;
import UserService.Service.UserPlanService;
import UserService.Service.UserService;
import UserService.Utills.JWTUtill;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//rest风格，控制层的bean
@RestController
@Slf4j
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserPlanService userPlanService;

    //手动负载俊豪
    private static int recordNumber=0;

    /**
     * 处理登录请求
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody User user, HttpServletResponse response) throws UnsupportedEncodingException {
        //查询数据库，获取用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(user.getUserName() != null, User::getUserName, user.getUserName());
        User one = userService.getOne(wrapper);
        //如果用户不存在
        if (one == null) {
            return R.error("该用户不存在");
        }
        //对密码进行md5加密
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //如果密码错误
        if (!password.equals(one.getPassword())) {
            return R.error("密码错误");
        }
        //如果一切正常，生成token，并返回
        String token = JWTUtill.getToken(one.getUserId());
        //将用户信息存储在redis数据库中
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //2小时后用户信息失效
        valueOperations.set(String.valueOf(one.getUserId()), one, 60 * 60 * 2, TimeUnit.SECONDS);
        if (user.getChecked() != null && user.getChecked()) {
            //将用户账号，密码放到cookie中
            Cookie cookie = new Cookie("userName", URLEncoder.encode(one.getUserName(), "UTF-8"));
            //设置2小时后过期
            cookie.setMaxAge(60 * 60 * 2);
            //使浏览器可以获取该cookie
            cookie.setPath("/");
            Cookie cookie1 = new Cookie("password", URLEncoder.encode(user.getPassword(), "UTF-8"));
            cookie1.setMaxAge(60 * 60 * 2);
            //使浏览器可以获取该cookie
            cookie1.setPath("/");
            //使浏览器可以获取该cookie
            response.addCookie(cookie);
            response.addCookie(cookie1);
        }

        return R.success(token);
    }

    /**
     * 处理注册请求
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        //查询数据库，看该用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(user.getUserName() != null, User::getUserName, user.getUserName());
        User one = userService.getOne(wrapper);
        log.info("注册请求，用户名：" + user.getUserName() + "搜素到的对象" + one);
        //如果用户存在
        if (one != null) {
            return R.error("该用户已经存在");
        }
        //对密码进行加密处理
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //创建一个独一无二的串流密钥
        String s = UUID.randomUUID().toString();
        System.out.println("生成的串流密钥为" + s);
        user.setStreamWord(s);
        user.setPassword(password);
        user.setCreateUser(1L);
        user.setUpdateUser(1L);
        //添加用户
        userService.save(user);
        //向添加用户队列发送消息
        //定义队列名
        String queue="addUser";
        //发送消息
        rabbitTemplate.convertAndSend(queue,user.getUserId().toString());
        return R.success("注册成功");
    }

    /**
     * 看
     *
     * @param request
     * @return
     */
    @GetMapping("/keepLogin")
    public R<String> keepLogin(HttpServletRequest request) {
        //如果请求头中带有userId，且缓存的user信息还存在，则不用再登录了
        if (request.getHeader("userId") != null) {
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Object userId = valueOperations.get(request.getHeader("userId"));
            if (userId != null) {
                return R.success("已登录");
            } else {
                return R.error("未登录");
            }
        }
        return R.error("未登录");
    }

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/getUserMessage")
    public R<User> getUserMessage(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        User byId = userService.getById(Long.parseLong(userId));
        return R.success(byId);
    }

    /**
     * 根据id获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/getUserMessageById")
    public User getUserMessageById(String userId){
        User byId = userService.getById(Long.parseLong(userId));
        return byId;
    }

    @GetMapping("/hello")
    public R<String> get() {
        return R.success("hello~");
    }

    /**
     * 获取推拉六地址
     *
     * @return
     */
    @GetMapping("/getKeyAndWord")
    public R<StreamResult> getKeyAndWord(HttpServletRequest request) {
        //srs服务器的ip地址
        String ipAddress="43.139.123.157";
        //端口号
        String port="8080";
        //获取记录数
        int record=recordNumber;
        //手动负载均衡
        if(record<3){
            if(record==1){
                ipAddress="1.12.230.201:19351";
                port="8081";
            }
            else if(record==2){
                ipAddress="43.138.211.210:19351";
                port="8081";
            }
        }else {
            record=record%3;
            recordNumber=recordNumber%3;
        }
        recordNumber++;
        String userId = request.getHeader("userId");
        //查询该用户的信息
        User byId = userService.getById(Long.parseLong(userId));
        //拼装返回地址
        String streamWord = byId.getStreamWord();
        String pushUrl = "rtmp://"+ipAddress+"/" + userId;
//        String pullUrl = "http://"+ipAddress+":"+port+"/" + userId + "/" + streamWord + ".flv";
        StreamResult streamResult = new StreamResult();
        streamResult.setKey(streamWord);
        streamResult.setPushUrl(pushUrl);
        return R.success(streamResult);
    }

    /**
     * 获取拉流地址
     *
     * @return
     */
    @GetMapping("/getPullUrl")
    public R<String> getPullUrl(String masterId) {
        User byId = userService.getById(Long.parseLong(masterId));
        String streamWord = byId.getStreamWord();
        String pullUrl = "http://43.139.123.157:8080/" + masterId + "/" + streamWord + ".flv";
        return R.success(pullUrl);
    }

    @GetMapping("/getUserIdByUserName")
    public R<String> getPullUrlAndRoomId(String userName) {
        //根据用户名去查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userName != null, User::getUserName, userName);
        User one = userService.getOne(wrapper);
        return R.success(String.valueOf(one.getUserId()));
    }

    /**
     * 提供给liveroom服务调用，获取四个同桌的拉流地址
     * @param userList
     * @return
     */
    @PostMapping("/getFourDeskMate")
    public List<String>getFourDeskMate(@RequestBody List<Long> userList){
        //srs服务器的ip地址
        String ipAddress="43.139.123.157";
        //端口号
        String port="8080";
        //获取记录数
        int record=recordNumber;
        //手动负载均衡
        if(record<3){
            if(record==1){
                ipAddress="1.12.230.201";
                port="8081";
            }
            else if(record==2){
                ipAddress="43.138.211.210";
                port="8081";
            }
        }else {
            record=record%3;
            recordNumber=recordNumber%3;
        }
        recordNumber++;
        List<String>list=new ArrayList<>();
        for(int i=0;i<userList.size();i++){
            User byId = userService.getById(userList.get(i));
            String streamWord = byId.getStreamWord();
            String pullUrl = "http://"+ipAddress+":"+port+"/" + byId.getUserId() + "/" + streamWord + ".flv";
            list.add(pullUrl);
        }
        return list;
    }


    /**
     * 用户创建每日计划
     * @param userPlan
     * @return
     */
    @PostMapping("/createUserPlanForToday")
    public R<String> createUserPlanForToday(@RequestBody UserPlan userPlan){
        //直接创建
        userPlanService.save(userPlan);
        return R.success("用户计划创建成功");
    }

    @GetMapping("/getUserPlanForToday")
    public R<List<UserPlan>> getUserPlanForToday(String userId){
        //创建一个时间模板
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");//年月日
        String format = simpleDateFormat.format(new Date());//获取当前时间的年月日
        //构造查询条件
        LambdaQueryWrapper<UserPlan>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,UserPlan::getUserId,userId);
        //根据创建时间升序排序
        wrapper.orderByAsc(UserPlan::getCreateTime);
        //查询
        List<UserPlan> list = userPlanService.list(wrapper);
        //遍历之，获取今天的学习计划
        List<UserPlan> reusltList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            //获取创建时间
            LocalDateTime createTime = list.get(i).getCreateTime();
            //将LocalDateTime转换为年月日的形式
            String format1 = createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            //如果是今天，则添加到结果集中
            if(format.equals(format1)){
                reusltList.add(list.get(i));
            }
        }
        return R.success(reusltList);
    }

    /**
     * 删除每日计划
     * @param userPlan
     * @return
     */
    @PutMapping("/deleteUserPlanForToday")
    public R<String> deleteUserPlanForToday(@RequestBody UserPlan userPlan){
        userPlanService.removeById(userPlan);
        return R.success("计划删除成功");
    }

}
