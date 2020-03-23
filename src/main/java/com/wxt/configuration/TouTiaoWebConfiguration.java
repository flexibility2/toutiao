package com.wxt.configuration;

import com.wxt.interceptor.LoginRequiredInterceptor;
import com.wxt.interceptor.PassPortInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class TouTiaoWebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    PassPortInterceptor passPortInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passPortInterceptor);

        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");

        super.addInterceptors(registry);
    }
}
