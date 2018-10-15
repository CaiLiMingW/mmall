/*
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

*/
/** 登录认证自定义F
 * @author Ccc
 * @date 2018/10/13 0013 下午 10:39
 *//*

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
        String requestURI = request.getRequestURI();
        User user = null;
        if(!("/user/login.do".equals(requestURI)||"/product/list.do".equals(requestURI)||"/manage/user/login.do".equals(requestURI))){
            String sessionId = CookieUtil.readLoginToken(request);
            if(StringUtils.isBlank(sessionId)){
                this.refresh(servletRequest,servletResponse);
                return;
            }

            String userJsonStr = ShardedPoolUtil.get(sessionId);
            user = JsonUtil.StringToObj(userJsonStr, User.class);
            if (user==null){
                this.refresh(servletRequest,servletResponse);
                return;
            }
        }
        //确定用户已登录,把用户信息放到request，并进入下一个Filter.
        servletRequest.setAttribute("user",user);
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
    private void refresh(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();

        //判断请求的页面是否属于需要强制登录
        //注意:线上服务器路径需加上项目名前缀/clm
        if ("/user/get_user_info.do".equals(requestURI)|| "/cart/get_cart_product_count.do".equals(requestURI)){
            servletRequest.getRequestDispatcher("/unNeedLogin").forward(servletRequest,servletResponse);
            return;
        }

        servletRequest.getRequestDispatcher("/needLogin").forward(servletRequest,servletResponse);
        return;
    }
}
*/
