package UserService.Common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.exceptions.*;
import java.sql.SQLIntegrityConstraintViolationException;

//全局对象捕获
//处理rest风格的controller抛出的异常
@ControllerAdvice(annotations = RestController.class)
//将返回信息封装成json
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理违反sql完整性约数的异常
     * @param e
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        //输出日志
        log.error("出错了{}",e.getMessage());

        return R.error("未知错误");
    }

    /**
     * 处理密钥错误异常
     * @param e
     * @return
     */
    @ExceptionHandler(SignatureVerificationException.class)
    public R<String> signatureHandler(SignatureGenerationException e){
        //输出日志
        log.error("出错了{}",e.getMessage());
        return R.error("密钥错误，请重新登录");
    }

    /**
     * 处理token过期错误
     * @param e
     * @return
     */
    @ExceptionHandler(TokenExpiredException.class)
    public R<String> tokenHandler(TokenExpiredException e){
        //输出日志
        log.error("出错了{}",e.getMessage());
        return R.error("token已经过期，请重新登录");
    }

    /**
     * 处理jwt算法不匹配错误
     * @param e
     * @return
     */
    @ExceptionHandler(AlgorithmMismatchException.class)
    public R<String> algorithmHandler(AlgorithmMismatchException e){
        //输出日志
        log.error("出错了{}",e.getMessage());
        return R.error("token生成算法不匹配");
    }






}
