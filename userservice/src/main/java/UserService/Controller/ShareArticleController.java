package UserService.Controller;

import UserService.Common.R;
import UserService.Domain.ShareArticle;
import UserService.Domain.UserFriends;
import UserService.Dto.LikesDto;
import UserService.Service.RedisService;
import UserService.Service.ShareArticleService;
import UserService.Service.UserFriendsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

//Restfull风格的controller
@RestController
@RequestMapping("/user/shareArticle")
public class ShareArticleController {

    @Autowired
    private ShareArticleService shareArticleService;

    @Autowired
    private UserFriendsService userFriendsService;

    @Autowired
    private RedisService redisService;


    /**
     * 创建说说文章
     * @param shareArticle
     * @return
     */
    @PostMapping("/createShareArticle")
    public R<String> createShareArticle(@RequestBody ShareArticle shareArticle){
        shareArticleService.save(shareArticle);
        return R.success("发布成功");
    }


    @GetMapping("/getShareArticle")
    @Transactional
    public R<List<ShareArticle>> getShareArticle(String userId){
        //把该用户的好友给查询出来
        LambdaQueryWrapper<UserFriends>friendsWrapper=new LambdaQueryWrapper<>();
        friendsWrapper.eq(UserFriends::getUserId,Long.parseLong(userId));
        List<UserFriends> friendList = userFriendsService.list(friendsWrapper);
        List<ShareArticle> resultList=new ArrayList<>();
        //查询用户的朋友们所发的说说
        for(int i=0;i<friendList.size();i++){
            UserFriends userFriends = friendList.get(i);
            LambdaQueryWrapper<ShareArticle>temWrapper=new LambdaQueryWrapper<>();
            temWrapper.eq(ShareArticle::getUserId,userFriends.getFriendId());
            temWrapper.eq(ShareArticle::getScope,"好友可见");
            List<ShareArticle> temList = shareArticleService.list(temWrapper);
            resultList.addAll(temList);
        }
        //查询所有人公开的那部分
        LambdaQueryWrapper<ShareArticle>allPeopleWrapper=new LambdaQueryWrapper<>();
        allPeopleWrapper.eq(ShareArticle::getScope,"所有人可见");
        List<ShareArticle> allPeopleOpenArticleList = shareArticleService.list(allPeopleWrapper);
        resultList.addAll(allPeopleOpenArticleList);

        //查询自己那部分
        LambdaQueryWrapper<ShareArticle>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShareArticle::getUserId,Long.parseLong(userId));
        wrapper.ne(ShareArticle::getScope,"所有人可见");
        List<ShareArticle> list = shareArticleService.list(wrapper);
        resultList.addAll(list);

        //对所有满足条件的文章按时间排序,用reversed()修改为降序排序
        resultList.sort(Comparator.comparing(ShareArticle::getCreateTime).reversed());

        return R.success(resultList);

    }

    /**
     * 删除用户发布的动态
     * @param shareArticle
     * @return
     */
    @DeleteMapping("/deleteShareArticle")
    public R<String> deleteShareArticle(@RequestBody ShareArticle shareArticle){
        shareArticleService.removeById(shareArticle);
        return R.success("动态删除成功");
    }

    /**
     * 添加点赞信息
     * @param likesDto
     * @return
     */
    @PostMapping("/addLikes")
    public R<String> addLikes(@RequestBody LikesDto likesDto){
       return redisService.addLikes(likesDto);
    }

    @DeleteMapping("/deleteLikes")
    public R<String> deleteLikes(@RequestBody LikesDto likesDto){
        return redisService.deleteLikes(likesDto);
    }

    /**
     * 获取所有的点赞信息
     * @return
     */
    @GetMapping("/getAllLikes")
    public R<List<String>> getAllLikes(){
        R<List<String>> allLikes = redisService.getAllLikes();
        return allLikes;
    }


}
