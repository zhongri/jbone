package com.majunwei.jbone.configuration.sys;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Map;

/**
 * CAS属性配置
 */
public class CasProperties implements Serializable {
    /**
     * CAS服务端URL
     */
    private String casServerUrl = "https://cas.majunwei.com:8443/cas";
    /**
     * 登录URL
     */
    private String loginUrl = casServerUrl + "/login";;
    /**
     * 推出登录URL
     */
    private String logoutUrl = casServerUrl + "/logout";
    /**
     * 当前服务URL
     */
    private String currentServerUrlPrefix = "";
    /**
     * CASFilter
     */
    private String casFilterUrlPattern = "/shiro-cas";

    /**
     * 成功后跳转的默认路径
     */
    private String successUrl = "/";

    /**
     * 没有权限的路径
     */
    private String unauthorizedUrl = "/403";

    /**
     * 权限过滤规则，限制需要CAS过滤的请求路径
     */
    private Map<String,String> filterChainDefinition;

    public String getCasServerUrl() {
        return casServerUrl;
    }

    public void setCasServerUrl(String casServerUrl) {
        this.casServerUrl = casServerUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getCurrentServerUrlPrefix() {
        return currentServerUrlPrefix;
    }

    public void setCurrentServerUrlPrefix(String currentServerUrlPrefix) {
        this.currentServerUrlPrefix = currentServerUrlPrefix;
    }

    public String getCasFilterUrlPattern() {
        return casFilterUrlPattern;
    }

    public void setCasFilterUrlPattern(String casFilterUrlPattern) {
        this.casFilterUrlPattern = casFilterUrlPattern;
    }

    public Map<String, String> getFilterChainDefinition() {
        return filterChainDefinition;
    }

    public void setFilterChainDefinition(Map<String, String> filterChainDefinition) {
        this.filterChainDefinition = filterChainDefinition;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public String getEncodedLoginUrl(){
        try {
            return loginUrl + "?service=" + URLEncoder.encode(currentServerUrlPrefix + casFilterUrlPattern, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
