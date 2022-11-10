package liveroomservice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import liveroomservice.Common.R;
import liveroomservice.Domain.Audience;
import liveroomservice.Domain.CallbackDataOnDvr;
import liveroomservice.Domain.Room;
import liveroomservice.Service.AudienceService;
import liveroomservice.Service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/liveRoom")
public class JoinLiveRoomController {
    @Autowired
    private AudienceService audienceService;
    @Autowired
    private RoomService roomService;

    /**
     * 加入直播间
     * @param
     * @return
     */
    @GetMapping("/addAudience")
    @Transactional
    public R<String> addAudience(Long roomId, HttpServletRequest request){
        //先检查该直播间是否存在
        LambdaQueryWrapper<Room> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(roomId!=null,Room::getRoomId,roomId);
        Room one = roomService.getOne(wrapper);
        if(one==null){
            return R.error("该房间不存在");
        }
        //再检查该用户是否在该直播间中
        //获取用户id
        String userId = request.getHeader("userId");
        LambdaQueryWrapper<Audience>wrapper1=new LambdaQueryWrapper<>();
        wrapper1.eq(userId!=null,Audience::getUserId,Long.parseLong(userId));
        //查询
        Audience one1 = audienceService.getOne(wrapper1);
        //如果还没加入直播间，则加入直播间
        if(one1==null){
            Audience audience=new Audience();
            audience.setRoomId(roomId);
            audience.setUserId(Long.parseLong(userId));
            UUID uuid = UUID.randomUUID();
            audience.setAudienceName("观众"+uuid.toString());
            //如果是直播间的主人，则身份为房主
            if(one.getMasterId()==Long.parseLong(userId)){
                audience.setAudienceIdentify("房主");
                audience.setAudienceName("房主");
            }
            LocalDateTime time=LocalDateTime.now();
            audience.setEnterTime(time);
            audienceService.save(audience);
            return R.success("欢迎加入房间");
        }
        //如果之前已经加入过了，则修改其状态为在线
        one1.setAudienceStatus("在线");
        //保存到数据库中
        LambdaQueryWrapper<Audience> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.eq(Audience::getUserId,one1.getUserId());
        audienceService.update(one1,wrapper2);
        return R.success("欢迎回来");
    }

    /**
     * 离开直播间，将状态设为离开
     * @param roomId
     * @param request
     * @return
     */
    @PutMapping("/leaveRoom")
    public R<String> leaveRoom(Long roomId, HttpServletRequest request){
        //获取用户id
        String userId = request.getHeader("userId");
        Long id=Long.parseLong(userId);
        //构造修改条件
        LambdaQueryWrapper<Audience> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Audience::getRoomId,roomId);
        wrapper.eq(Audience::getUserId,id);
        //查询该用户是否在直播间中
        Audience one = audienceService.getOne(wrapper);
        if(one==null){
            return R.error("该用户未来过该房间，又何谈离开呢");
        }
        //修改状态
        one.setAudienceStatus("离开");
        one.setLeaveTime(LocalDateTime.now());
        audienceService.update(one,wrapper);
        return R.success("欢迎下次回来");
    }

    /**
     * srs存储完直播文件后的回调函数
     * @param callbackDataOnDvr
     * @return
     */
    @PostMapping("save")
    public int saveFlvFile(@RequestBody CallbackDataOnDvr callbackDataOnDvr){
        log.info("srs服务存储完flv文件"+callbackDataOnDvr.getFile());
        System.out.println(callbackDataOnDvr);
        return 0;
    }

}
