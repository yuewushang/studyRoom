package liveroomservice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import liveroomservice.Common.R;
import liveroomservice.Domain.Room;
import liveroomservice.Dto.RoomResult;
import liveroomservice.Service.RoomService;
import liveroomservice.Utill.StringUtill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

//控制层bean，restful风格
@RestController
@RequestMapping("/liveRoom")
@Slf4j
public class LiveRoomController {

    @Autowired
    private RoomService roomService;
    @GetMapping("/hello")
    public R<String> Hello(){
        return R.success("hello");
    }

    /**
     * 创建直播间
     * @param room
     * @return
     */
    @Transactional
    @PostMapping("/createRoom")
    public R<RoomResult> createLiveRoom(@RequestBody Room room, HttpServletRequest request){
        //获取用户id
        String userId = request.getHeader("userId");
        //查看该用户是否创建过直播间
        LambdaQueryWrapper<Room> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,Room::getMasterId,userId);
        Room one = roomService.getOne(wrapper);
        if(one!=null){
            return R.error("该用户已经创建过直播间，且该直播未结束");
        }
        room.setMasterId(Long.parseLong(userId));
        //设置默认图片
        if(room.getRoomImage()==null||room.getRoomImage().equals("")){
            room.setRoomImage("5198e027-acac-4259-ab35-9c7e7d6fdc43.png");
        }
        //创建直播间
        roomService.save(room);
        RoomResult roomResult=new RoomResult()  ;
        roomResult.setCode(1);
        roomResult.setMessage("创建直播间成功");
        roomResult.setRoomId(room.getRoomId());
        return R.success(roomResult);
    }

    /**
     * 修改房间的信息
     * @param room
     * @return
     */
    @PutMapping("/updateRoom")
    public R<String> updateRoom(@RequestBody Room room){
        //查询数据库，看是否有该房间的信息
        LambdaQueryWrapper<Room> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(StringUtill.isStringNull(String.valueOf(room.getRoomId())),Room::getRoomId,room.getRoomId());
        Room one = roomService.getOne(wrapper);
        if(one!=null){
            //修改房间信息
            LambdaQueryWrapper<Room> wrapper1=new LambdaQueryWrapper<>();
            wrapper1.eq(Room::getRoomId,room.getRoomId());
            roomService.update(room,wrapper1);
            return R.success("修改房间信息成功");
        }
        else {
            return R.error("该房间不存在");
        }

    }

    /**
     * 获取一个房间信息
     * @param roomId
     * @return
     */
    @GetMapping("/getOneRoom")
    public R<Room> getOneRoom(Long roomId){
        LambdaQueryWrapper<Room> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(StringUtill.isStringNull(String.valueOf(roomId)),Room::getRoomId,roomId);
        Room one = roomService.getOne(wrapper);
        if(one==null){
            return R.error("该房间不存在");
        }
        return R.success(one);
    }

    /**
     * 逻辑删除一个直播间
     * @param roomId
     * @return
     */
    @DeleteMapping("/deleteRoom")
    public R<String> logicDeleteRoom(Long roomId){
        //获取当前时间
        LocalDateTime time=LocalDateTime.now();
        //查询数据库
        LambdaQueryWrapper<Room> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(roomId!=null,Room::getRoomId,roomId);
        //获取该room的信息
        Room one = roomService.getOne(wrapper);
        //如果房间信息为空，则返回不存在
        if(one==null){
            return R.error("该房间不存在");
        }
//        //如果房间存在，更新其更新时间
//        one.setUpdateTime(time);
//        roomService.update(one,wrapper);
        //逻辑上删除之
        roomService.removeById(roomId);
        return R.success("删除房间成功");
    }


    /**
     * 获取用户创建的所以直播间
     * @param request
     * @return
     */
    @GetMapping("/getAllLiveroom")
    public R<List<Room>> getAllLiveRoom(HttpServletRequest request){
        String userId = request.getHeader("userId");
        LambdaQueryWrapper<Room>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,Room::getMasterId,Long.parseLong(userId));
        //安装创建时间降序排列
        wrapper.orderByAsc(Room::getCreateTime);
        List<Room> list = roomService.list(wrapper);
        return R.success(list);
    }

    /**
     * 获取所有直播视频
     * @return
     */
    @GetMapping("/getAllLiveRoomToLook")
    public R<List<Room>> getAllLiveRoomToLook(){
        List<Room> list = roomService.list();
        return R.success(list);
    }





}
