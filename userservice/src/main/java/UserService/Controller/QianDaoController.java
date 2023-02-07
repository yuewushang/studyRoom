package UserService.Controller;

import UserService.Common.R;
import UserService.Domain.QianDao;
import UserService.Service.QianDaoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/qiandao")
@Slf4j
public class QianDaoController {

    @Autowired
    private QianDaoService qianDaoService;


    /**
     * 添加签到数据
     * @param qianDao
     * @return
     */
    @PostMapping("/addQianDaoMessage")
    public R<String> addQianDaoMessage(@RequestBody QianDao qianDao){
        //获取该用户的所有签到记录
        LambdaQueryWrapper<QianDao>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(QianDao::getUserId,qianDao.getUserId());
        wrapper.orderByDesc(QianDao::getCreateTime);
        List<QianDao> list = qianDaoService.list(wrapper);
        if(list.size()!=0){
            //当前日期，形如2023/02/07的格式
            LocalDateTime nowTime = LocalDateTime.now();
            //转换时间格式
            String nowTimeString = nowTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            //获取该用户的最新签到记录的时间格式
            LocalDateTime createTime = list.get(0).getCreateTime();
            String format = createTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            if(format.equals(nowTimeString)){
                return R.success("您今天已经签到过了");
            }
        }
        //若没签到过，则签到
        qianDaoService.save(qianDao);
        return R.success("签到成功");
    }

    /**
     * 获取已经签到，要标记的日期
     * @param userId
     * @return
     */
    @GetMapping("/getMarKDate")
    public R<List<String>> getMarKDate(String userId){
        LambdaQueryWrapper<QianDao>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(QianDao::getUserId,Long.parseLong(userId));
        List<QianDao> list = qianDaoService.list(wrapper);
        List<String> result=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            LocalDateTime createTime = list.get(i).getCreateTime();
            String format = createTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            result.add(format);
        }
        return R.success(result);
    }





}
