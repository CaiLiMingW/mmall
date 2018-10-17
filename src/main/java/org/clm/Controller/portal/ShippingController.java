package org.clm.Controller.portal;

import org.clm.Pojo.Shipping;
import org.clm.Pojo.User;
import org.clm.Service.IShippingService;
import org.clm.Service.IUserService;
import org.clm.common.ServiceResponse;
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

        User user = (User)request.getAttribute("user");
        return iShippingService.addShipping(user,shipping);
    }

    @RequestMapping("/del.do")
    public ServiceResponse delShipping(HttpServletRequest request, Integer shippingId){
        User user = (User)request.getAttribute("user");
            return iShippingService.deleteShipping(user,shippingId);
    }

    @RequestMapping("/update.do")
    public ServiceResponse updateShipping(HttpServletRequest request, Shipping shipping){

        User user = (User)request.getAttribute("user");
        return iShippingService.updateShipping(user,shipping);
    }

    @RequestMapping("/select.do")
    public ServiceResponse selectShipping(HttpServletRequest request, Integer shippingId){

        User user = (User)request.getAttribute("user");
        return iShippingService.selectShipping(user,shippingId);

    }

    @RequestMapping("/list.do")
    public ServiceResponse getShippingList(HttpServletRequest request,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user = (User)request.getAttribute("user");
        return iShippingService.getShippingList(user,pageNum,pageSize);

    }
}
