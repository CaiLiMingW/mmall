package org.clm.Controller.portal;

import org.clm.common.Const;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ccc
 * @date 2018/10/14 0014 上午 8:06
 */
@RestController
public class refreshContreller {

    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("/needLogin")
    public ServiceResponse refreshNeedLogin(){
        return ServiceResponse.createByCodeError(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需强制登录");
    }

    @RequestMapping("/unNeedLogin")
    public ServiceResponse refreshUnNeedLogin(){
        return ServiceResponse.createByErrorMessage("用户未登录,无法获取用户信息");
    }

    @RequestMapping("/noAdminRole")
    public ServiceResponse noAdminRole(){

        return ServiceResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("del")
    public void de(){
        redisTemplate.delete("6379");
    }

}
