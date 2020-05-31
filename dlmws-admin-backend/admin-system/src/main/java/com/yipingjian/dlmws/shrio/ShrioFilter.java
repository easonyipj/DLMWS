package com.yipingjian.dlmws.shrio;

import com.google.common.collect.Lists;
import com.yipingjian.dlmws.entity.JWTToken;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class ShrioFilter extends BasicHttpAuthenticationFilter {

    public static final List<String> allowUrls = Lists.newArrayList("/sys/login", "/sys/register");

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        HttpServletRequest req = (HttpServletRequest) request;
//        if(req.getRequestURI().contains("/ws")) {
//            return true;
//        }
//        return allowUrls.contains(req.getRequestURI());
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        AuthenticationToken token = createToken(servletRequest, servletResponse);
        if(token == null) {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.getWriter().write("{\"code\":" + 401 + ", \"msg\":\"" + "invalid token" + "\"}");
            return false;
        }
        this.getSubject(servletRequest, servletResponse).login(token);
        return true;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        return new JWTToken(token);
    }

    /**
      * 支持跨域
      *
      * @param request
      * @param response
      * @return
      * @throws Exception
      */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept");

        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.SC_OK);
            return false;
        }
        return super.preHandle(httpServletRequest, httpServletResponse);

    }


}
