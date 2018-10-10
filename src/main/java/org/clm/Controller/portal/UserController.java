package org.clm.Controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.clm.Dao.UserMapper;
import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.RedisPoolUtil;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Ccc
 * @date 2018/9/27 0027 上午 8:30
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    public ServiceResponse<User> login(String username, String password, HttpServletResponse responseCookie, HttpServletRequest request, HttpSession session){
        ServiceResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            /**
             * 把sessionId写入cookie
             * 存入redis中 key= sessionId,Value = UserJsonString
             */
            CookieUtil.writeLoginToken(responseCookie,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.objToString(response.getData()),
                                Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 登出
     * @param session
     * @return
     */
    @RequestMapping(value = "/outlogin.do",method = RequestMethod.POST)
    public ServiceResponse<User> outlogin(HttpServletResponse responseCookie,HttpServletRequest request){
        CookieUtil.delLoginToken(request,responseCookie);
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByErrorMessage("还未登录，何来退出");
    }

    /**
     * 用户注册
     * @param user 从前端接收的user信息
     * @return
     */
    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 数据验证
     * @param str 验证数值
     * @param type 验证类型
     * @return 返回验证结果  status:1
     *                     message:
     */
    @RequestMapping(value ="/check_valid.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取当前会话用户信息
     * @param session 当前会话
     * @return 返回会话信息
     */
    @RequestMapping(value ="/get_user_info.do" ,method =RequestMethod.POST )
    public ServiceResponse<User> getUserInfo(HttpServletRequest request){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);

        return response;
    }

    /**
     * 忘记密码获取提示问题
     * @param username 用户名
     * @return
     */
    @RequestMapping(value ="/forget_get_question.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> forgetGetQuession(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value ="/forget_check_answer.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value ="/forget_reset_password.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetrestPassword(username,passwordNew,forgetToken);
    }
    //todo
    @RequestMapping(value ="/rest_password.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return iUserService.restPassword(passwordOld,passwordNew,user);
    }
    @RequestMapping(value ="/update_Information.do" ,method =RequestMethod.POST )
    public ServiceResponse<User> updateInfomation(HttpServletRequest request,User user){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iUserService.updateInfomation(response.getData());
        }
        return response;
    }

    @RequestMapping(value ="/get_Information.do" ,method =RequestMethod.POST )
    public ServiceResponse<User> getInfomation(HttpServletRequest request){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iUserService.getInfomation(response.getData().getId());
        }
       return response;
    }
}
