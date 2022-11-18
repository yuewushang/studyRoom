package searchService.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.web.bind.annotation.*;
import searchService.Common.R;
import searchService.Domain.StudyRoom;
import searchService.Domain.User;
import searchService.Dto.UserDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/search")
@Slf4j
public class ElasticSearchController {

    //创建一个restHighLevelClient来操作elasticSearch
    public RestHighLevelClient createRestHighLevelClient(){
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://121.37.24.27:9200")
        ));
        return restHighLevelClient;
    }

    /**
     * 向elasticSearch中更新数据
     * @param user
     * @return
     */
    @PostMapping("/addUserToElasticSearch")
    public R<String> addUserToElasticSearch(@RequestBody User user) throws IOException {
        //创建一个对象，用于存储要放到elasticSearch中的数据
        user.setSuggestion(Arrays.asList(user.getUserName(),user.getUserId().toString()));
        JSONObject object=new JSONObject();
        object.put("user_id",user.getUserId());
        object.put("user_name",user.getUserName());
        object.put("image_url",user.getImageUrl());
        object.put("suggestion",user.getSuggestion());
        //转换为json字符串
        String s = JSON.toJSONString(object);
        //创建一个RestHighLevelClient
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建请求对象,并使用userId做为文档id
        IndexRequest indexRequest=new IndexRequest("user").id(user.getUserId().toString());
        //以json的格式包装数据
        indexRequest.source(s, XContentType.JSON);
        //发送请求
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        //关闭客户端
        restHighLevelClient.close();
        return R.success("添加user成功");
    }

    /**
     * 更新elasticSearch中的用户数据
     * @param user
     * @return
     */
    @PutMapping("/updateUserFromElasticSearch")
    public R<String> updateUserFromElasticSearch(@RequestBody User user) throws IOException {
        //创建restHighLevelClient
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建更新请求对象
        UpdateRequest request=new UpdateRequest("user",user.getUserId().toString());
        //设置更新内容
        request.doc(
                "user_name",user.getUserName()
        );
        //发送更新请求
        restHighLevelClient.update(request,RequestOptions.DEFAULT);
        //关闭客户端
        restHighLevelClient.close();
        return R.success("数据更新成功");
    }

    /**
     * 删除elasticSearch中的user数据
     * @param userId
     * @return
     */
    @DeleteMapping("/deleteUserFromElasticSearch")
    public R<String> deleteUserFromElasticSearch(String userId) throws IOException {
        //创建RestHighLevelClient对象
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建删除请求对象
        DeleteRequest deleteRequest=new DeleteRequest("user",userId);
        //发送删除请求
        restHighLevelClient.delete(deleteRequest,RequestOptions.DEFAULT);
        //关闭客户端
        restHighLevelClient.close();
        return R.success("数据删除成功");
    }

    /**
     * 根据id或者用户名去搜素用户
     * @param keyword
     * @return
     */
    @GetMapping("/queryUsersFromElasticSearch")
    public R<List<User>> queryUsersFromElasticSearch(String keyword) throws IOException {
        //创建RestHighLevelClient
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建查询请求
        SearchRequest searchRequest=new SearchRequest("user");
        searchRequest.source().query(QueryBuilders.matchQuery("all",keyword));
        //发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析请求结果，获取大hits
        SearchHits hits = search.getHits();
        //获取结果数组
        SearchHit[] hits1 = hits.getHits();
        //创建一个列表，来存储解析出的java对象
        List<User>list=new ArrayList<>();
        //遍历取值
        for(SearchHit searchHit:hits1){
            String sourceAsString = searchHit.getSourceAsString();
            //转换为java对象
            User user = JSON.parseObject(sourceAsString, User.class);
            //加入结果数组
            list.add(user);
        }
        //关闭客户端
        restHighLevelClient.close();
        return R.success(list);
    }


    /**
     * 自动补全功能
     * @param keyword
     * @return
     */
    @GetMapping("/autoComplementUser")
    public R<List<String>> autoComplementUser(String keyword) throws IOException {
        //创建RestHighLevelClient
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建搜素请求
        SearchRequest searchRequest=new SearchRequest("user");
        searchRequest.source().suggest(new SuggestBuilder().addSuggestion(
                "suggester",
                SuggestBuilders.completionSuggestion("suggestion") //自动补全的字段
                .prefix(keyword) //根据关键词查找
                .skipDuplicates(true)  //跳过重复的词
                .size(10) //返回10条
        ));
        //发起请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        //获取suggestion处理结果
        Suggest suggest = search.getSuggest();
        //根据名称获取补全结果
        CompletionSuggestion suggestion=suggest.getSuggestion("suggester");
        List<String> list=new ArrayList<>();
        //遍历options获取结果
        for(CompletionSuggestion.Entry.Option option:suggestion.getOptions()){
            String s = option.getText().toString();
            list.add(s);
        }
        //关闭客户端
        restHighLevelClient.close();

        return R.success(list);
    }

    /**
     * 向elasticSearch中添加studyRoom
     * @param studyRoom
     * @return
     */
    @PostMapping("/addStudyRoomToElasticSearch")
    public R<String> addStudyToElasticSearch(@RequestBody StudyRoom studyRoom) throws IOException {
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        studyRoom.setSuggestion(Arrays.asList(studyRoom.getStudyRoomId().toString(),
                studyRoom.getStudyRoomName(),
                studyRoom.getStudyRoomType(),
                studyRoom.getStudyRoomIntroduction(),
                studyRoom.getUserId().toString(),
                studyRoom.getUserName()));
        //构造对象
        String s = JSON.toJSONString(studyRoom);
        //创建请求对象
        IndexRequest indexRequest=new IndexRequest("study_room").id(studyRoom.getStudyRoomId().toString());
        //指定形式为json字符串
        indexRequest.source(s,XContentType.JSON);
        //发送请求
        restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
        //关闭客户端
        restHighLevelClient.close();
        return R.success("添加数据成功");

    }


    /**
     * 更新elasticSearch中的studyRoom数据
     * @param studyRoom
     * @return
     */
    @PutMapping("/updateStudyRoomToElasticSearch")
    public R<String> updateStudyRoomToElasticSearch(@RequestBody StudyRoom studyRoom) throws IOException {
        //创建客户端
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        studyRoom.setSuggestion(Arrays.asList(studyRoom.getStudyRoomId().toString(),
                studyRoom.getStudyRoomName(),
                studyRoom.getStudyRoomType(),
                studyRoom.getStudyRoomIntroduction(),
                studyRoom.getUserId().toString(),
                studyRoom.getUserName()));
        //创建更新请求
        UpdateRequest updateRequest=new UpdateRequest("study_room",studyRoom.getStudyRoomId().toString());
        //更新内容
        updateRequest.doc(
                "studyRoomId",studyRoom.getStudyRoomId().toString(),
                "studyRoomName",studyRoom.getStudyRoomName(),
                "studyRoomType",studyRoom.getStudyRoomType(),
                "studyRoomIntroduction",studyRoom.getStudyRoomIntroduction(),
                "studyRoomMemberNumber",studyRoom.getStudyRoomMemberNumber(),
                "studyRoomImageName",studyRoom.getStudyRoomImageName(),
                "studyRoomPassword",studyRoom.getStudyRoomPassword(),
                "userId",studyRoom.getUserId(),
                "userName",studyRoom.getUserName(),
                "isDelete",studyRoom.getIsDelete(),
                "suggestion",studyRoom.getSuggestion()

        );
        //发送请求
        restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);
        //关闭客户端
        restHighLevelClient.close();
        return R.success("更新数据成功");
    }

    /**
     * 全文检索studyRoom
     * @param keyword
     * @return
     */
    @GetMapping("/queryStudyRoomToElasticSearch")
    public R<List<StudyRoom>> queryStudyRoomToElasticSearch(String keyword) throws IOException {
        //创建客户端
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建请求
        SearchRequest searchRequest=new SearchRequest("study_room");
        searchRequest.source().query(QueryBuilders.matchQuery("all",keyword));
        //发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析请求结果
        //获取大Hits
        SearchHits hits = search.getHits();
        //获取结果数组
        SearchHit[] hits1 = hits.getHits();
        List<StudyRoom>list=new ArrayList<>();
        //遍历取值
        for(SearchHit searchHit:hits1){
            String sourceAsString = searchHit.getSourceAsString();
            //转换为java对象
            StudyRoom studyRoom = JSON.parseObject(sourceAsString, StudyRoom.class);
            list.add(studyRoom);
        }
        return R.success(list);
    }

    /**
     * 自动补全studyRoom的搜素
     * @param keyword
     * @return
     */
    @GetMapping("/autoComplementStudyRoom")
    public R<List<String>> autoComplementStudyRoom(String keyword) throws IOException {
        //创建客户端
        RestHighLevelClient restHighLevelClient = createRestHighLevelClient();
        //创建请求
        SearchRequest searchRequest=new SearchRequest("study_room");
        searchRequest.source().suggest(new SuggestBuilder().addSuggestion(
                "suggestion",
                SuggestBuilders.completionSuggestion("suggestion")
                .size(10) //返回前10条数据
                .skipDuplicates(true)  //跳过重复的值
                .prefix(keyword) //查找的关键词前缀
        ));
        //发送请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析请求结果
        Suggest suggest = search.getSuggest();
        //根据名称获取补全结果
        CompletionSuggestion suggestion=suggest.getSuggestion("suggestion");
        List<String> list=new ArrayList<>();
        //遍历options获取结果
        for(CompletionSuggestion.Entry.Option option:suggestion.getOptions()){
            String s = option.getText().toString();
            list.add(s);
        }
        //关闭客户端
        restHighLevelClient.close();

        return R.success(list);
    }






}
