package Gateway;

import Gateway.Utills.JWTUtill;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//全局过滤器,注册成bean，使过滤器生效
@Component
//全局拦截器的执行顺序，数值越小，越先执行
@Order(-1)
public class GlobelFilter implements GlobalFilter {

    /**
     * 过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求头中的认证信息
        ServerHttpRequest request = exchange.getRequest();
        //如果是登录和注册的请求，直接放行
        if (allowedAssess(request)){
            return chain.filter(exchange);
        }
        //获取请求头参数哈希表
        HttpHeaders headers = request.getHeaders();
        String authentication = headers.getFirst("authentication");
        //检验token是否有效
        String userId = JWTUtill.checkToken(authentication);
        //如果有效，添加请求头后放行
        if(userId!=null){
            //追加请求头信息到request中
            ServerHttpRequest userId1 = request.mutate().header("userId", userId).build();
            //更新exchange上下文的request
            ServerWebExchange build = exchange.mutate().request(userId1).build();
            //放行
            return chain.filter(build);
        }
        //token无效则截止
        //返回错误信息
        return errorInfo(exchange,"认证未通过，请重新登录",401);
    }

    /**
     * 判断是否是登录，注册请求
     * @param request
     * @return
     */
    public boolean allowedAssess(ServerHttpRequest request){
        //获取uri
        URI uri = request.getURI();
        String uriString=uri.toString();
        List<String> list=new ArrayList<>();
        list.add("http://175.178.85.36:10010/user/login");
        list.add("http://175.178.85.36:10010/user/register");
        list.add("http://localhost:10010/user/login");
        list.add("http://localhost:10010/user/register");
        list.add("http://175.178.85.36:8083/liveRoom/save");
        list.add("http://localhost:8083/liveRoom/save");
        for (String s:list){
            if(uriString.equals(s)){
                return true;
            }
            if (uriString.substring(0,uriString.lastIndexOf('/')).equals("http://localhost:10010/storage/image")
            ||uriString.substring(0,uriString.lastIndexOf('/')).equals("http://175.178.85.36:10010/storage/image")){

                return true;
            }
        }
        System.out.println("不允许访问"+uri);
        return false;
    }


    /**
     * 返回错误信息
     * @param exchange
     * @param message
     * @param status
     * @return
     */
    public static Mono<Void> errorInfo(ServerWebExchange exchange,String message,Integer status){
        Map<String,Object> map=new HashMap<>();
        map.put("code",status);
        map.put("erroMessage",message);
        return Mono.defer(()->{
            byte[]bytes = new byte[0];
            try {
                //将要返回的信息设置为字节数据
                bytes=new ObjectMapper().writeValueAsBytes(map);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            ServerHttpResponse response = exchange.getResponse();
            //添加返回头信息
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
            //将返回的数据放到response中
            DataBuffer wrap = response.bufferFactory().wrap(bytes);
            return response.writeWith(Flux.just(wrap));
        });
    }

}
