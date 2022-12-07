package searchService.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter=new MappingJackson2HttpMessageConverter();
        //设置消息转换器，底层使用jackson将java对象转换为json对象
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        //将我们的消息转换器追加到springMvc的消息转换器中，并设置其最高优先级
        converters.add(0,mappingJackson2HttpMessageConverter);
    }
}
