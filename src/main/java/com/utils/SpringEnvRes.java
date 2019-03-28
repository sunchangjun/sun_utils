package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/*设置环境*/
public class SpringEnvRes implements ServletContextListener {
    private Logger log = LoggerFactory.getLogger(SpringEnvRes.class);
    public static final String springProfilesActive = "spring.profiles.active";
    public static final String PROFILE_DEVELOP = "develop";// 开发环境
    public static final String PROFILE_PRODUCT = "product";// 生产环境
    public static String env;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext sc = sce.getServletContext();
            env = sc.getInitParameter(springProfilesActive);
            System.setProperty(SpringEnvRes.springProfilesActive, env);
            log.info("===========================> Starting Spring Web Environment:["+env+"]......");
        } catch (Exception e) {
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
