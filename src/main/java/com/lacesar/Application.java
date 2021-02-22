package com.lacesar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Freeeeeedom
 * @date 11:57 2020/12/30
 */
@SuppressWarnings("all")
//@MapperScan("priv.freeeeeedom.**.mapper")
//@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
//@AutoConfigureDataRedis
//@EnableConfigurationProperties({RedisProperties.class})
//@ImportResource(locations = {"config/*.xml"})
@SpringBootApplication
public class Application {
    private static final Map<String, ApplicationContext> APPLICATIONS = new HashMap<>();
    private static final String APPLICATION = "application";

    public static ApplicationContext getApplication() {
        return APPLICATIONS.get(APPLICATION);
    }

    public static ApplicationContext start() {
        final ApplicationContext application = SpringApplication.run(Application.class);
        APPLICATIONS.put(APPLICATION, application);
        return application;
    }

    public static void main(String[] args) {
        start();
    }
}
