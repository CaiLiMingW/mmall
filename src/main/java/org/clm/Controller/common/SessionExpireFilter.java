package org.clm.Controller.common;

import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.User;
import org.clm.common.Const;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.ShardedPoolUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Ccc
 * @date 2018/10/12 0012 下午 5:36
 */

@WebFilter(filterName = "sessionExpireFilter",urlPatterns = "*.do")
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //捕获抛出异常，并把doFilter放在finally,防止出现bug 进不到后续逻辑
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String sessionId = CookieUtil.readLoginToken(request);
            if (StringUtils.isNotBlank(sessionId)){
                String userJsonStr = ShardedPoolUtil.get(sessionId);
                User user = JsonUtil.StringToObj(userJsonStr, User.class);
                if (user!=null){
                    ShardedPoolUtil.expire(sessionId, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //放行,将请求转发给过滤器链上的下一个Filter，若无下个Filter，则进入Servlet
            filterChain.doFilter(servletRequest,servletResponse);
        }
        /*System.out.println("doFilter后:服务器返回时执行");*/


    }

    @Override
    public void destroy() {

    }
}
