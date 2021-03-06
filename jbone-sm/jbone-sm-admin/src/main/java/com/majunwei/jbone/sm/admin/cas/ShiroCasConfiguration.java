package com.majunwei.jbone.sm.admin.cas;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import com.majunwei.jbone.configuration.JboneConfiguration;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Shiro集成Cas配置
 *
 */
@Configuration
public class ShiroCasConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ShiroCasConfiguration.class);

    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    /**
     * 注册DelegatingFilterProxy（Shiro）
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(JboneCasRealm jboneCasRealm) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(jboneCasRealm);
        //用户授权/认证信息Cache, 采用EhCache 缓存
        dwsm.setCacheManager(getEhCacheManager());
        dwsm.setSubjectFactory(new CasSubjectFactory());
        return dwsm;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }

    /**
     * 加载shiroFilter权限控制规则
     */
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean,JboneConfiguration jboneConfiguration){
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String,String>();

        filterChainDefinitionMap.put(jboneConfiguration.getCas().getCasFilterUrlPattern(), "casFilter");// shiro集成cas后，首先添加该规则

        //添加jbone.cas的配置规则
        if(jboneConfiguration.getCas().getFilterChainDefinition() != null){
            filterChainDefinitionMap.putAll(jboneConfiguration.getCas().getFilterChainDefinition());
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    /**
     * CAS过滤器
     */
    @Bean(name = "casFilter")
    public CasFilter getCasFilter(JboneConfiguration jboneConfiguration) {
        CasFilter casFilter = new CasFilter();
        casFilter.setName("casFilter");
        casFilter.setEnabled(true);
        //失败后跳转到CAS登录页面
        casFilter.setFailureUrl(jboneConfiguration.getCas().getEncodedLoginUrl()); // 我们选择认证失败后再打开登录页面
        return casFilter;
    }

    /**
     * ShiroFilter
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager, CasFilter casFilter,JboneConfiguration jboneConfiguration) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // SecurityManager，Shiro安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // Shiro登录页面，这里默认为CAS的登录页面：cas.majunwei.com/login?service=serviceurl
        shiroFilterFactoryBean.setLoginUrl(jboneConfiguration.getCas().getEncodedLoginUrl());

        shiroFilterFactoryBean.setSuccessUrl(jboneConfiguration.getCas().getSuccessUrl());
        shiroFilterFactoryBean.setUnauthorizedUrl(jboneConfiguration.getCas().getUnauthorizedUrl());
        // 添加casFilter到shiroFilter中
        Map<String, Filter> filters = new HashMap<>();
        filters.put("casFilter", casFilter);
        shiroFilterFactoryBean.setFilters(filters);

        loadShiroFilterChain(shiroFilterFactoryBean,jboneConfiguration);
        return shiroFilterFactoryBean;
    }

}