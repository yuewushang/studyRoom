package UserService.Controller;

import UserService.Common.R;
import UserService.Domain.StreamResult;
import UserService.Domain.User;
import UserService.Service.UserService;
import UserService.Utills.JWTUtill;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
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
        String userId = request.getHeader("userId");
        //查询该用户的信息
        User byId = userService.getById(Long.parseLong(userId));
        //拼装返回地址
        String streamWord = byId.getStreamWord();
        String pushUrl = "rtmp://175.178.85.36/live/" + userId;
        String pullUrl = "http://175.178.85.36:8080/live/" + userId + "/" + streamWord + ".flv";
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
        String pullUrl = "http://175.178.85.36:8080/live/" + masterId + "/" + streamWord + ".flv";
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
}
