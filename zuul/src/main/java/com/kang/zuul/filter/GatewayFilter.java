package com.kang.zuul.filter;

import com.kang.mapper.IpBlackMapper;
import com.kang.mapper.entity.IpBlack;
import com.kang.util.SignUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * s使用网关拦截所有请求
 */


@Component
@Slf4j
public class GatewayFilter extends ZuulFilter {
    @Autowired
    private IpBlackMapper ipBlackMapper;

    //请求之前拦截
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    //处理业务能力
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String ipAddr = getIpAddr(request);
        log.info("网关路过的ip:" + ipAddr);
        IpBlack ipBlack = ipBlackMapper.findByIp(ipAddr, 1);
        if (ipBlack != null) {
            resultError(requestContext, "ip:" + ipAddr + " Insufficient acess rights");
            log.debug("ip:" + ipAddr + " Insufficient acess rights");

        }
        //验证签名拦截
        Map<String, String> verifyMap = SignUtil.toVerifyMap(request.getParameterMap(), false);
        if (!SignUtil.verify(verifyMap)) {
            resultError(requestContext, "ip:" + ipAddr + " sign fail");
            log.debug("ip:" + ipAddr + " sign fail");
        }
        //xxs,sql注入拦截
//        HttpServletResponse response=requestContext.getResponse();
        return null;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
        }

//        //使用代理，则获取第一个IP地址
//        if(StringUtils.isEmpty(ip) && ip.length() > 15) {
//          if(ip.indexOf(",") > 0) {
//              ip = ip.substring(0, ip.indexOf(","));
//          }
//      }

        return ip;
    }

    private void resultError(RequestContext ctx, String errorMsg) {
        ctx.setResponseStatusCode(401);
        ctx.setSendZuulResponse(false);
        ctx.setResponseBody(errorMsg);
    }
}
