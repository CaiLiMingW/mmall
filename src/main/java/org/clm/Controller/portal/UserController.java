package org.clm.Controller.portal;

import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.UserMapper;
import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author Ccc
 * @date 2018/9/27 0027 上午 8:30
 */
@Controller
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
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        ServiceResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 登出
     * @param session
     * @return
     */
    @RequestMapping(value = "/outlogin.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> outlogin(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user 从前端接收的user信息
     * @return
     */
    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody
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
    @ResponseBody
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取当前会话用户信息
     * @param session 当前会话
     * @return 返回会话信息
     */
    @RequestMapping(value ="/get_user_info.do" ,method =RequestMethod.POST )
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServiceResponse.createBySucces(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录,无法获取当前用户信息");
    }

    /**
     * 忘记密码获取提示问题
     * @param username 用户名
     * @return
     */
    @RequestMapping(value ="/forget_get_question.do" ,method =RequestMethod.POST )
    @ResponseBody
    public ServiceResponse<String> forgetGetQuession(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value ="/forget_check_answer.do" ,method =RequestMethod.POST )
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value ="/forget_reset_password.do" ,method =RequestMethod.POST )
    @ResponseBody
    public ServiceResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetrestPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value ="/rest_password.do" ,method =RequestMethod.POST )
    @ResponseBody
    public ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return iUserService.restPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value ="/update_Information.do" ,method =RequestMethod.POST )
    @ResponseBody
    public ServiceResponse<User> updateInfomation(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser ==null){
            ServiceResponse.createByErrorMessage("用户未登录");
        }
        //设置id为当前会话用户id，防止被改变
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServiceResponse<User> response = iUserService.updateInfomation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
}
