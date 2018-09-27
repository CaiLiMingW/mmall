package org.clm.Controller;

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
    @RequestMapping(value = "/outlogin.do",method = RequestMethod.GET)
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
    @RequestMapping(value = "/register.do",method = RequestMethod.GET)
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
    @RequestMapping(value ="/check_valid.do" ,method =RequestMethod.GET )
    @ResponseBody
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

}
