package UserService.Utills;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;

public class JWTUtill {
    private static String secretKey="yuewushuang";//密钥，用于生成token
    //私有化构造函数，使之成为工具类
    private JWTUtill(){

    }

    /**
     * 根据用户id生成token
     * @param userId
     * @return
     */
    public static String getToken(Long userId){
        //创建一个日历对象，获取时间
        Calendar calendar=Calendar.getInstance();
        //把该时间值设置在2小时之后
        calendar.add(Calendar.SECOND,60*60*2);
        //生成token
        String token= JWT.create()
                .withClaim("userId",userId) //设置用户名负载
                .withExpiresAt(calendar.getTime())//设置过期时间，在两小时之后
                .sign(Algorithm.HMAC256(secretKey));//设置生成的算法为HMAC256，和密钥
        return token;
    }

    /**
     * 检验token
     * @param token
     */
    public static String checkToken(String token){
        //创建JWTVerify对象，设置相同的算法和密钥
        JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(secretKey)).build();
        //解析token
        try {
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            String userId = decodedJWT.getClaim("userId").asString();
            return userId;
        }
        catch (Exception e){
            return null;
        }
    }
}
