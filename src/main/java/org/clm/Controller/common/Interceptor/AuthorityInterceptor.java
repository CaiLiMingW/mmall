package org.clm.Controller.common.Interceptor;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.User;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.ShardedPoolUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * @author Ccc
 * @date 2018/10/14 0014 下午 4:58
 */
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
        String sessionId = CookieUtil.readLoginToken(request);
        String requestURI = request.getRequestURI();
        User user = null;
        if (StringUtils.isBlank(sessionId)){
            this.forward(request,response);
            return true;
        }
        String userJsonStr = ShardedPoolUtil.get(sessionId);
        user = JsonUtil.StringToObj(userJsonStr, User.class);
        if (user==null){
            this.forward(request, response);
            return true;
        }
        // / c l m / m a n a g e / 5,11
        // / m a n a g e / p r o d uct 1-7
        String manage = requestURI.substring(5, 11);
        if (("manage").equals(manage)){
            if (user.getRole().intValue()== Const.Role.ROLE_ADMIN){
                return true;
            }
            request.setAttribute("user",user);
            request.getRequestDispatcher("/noAdminRole").forward(request,response);
            return true;
        }

        request.setAttribute("user",user);
        return true;


       /* //请求controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod)handle;
        //解析handlerMethod
        String name = handlerMethod.getMethod().getName();
        handlerMethod.getBean().getClass().getSimpleName();

        StringBuffer stringBuffer = new StringBuffer();
        Map<String,String[]> paramMap = httpServletRequest.getParameterMap();
        Map reusltMap = new HashMap();
        for (Map.Entry s : paramMap.entrySet()) {
            Map.Entry entry = (Map.Entry)s;
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;

            Object obj = entry.getValue();
            if (obj instanceof String[]){

            }
        }*/

    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("post");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("after");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        //判断请求的页面是否属于需要强制登录
        //注意:线上服务器路径需加上项目名前缀/clm
        if ("/clm/user/get_user_info.do".equals(requestURI)|| "/clm/cart/get_cart_product_count.do".equals(requestURI)){
            request.getRequestDispatcher("/unNeedLogin").forward(request,response);
        }

        request.getRequestDispatcher("/needLogin").forward(request,response);
    }


}
