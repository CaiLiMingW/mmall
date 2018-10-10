package org.clm.Controller.portal;

import org.clm.Pojo.Shipping;
import org.clm.Pojo.User;
import org.clm.Service.IShippingService;
import org.clm.Service.IUserService;
import org.clm.common.ServiceResponse;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ccc
 * @date 2018/10/5 0005 上午 10:55
 */
@RestController
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping("/add.do")
    public ServiceResponse addShipping(HttpServletRequest request, Shipping shipping){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iShippingService.addShipping(response.getData(),shipping);
        }
        return response;
    }

    @RequestMapping("/del.do")
    public ServiceResponse delShipping(HttpServletRequest request, Integer shippingId){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iShippingService.deleteShipping(response.getData(),shippingId);
        }
        return response;
    }

    @RequestMapping("/update.do")
    public ServiceResponse updateShipping(HttpServletRequest request, Shipping shipping){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iShippingService.updateShipping(response.getData(),shipping);
        }
        return response;
    }

    @RequestMapping("/select.do")
    public ServiceResponse selectShipping(HttpServletRequest request, Integer shippingId){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iShippingService.selectShipping(response.getData(),shippingId);
        }
        return response;
    }

    @RequestMapping("/list.do")
    public ServiceResponse getShippingList(HttpServletRequest request,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        String sessionId = CookieUtil.readLoginToken(request);
        String userJsonStr = RedisPoolUtil.get(sessionId);
        User user = JsonUtil.StringToObj(userJsonStr, User.class);
        if(user == null){
            return ServiceResponse.createByErrorMessage("请登录之后查询");
        }
        return iShippingService.getShippingList(user,pageNum,pageSize);

    }
}
