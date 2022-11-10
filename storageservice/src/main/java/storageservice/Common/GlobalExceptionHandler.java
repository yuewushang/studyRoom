package storageservice.Common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
}
