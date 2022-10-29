package UserService.Controller;

import UserService.Common.R;
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

import javax.servlet.http.HttpServletRequest;

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
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody User user){
        //查询数据库，获取用户信息
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(user.getUserId()!=null,User::getUserId,user.getUserId());
        User one = userService.getOne(wrapper);
        //如果用户不存在
        if(one==null){
            return R.error("该用户不存在");
        }
        //对密码进行md5加密
        String password = user.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //如果密码错误
        if(!password.equals(one.getPassword())){
            return R.error("密码错误");
        }
        //如果一切正常，生成token，并返回
        String token = JWTUtill.getToken(one.getUserId());
        //将用户信息存储在redis数据库中
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(String.valueOf(one.getUserId()),one);
        return R.success(token);
    }

    /**
     * 处理注册请求
     * @param user
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody User user){
        //查询数据库，看该用户是否存在
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(user.getUserId()!=null,User::getUserId,user.getUserId());
        User one = userService.getOne(wrapper);
        //如果用户存在
        if(one!=null){
            return R.error("该用户已经存在");
        }
        //对密码进行加密处理
        String password = user.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);
        user.setCreateUser(1L);
        user.setUpdateUser(1L);
        //添加用户
        userService.save(user);
        return R.success("注册成功");
    }

    /**
     * 看
     * @param request
     * @return
     */
    @GetMapping("/keepLogin")
    public R<String> keepLogin(HttpServletRequest request){
        //如果请求头中带有userId，则不用再登录了
        if(request.getHeader("userId")!=null){
            return R.success("已登录");
        }
        return R.error("未登录");
    }

}
