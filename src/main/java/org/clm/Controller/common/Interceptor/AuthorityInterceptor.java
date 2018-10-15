package org.clm.Controller.common.Interceptor;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.User;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.ShardedPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private static Logger log = LoggerFactory.getLogger(AuthorityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod)handle;
        String methodName = handlerMethod.getMethod().getName();
        String classname = handlerMethod.getBean().getClass().getSimpleName();
        log.info("\n=========拦截器拦截到bean===========\n{}\n=========拦截方法===========\n{}\n",classname,methodName);
        //MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        //MethodParameter returnType = handlerMethod.getReturnType();

        String sessionId = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isBlank(sessionId)){
            this.forward(request,response,methodName);
            return true;
        }
        String userJsonStr = ShardedPoolUtil.get(sessionId);
        user = JsonUtil.StringToObj(userJsonStr, User.class);
        if (user==null){
            this.forward(request, response,methodName);
            return true;
        }
        // manage*
        //需要管理员权限的业务:方法前缀为manage
        String manage = methodName.substring(0, 6);
        if (StringUtils.equals(manage,"manage")){
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
        log.info("拦截器");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    private void forward(HttpServletRequest request, HttpServletResponse response,String methodName) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        //判断请求的页面是否属于需要强制登录
        //注意:线上服务器路径需加上项目名前缀/clm

        if ( StringUtils.equals(methodName,"getUserInfo")||StringUtils.equals(methodName,"getCartProductcount")){
            request.getRequestDispatcher("/unNeedLogin").forward(request,response);
        }
        /*if ("/clm/user/get_user_info.do".equals(requestURI)|| "/clm/cart/get_cart_product_count.do".equals(requestURI)){
            request.getRequestDispatcher("/unNeedLogin").forward(request,response);
        }*/

        request.getRequestDispatcher("/needLogin").forward(request,response);
    }


}
