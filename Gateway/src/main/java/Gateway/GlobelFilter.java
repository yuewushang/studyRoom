package Gateway;

import Gateway.Utills.JWTUtill;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


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
        //获取响应
        ServerHttpResponse response = exchange.getResponse();
        //设置401未登录状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //设置响应状态完成，不再执行后续操作
        return response.setComplete();
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
        list.add("http://localhost:10010/user/login");
        list.add("http://localhost:10010/user/register");
        for (String s:list){
            if(uriString.equals(s)){
                return true;
            }
        }
        return false;
    }
}
