package org.clm.Controller.common;

import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.User;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.ShardedPoolUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ccc
 * @date 2018/10/13 0013 下午 10:39
 */
@WebFilter(filterName = "userLoginFilter",urlPatterns = "*.do")
public class UserLoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    // /product/list.do
    // /user/get_user_info.do
    // /user/get_user_info.do
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        /**不需要登录权限的请求,放行，进入下一个逻辑*/
        if("/user/login.do".equals(requestURI)||"/product/list.do".equals(requestURI)){
            filterChain.doFilter(servletRequest,servletResponse);
        }


        String sessionId = CookieUtil.readLoginToken(request);
        if(StringUtils.isBlank(sessionId)){
            /**需要登录权限，且不需强制登录的请求,*/
            if ("/user/get_user_info.do".equals(requestURI)|| "/cart/get_cart_product_count.do".equals(requestURI)){
                response.setHeader("refresh","2;/unNeedLogin");
                return;
            }

            response.setHeader("refresh","2;/needLogin");
            return;
        }

        String userJsonStr = ShardedPoolUtil.get(sessionId);
        User user = JsonUtil.StringToObj(userJsonStr, User.class);
        if (user==null){
            if ("/user/get_user_info.do".equals(requestURI)|| "/cart/get_cart_product_count.do".equals(requestURI)){
                response.setHeader("refresh","2;/unNeedLogin");
                return;
            }

            response.setHeader("refresh","2;/needLogin");
            return;
        }

        //确定用户已登录,把用户信息放到request，并进入下一个Filter.
        servletRequest.setAttribute("user",user);
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
    private void refresh(HttpServletRequest request,HttpServletResponse response){
        String requestURI = request.getRequestURI();

        //判断请求的页面是否属于需要强制登录
        if ("/user/get_user_info.do".equals(requestURI)|| "/cart/get_cart_product_count.do".equals(requestURI)){
            response.setHeader("refresh","2;/unNeedLogin");
            return;
        }

        response.setHeader("refresh","2;/needLogin");
        return;
    }
}
