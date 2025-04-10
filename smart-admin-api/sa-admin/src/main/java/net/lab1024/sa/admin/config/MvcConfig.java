package net.lab1024.sa.admin.config;

import net.lab1024.sa.admin.interceptor.AdminInterceptor;
import net.lab1024.sa.base.config.SwaggerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * web相关配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private AdminInterceptor adminInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .excludePathPatterns(SwaggerConfig.SWAGGER_WHITELIST)
                .addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
