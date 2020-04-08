package com.yipingjian.dlmws.common.filter;

import com.alibaba.fastjson.JSON;
import com.yipingjian.dlmws.common.constant.HTTPResponseConstant;
import com.yipingjian.dlmws.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {

    @Value("${token}")
    private String TOKEN;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse rep = (HttpServletResponse) servletResponse;

        // 设置允许跨域的配置
        // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
        rep.setHeader("Access-Control-Allow-Origin", "*");
        // 允许的访问方法
        rep.setHeader("Access-Control-Allow-Methods", "POST, GET");
        // Access-Control-Max-Age 用于 CORS 相关配置的缓存
        rep.setHeader("Access-Control-Max-Age", "3600");
        rep.setHeader("Access-Control-Allow-Headers", "token,Origin, X-Requested-With, Content-Type, Accept");

        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("application/json; charset=utf-8");

        String token = req.getHeader("token");//header方式
        Response response = null;
        if (null == token || token.isEmpty()) {
            response = Response.unAuth(HTTPResponseConstant.NO_TOKEN);
            outPut(servletResponse, response);
            return;
        }

        if (!TOKEN.equals(token)) {
            response = Response.unAuth(HTTPResponseConstant.ERROR_TOKEN);
            outPut(servletResponse, response);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void outPut(ServletResponse response, Response responseInfo) {
        PrintWriter writer = null;
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
            writer = new PrintWriter(osw, true);
            String jsonStr = JSON.toJSONString(responseInfo);
            writer.write(jsonStr);
        } catch (Exception e) {
            log.error("response filter info error", e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                log.error("close OutputStreamWriter error", e);
            }
        }
    }

}
