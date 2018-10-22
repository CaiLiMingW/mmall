package org.clm.Controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.CookieUtil;
import org.clm.util.RedisTemplateUtil;
import org.joda.time.YearMonth;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    public ServiceResponse<User> login(String username, String password, HttpServletResponse responseCookie, HttpSession session){
        ServiceResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            /**
             * 把sessionId写入cookie
             * 存入redis中 key= sessionId,Value = UserJsonString
             */
            User user = response.getData();
            CookieUtil.writeLoginToken(responseCookie,session.getId());
            redisTemplateUtil.setEx(Const.objType.SESSION,session.getId(),user,Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            //密码清空
            user.setPassword(StringUtils.EMPTY);
            user.setQuestion(StringUtils.EMPTY);
            user.setAnswer(StringUtils.EMPTY);
        }
        return response;
    }

    /**
     * 登出
     * @param
     * @return
     */
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    public ServiceResponse<User> outlogin(HttpServletResponse responseCookie,HttpServletRequest request){
            rabbitTemplate.convertAndSend(Const.Routingkey.USERUPDATE,request.getSession().getId());
            CookieUtil.delLoginToken(request,responseCookie);
            return ServiceResponse.createBySuccess();
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
     * @param
     * @return 返回会话信息
     */
    @RequestMapping(value ="/get_user_info.do" ,method =RequestMethod.POST )
    public ServiceResponse<User> getUserInfo(HttpServletRequest request){
        User user = (User) request.getAttribute("user");
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySucces(user);
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

    /**
     * 忘记密码问题找回
     * @param username 账号
     * @param question 问题
     * @param answer   密码
     * @return
     */
    @RequestMapping(value ="/forget_check_answer.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value ="/forget_reset_password.do" ,method =RequestMethod.POST )
    public ServiceResponse<String> forgetRestPassword(HttpServletRequest request,String username,String passwordNew,String forgetToken){
        String seesionId = CookieUtil.readLoginToken(request);
        return iUserService.forgetrestPassword(username,passwordNew,forgetToken,seesionId);
    }

    //todo
    @RequestMapping(value ="/reset_password.do" ,method =RequestMethod.POST )
    public ServiceResponse resetPassword(String passwordOld,String passwordNew,HttpServletRequest request){
        String seesionId = CookieUtil.readLoginToken(request);
        User user = (User)request.getAttribute("user");
        return iUserService.restPassword(passwordOld,passwordNew,user,seesionId);
    }

    @RequestMapping(value ="/update_information.do" ,method =RequestMethod.POST )
    public ServiceResponse updateInfomation(HttpServletRequest request,User updateuser){
        User user = (User)request.getAttribute("user");
        updateuser.setId(user.getId());
        updateuser.setUsername(user.getUsername());
        String sessionId = CookieUtil.readLoginToken(request);
        ServiceResponse<User> response = iUserService.updateInfomation(updateuser,sessionId);
        if (response.isSuccess()){
            request.setAttribute("newUser",response.getData());
        }
        return response;
    }

    @RequestMapping(value ="/get_information.do" ,method =RequestMethod.POST )
    public ServiceResponse<User> getInfomation(HttpServletRequest request){
        User user = (User)request.getAttribute("user");
        return ServiceResponse.createBySucces(user);
    }

}
