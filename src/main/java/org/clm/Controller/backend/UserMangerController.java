package org.clm.Controller.backend;

import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Ccc
 * @date 2018/9/27 0027 下午 6:50
 */
@Controller
@RequestMapping("/manage/user")
public class UserMangerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpServletResponse responseCookie, HttpSession session){
        ServiceResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            if(response.getData().getRole()== Const.Role.ROLE_ADMIN){
                //把seesionId存入cookie中
                CookieUtil.writeLoginToken(responseCookie,session.getId());
                //将sessionId存入redis中，key:sessionId,value:登录用户信息:时间:30分钟
                RedisPoolUtil.setEx(session.getId(), JsonUtil.objToString(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return response;
            }
            return ServiceResponse.createByErrorMessage("不是管理员,登录失败");
        }
        return response;
    }

}
