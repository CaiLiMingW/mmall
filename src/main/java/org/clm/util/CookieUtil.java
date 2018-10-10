package org.clm.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ccc
 * @date 2018/10/10 0010 上午 10:28
 */
public class CookieUtil {


    private final static  String COOKIE_DOMAIN = "";

    private final static  String COOKIE_NAME = "login_token";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    System.out.println("返回cookie-Name:"+cookie.getName()+":Value:"+cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 把sessionId(token)写入 cookie(名字为COOKIE_NAME)，设置在根目录下
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);

       /* cookie.setDomain(".admin.clm.com");*//**跨域共享session,*/
        /**设置在根目录,使用服务器所有应用都可获取该cookies*/
        cookie.setPath("/");
        /**不允许通过脚本访问cookies*/
        cookie.setHttpOnly(true);
        /**-1代表永久 调用方法会使cookie写入硬盘*/
        cookie.setMaxAge(60*60*24*365);

        System.out.println("写入cookie-Name:"+cookie.getName()+"Value:"+cookie.getValue());
        response.addCookie(cookie);
    }

    public static void
    delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie : cookies) {
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    cookie.setPath("/");
                    /**年龄为0 即删除*/
                    System.out.println("删除cookie-Name:"+cookie.getName()+":Value:"+cookie.getValue());
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
