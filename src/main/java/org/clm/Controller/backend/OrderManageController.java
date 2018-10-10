package org.clm.Controller.backend;

import org.clm.Pojo.User;
import org.clm.Service.IOrderService;
import org.clm.Service.IUserService;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author Ccc
 * @date 2018/10/8 0008 上午 10:40
 */
@RequestMapping("/manage/order")
@RestController
public class OrderManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("/list.do")
    public ServiceResponse getOrderList(HttpSession session,
                                        @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            return iOrderService.getOrderListByUserId(null,pageNum,pageSize);
        }
        return response;
    }

    @RequestMapping("/detail.do")
    public ServiceResponse getOrderList(HttpSession session,Long orderNo){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            return iOrderService.getOrderDeatilByOrderNo(null,orderNo);
        }
        return response;
    }
    @RequestMapping("/search.do")
    public ServiceResponse getOrderList(HttpSession session,Long orderNo,
                                        @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            return iOrderService.searchByOrderNoKey(pageNum,pageSize,orderNo);
        }
        return response;
    }

    @RequestMapping("/send_goods.do")
    public ServiceResponse sendgoods(HttpSession session,Long orderNo){
        ServiceResponse response = iUserService.checkAdminRole(session);
        if (response.isSuccess()){
            return iOrderService.sendgoods(orderNo);
        }
        return response;
    }
}
