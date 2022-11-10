package InteractionService.Controller;

import InteractionService.Common.R;
import InteractionService.Domain.Message;
import InteractionService.Service.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
    @RequestMapping("/interaction/bullet")
public class bulletScreenController {

    @Autowired
    private MessageService messageService;
    /**
     * 实验
     */
    @GetMapping("/hello")
    public R<String> hello(){
        return R.success("hello,interaction");
    }

    /**
     * 获取直播间中的所有弹幕
     * @param roomId
     * @return
     */
    @GetMapping("/getRoomBullet")
    public R<List<Message>> getRoomBullet(String roomId){
        //构造查询条件
        LambdaQueryWrapper<Message>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(roomId!=null,Message::getRoomId,roomId);
        //根据创建时间降序排序
        wrapper.orderByAsc(Message::getCreateTime);
        List<Message> list = messageService.list(wrapper);
        return R.success(list);
    }

}
