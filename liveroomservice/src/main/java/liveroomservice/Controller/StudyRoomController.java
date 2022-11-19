package liveroomservice.Controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import liveroomservice.Client.UserClient;
import liveroomservice.Common.R;
import liveroomservice.Domain.*;
import liveroomservice.Dto.EnterStudyRoomVerify;
import liveroomservice.Dto.ShareStudyRoom;
import liveroomservice.Service.StudyRoomMemberService;
import liveroomservice.Service.StudyRoomPlanService;
import liveroomservice.Service.StudyRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studyRoom")
public class StudyRoomController {
    //注入studyroom服务
    @Autowired
    private StudyRoomService studyRoomService;
    //注入studyroomplan服务
    @Autowired
    private StudyRoomPlanService studyRoomPlanService;

    @Autowired
    private StudyRoomMemberService studyRoomMemberService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 创建自习室
     * @param studyRoom
     * @return
     */
    @PostMapping("/createStudyRoom")
    @Transactional
    public R<String> createStudyRoom(@RequestBody StudyRoom studyRoom){
        //查看是否已经有该名字的自习室了
        LambdaQueryWrapper<StudyRoom> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoom.getStudyRoomName()!=null,StudyRoom::getStudyRoomName,studyRoom.getStudyRoomName());
        StudyRoom one = studyRoomService.getOne(wrapper);
        //如果已经有该名字的自习室了，返回提示信息
        if(one!=null){
            return R.error("自习室创建失败，请更换一个名字");
        }
        //如果可以创建，则去创建
        studyRoom.setStudyRoomMemberNumber(1);
        studyRoomService.save(studyRoom);
        //在创建直播间成功后，添加第一个成员
        StudyRoomMember member=new StudyRoomMember();
        member.setIsOnline(0);
        member.setStudyRoomId(studyRoom.getStudyRoomId());
        member.setUserId(studyRoom.getUserId());
        member.setUserName(studyRoom.getUserName());
        member.setMemberIdentify("房主");
        studyRoomMemberService.save(member);
        //向队列中发送消息
        String queue="addStudyRoom";
        //发送消息
        rabbitTemplate.convertAndSend(queue,studyRoom.getStudyRoomId().toString());
        //创建一个
        return R.success("自习室间创建成功");
    }


    /**
     * 根据用户id去获取创创建的自习室的信息
     * @param userId
     * @return
     */
    @GetMapping("/getAllStudyRoomByUserId")
    public R<List<StudyRoom>> getAllStudyRoomByUserId(String userId){
        //构建查询条件
        LambdaQueryWrapper<StudyRoom>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,StudyRoom::getUserId,Long.parseLong(userId));
        //根据创建时间降序排序
        wrapper.orderByDesc(StudyRoom::getCreateTime);
        List<StudyRoom> list = studyRoomService.list(wrapper);
        //返回信息
        return R.success(list);
    }

    /**
     * 更具自习室的id去删除自习室
     * @param studyRoomId
     * @return
     */
    @DeleteMapping("/deleteStudyRoom/{studyRoomId}")
    public R<String> deleteStudyRoom(@PathVariable(value = "studyRoomId")String studyRoomId){
        log.info(studyRoomId);
        //直接删除之
        studyRoomService.removeById(Long.parseLong(studyRoomId));
        //定义队列
        String queue="deleteStudyRoom";
        rabbitTemplate.convertAndSend(queue,studyRoomId);
        return R.success("删除自习室成功");
    }

    /**
     * 根据id去获取某个自习室的信息
     * @param studyRoomId
     * @return
     */
    @GetMapping("/getStudyRoomMessage")
    public R<StudyRoom> getStudyRoomMessage(String studyRoomId){
        //查询该自习室是否存在
        LambdaQueryWrapper<StudyRoom> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoomId!=null,StudyRoom::getStudyRoomId,Long.parseLong(studyRoomId));
        StudyRoom one = studyRoomService.getOne(wrapper);
        if(one==null){
            return R.error("该自习室不存在");
        }
        //如果一切正常返回直播间信息
        return R.success(one);
    }


    /**
     * 更新自习室的信息
     * @param studyRoom
     * @return
     */
    @PutMapping("/updateStudyRoomMessage")
    public R<String> updateStudyRoomMessage(@RequestBody StudyRoom studyRoom){
        //直接更新之
        studyRoomService.updateById(studyRoom);
        String queue="updateStudyRoom";
        rabbitTemplate.convertAndSend(queue,studyRoom.getStudyRoomId().toString());
        return R.success("修改自习室信息成功");
    }

    /**
     * 创建自习室计划
     * @param studyRoomPlan
     * @return
     */
    @PostMapping("/createStudyRoomPlan")
    public R<String> createStudyRoomPlan(@RequestBody StudyRoomPlan studyRoomPlan){
        //先查看该自习室算是否存在
        LambdaQueryWrapper<StudyRoom>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoomPlan.getStudyRoomId()!=null,StudyRoom::getStudyRoomId,studyRoomPlan.getStudyRoomId());
        //查询
        StudyRoom one = studyRoomService.getOne(wrapper);
        //如果该自习室不存在，返回错误信息
        if(one==null){
            return R.error("该自习室不存在");
        }
        //如果一切正常，去创建一个自习室的计划
        studyRoomPlanService.save(studyRoomPlan);
        return R.success("自习室计划添加成功");
    }


    /**
     * 获取某个自习室的所有计划
     * @param studyRoomId
     * @return
     */
    @GetMapping("/getStudyRoomPlan")
    public R<List<StudyRoomPlan>> getStudyRoomPlan(String studyRoomId){
        LambdaQueryWrapper<StudyRoomPlan>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoomId!=null,StudyRoomPlan::getStudyRoomId,Long.parseLong(studyRoomId));
        //查询
        List<StudyRoomPlan> list = studyRoomPlanService.list(wrapper);
        //返回数据
        return R.success(list);
    }


    /**
     * 根据自习室计划的id去删除该计划
     * @param studyroomPlanId
     * @return
     */
    @DeleteMapping("/deleteStudyRoomPlan/{studyroomPlanId}")
    public R<String> deleteStudyRoomPlan(@PathVariable("studyroomPlanId") String studyroomPlanId){
        //看该计划是否存在
        StudyRoomPlan byId = studyRoomPlanService.getById(Long.parseLong(studyroomPlanId));
        if(byId==null){
            return R.error("该计划不存在或者已经被删除");
        }
        //如果一切正常，删除该计划
        studyRoomPlanService.removeById(Long.parseLong(studyroomPlanId));
        return R.success("计划删除成功");
    }


    /**
     * 进入一个自习室之前的验证
     * @param enterStudyRoomVerify
     * @return
     */
    @PostMapping("/enterStudyRoom")
    @Transactional
    public R<String> enterStudyRoom(@RequestBody EnterStudyRoomVerify enterStudyRoomVerify){
        //看该用户是否是这个自习室的成员
        Long studyRoomId = enterStudyRoomVerify.getStudyRoomId();
        Long userId = enterStudyRoomVerify.getUserId();
        //构造查询条件
        LambdaQueryWrapper<StudyRoomMember>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoomId!=null,StudyRoomMember::getStudyRoomId,studyRoomId);
        wrapper.eq(userId!=null,StudyRoomMember::getUserId,userId);
        StudyRoomMember one = studyRoomMemberService.getOne(wrapper);
        //如果结果不为空，说明这个用户已经是团队成员了
        if(one!=null){
            //更新其在线状态
            one.setIsOnline(1);
            studyRoomMemberService.update(one,wrapper);
            return R.success("允许进入");
        }
        //如果结果为空，则去看是否是公共自习室，查询密码是否正确
        StudyRoom byId = studyRoomService.getById(studyRoomId);
        //如果是公共自习室
        if(byId.getStudyRoomType().equals("公共自习室")){
            return R.success("允许进入");
        }
        //获取自习室密码
        String studyRoomPassword = byId.getStudyRoomPassword();
        //如果密码正确，则允许进入
        if(studyRoomPassword.equals(enterStudyRoomVerify.getStudyRoomPassword())){
            return R.success("允许进入");
        }
        //如果上诉的条件都不满足
        return R.error("密码错误，请重新输入密码");
    }


    /**
     * 点击了加入自习室的按钮
     * @param verify
     * @return
     */
    @PostMapping("/addStudyRoom")
    @Transactional
    public R<String> addStudyRoom(@RequestBody StudyRoomMember verify){
        //查看是否已经是自习室的成员
        LambdaQueryWrapper<StudyRoomMember> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(verify.getStudyRoomId()!=null,StudyRoomMember::getStudyRoomId,verify.getStudyRoomId());
        wrapper.eq(verify.getUserId()!=null,StudyRoomMember::getUserId,verify.getUserId());
        StudyRoomMember one = studyRoomMemberService.getOne(wrapper);
        //如果不为空，则已经是该自习室的成员了
        if(one!=null){
            return R.success("你已经是该自习室的成员了，请勿重复加入");
        }
        //如果还不是成员，则加入之
        studyRoomMemberService.save(verify);
        //自习室的成员数加1
        StudyRoom byId = studyRoomService.getById(verify.getStudyRoomId());
        byId.setStudyRoomMemberNumber(byId.getStudyRoomMemberNumber()+1);
        //更新成员人数
        studyRoomService.updateById(byId);
        return R.success("加入自习室成功");
    }


    /**
     * 退出该自习室，从群组的角度退出
     * @param studyRoomMember
     * @return
     */
    @DeleteMapping("/leaveStudyRoom")
    @Transactional
    public R<String>  leaveStudyRoom(@RequestBody StudyRoomMember studyRoomMember){
        //构造查询条件
        LambdaQueryWrapper<StudyRoomMember>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoomMember.getStudyRoomId()!=null,StudyRoomMember::getStudyRoomId,studyRoomMember.getStudyRoomId());
        wrapper.eq(studyRoomMember.getUserId()!=null,StudyRoomMember::getUserId,studyRoomMember.getUserId());
        studyRoomMemberService.remove(wrapper);
        //自习室成员数减1
        StudyRoom byId = studyRoomService.getById(studyRoomMember.getStudyRoomId());
        byId.setStudyRoomMemberNumber(byId.getStudyRoomMemberNumber()-1);
        studyRoomService.updateById(byId);

        return R.success("退出该自习室成功");
    }


    /**
     * 在退出自习室的同时，更新学习时长,在线状态
     * @param studyRoomMember
     * @return
     */
    @PutMapping("/updateStudyTime")
    public R<String> updateStudyTime(@RequestBody StudyRoomMember studyRoomMember){
        //查询自习室中原有的成员
        LambdaQueryWrapper<StudyRoomMember>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(studyRoomMember.getStudyRoomId()!=null,StudyRoomMember::getStudyRoomId,studyRoomMember.getStudyRoomId());
        wrapper.eq(studyRoomMember.getUserId()!=null,StudyRoomMember::getUserId,studyRoomMember.getUserId());
        StudyRoomMember one = studyRoomMemberService.getOne(wrapper);
        //如果该用户不是该自习室的成员，则返回提示信息
        if(one==null){
            return R.error("该用户还未加入该自习室");
        }
        //如果已经是了，修改其在线状态，更新其学习时长
        Double lengthOfStudy = one.getLengthOfStudy();
        lengthOfStudy=lengthOfStudy+studyRoomMember.getLengthOfStudy();
        one.setLengthOfStudy(lengthOfStudy);
        //将状态改为下线
        one.setIsOnline(0);
        studyRoomMemberService.update(one,wrapper);
        return R.success("更新学习时长成功");
    }


    /**
     * 获取排行榜信息
     * @param studyRoomId
     * @return
     */
    @GetMapping("/getRankingList")
    public R<List<StudyRoomMember>> getRankingList(String studyRoomId){
        //将字符串转换为Long型变量
        Long l = Long.parseLong(studyRoomId);
        //获取其成员信息
        LambdaQueryWrapper<StudyRoomMember> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(l!=null,StudyRoomMember::getStudyRoomId,l);
        //按照学习时长降序排列
        wrapper.orderByDesc(StudyRoomMember::getLengthOfStudy);
        List<StudyRoomMember> list = studyRoomMemberService.list(wrapper);
        return R.success(list);
    }


    /**
     * 获取该自习室的在线成员列表
     * @param studyRoomId
     * @return
     */
    @GetMapping("/getOnlineMember")
    public R<List<StudyRoomMember>> getOnlineMember(String studyRoomId){
        Long l = Long.parseLong(studyRoomId);
        //获取其成员信息
        LambdaQueryWrapper<StudyRoomMember> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(l!=null,StudyRoomMember::getStudyRoomId,l);
        wrapper.eq(StudyRoomMember::getIsOnline,1);
        wrapper.orderByDesc(StudyRoomMember::getLengthOfStudy);
        List<StudyRoomMember> list = studyRoomMemberService.list(wrapper);
        return R.success(list);
    }


    /**
     * 分享自习室
     * @param studyRoomId
     * @return
     */
    @GetMapping("/shareStudyRoom")
    public R<ShareStudyRoom> shareStudyRoom(String studyRoomId){
        //查询该自习室的信息
        StudyRoom byId = studyRoomService.getById(Long.parseLong(studyRoomId));
        //封装结果
        ShareStudyRoom shareStudyRoom=new ShareStudyRoom();
        shareStudyRoom.setStudyRoomIntroduction(byId.getStudyRoomIntroduction());
        shareStudyRoom.setStudyRoomName(byId.getStudyRoomName());
        shareStudyRoom.setStudyRoomPassword(byId.getStudyRoomPassword());
        return R.success(shareStudyRoom);
    }

    /**
     * 获取该用户加入的所有自习室的信息
     * @param userId
     * @return
     */
    @GetMapping("/getAllJoinedStudyRoomByUserId")
    public R<List<StudyRoom>>getAllJoinedStudyRoomByUserId(String userId){
        //构造查询条件
        LambdaQueryWrapper<StudyRoomMember>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,StudyRoomMember::getStudyRoomId,Long.parseLong(userId));
        List<StudyRoomMember> list = studyRoomMemberService.list(wrapper);
        List<StudyRoom>studyRoomList=new ArrayList<>();
        //根据所得studyRoomId去查询房间
        //遍历之
        for(int i=0;i<list.size();i++){
            StudyRoomMember member = list.get(i);
            Long studyRoomId = member.getStudyRoomId();
            StudyRoom byId = studyRoomService.getById(studyRoomId);
            studyRoomList.add(byId);
        }
        return R.success(studyRoomList);
    }


    @GetMapping("/getDeskMate")
    public R<List<String>> getDeskMate(String studyRoomId,String userId){
        //获取在线成员
        Long l = Long.parseLong(studyRoomId);
        //获取其成员信息
        LambdaQueryWrapper<StudyRoomMember> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(l!=null,StudyRoomMember::getStudyRoomId,l);
        wrapper.eq(StudyRoomMember::getIsOnline,1);
        wrapper.orderByDesc(StudyRoomMember::getLengthOfStudy);
        List<StudyRoomMember> list = studyRoomMemberService.list(wrapper);
        //记录符合条件的用户id
        List<Long>userIdList=new ArrayList<>();
        //如果在线的成员数少于4人，
        if(list.size()<=4){
            //调用userService服务，获取这几个人的拉流地址
            //获取用户id
            for(int i=0;i<list.size();i++){
                userIdList.add(list.get(i).getUserId());
            }
            //发送服务调用请求
            List<String> fourDeskMate = userClient.getFourDeskMate(userIdList);
            return R.success(fourDeskMate);

        }
        int location=0;//用户所在位置
        //获取该用户所在的位置
        for(int i=0;i<list.size();i++){
            //如果找到了，记录下来
            if(list.get(i).getUserId().equals(Long.parseLong(userId))){
                location=i;
                break;
            }
        }
        //如果位置等于4.则返回前四名
        if(location==4){
            //返回前四名的拉流地址
            for(int i=0;i<4;i++){
                userIdList.add(list.get(i).getUserId());
            }
            //发送服务调用
            List<String> fourDeskMate = userClient.getFourDeskMate(userIdList);
            return R.success(fourDeskMate);
        }
        //如果位置小于4，则要借用后面的
        if(location<4&&list.size()>=4){
            for(int i=0;i<4;i++){
                userIdList.add(list.get(i).getUserId());
            }
            //发送服务调用
            List<String> fourDeskMate = userClient.getFourDeskMate(userIdList);
            return R.success(fourDeskMate);
        }
        if(location<4&&list.size()<4){
            for(int i=0;i<list.size();i++){
                userIdList.add(list.get(i).getUserId());
            }
            //发送服务调用
            List<String> fourDeskMate = userClient.getFourDeskMate(userIdList);
            return R.success(fourDeskMate);
        }
        //如果一切正常，则返回前三人和自己的拉流地址
        for(int i=location;i>=0;i--){
            userIdList.add(list.get(i).getUserId());
        }
        //发送服务调用
        List<String> fourDeskMate = userClient.getFourDeskMate(userIdList);
        return R.success(fourDeskMate);

    }



}
