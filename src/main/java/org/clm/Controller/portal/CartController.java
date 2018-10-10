package org.clm.Controller.portal;

import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.Service.ICartService;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Ccc
 * @date 2018/10/4 0004 下午 2:55
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICartService iCartService;
    


    @RequestMapping("/add.do")
    public ServiceResponse add(HttpServletRequest request, Integer productId, Integer count){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iCartService.addCartProduct(response.getData(),productId,count);
        }
        return response;
    }

    @RequestMapping("/update.do")
    public ServiceResponse updateCart(HttpServletRequest request,Integer productId,Integer count){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iCartService.updateCart(response.getData(),productId,count);
        }
        return response;
    }

    @RequestMapping("/delete_product.do")
    public ServiceResponse deleteProductCart(HttpServletRequest request,String productIds){
        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if(response.isSuccess()){
            return iCartService.deleteCartProduct(response.getData(),productIds);
        }
        return response;
    }

    @RequestMapping("/list.do")
    public ServiceResponse getCartList(HttpServletRequest request){

        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
           return iCartService.getCartProductList(response.getData());
        }
        return response;
    }

    @RequestMapping("/select_all.do")
    public ServiceResponse chooseAll(HttpServletRequest request){

        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iCartService.updateCheck(response.getData(), Const.Cart.CHECKED,null);
        }
        return response;
    }

    @RequestMapping("/un_select_all.do")
    public ServiceResponse unchooseAll(HttpServletRequest request){

        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iCartService.updateCheck(response.getData(), Const.Cart.UN_CHECKED,null);
        }
        return response;
    }

    @RequestMapping("/un_select.do")
    public ServiceResponse unChooseOne(HttpServletRequest request,Integer productId){

        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iCartService.updateCheck(response.getData(), Const.Cart.UN_CHECKED,productId);
        }
        return response;
    }

    @RequestMapping("/select.do")
    public ServiceResponse ChooseOne(HttpServletRequest request,Integer productId){

        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iCartService.updateCheck(response.getData(), Const.Cart.CHECKED,productId);
        }
        return response;
    }

    @RequestMapping("/get_cart_product_count.do")
    public ServiceResponse getCartProductcount(HttpServletRequest request){

        ServiceResponse<User> response = iUserService.checkUserLoginCookie(request);
        if (response.isSuccess()){
            return iCartService.selectCartProductCount(response.getData());
        }
        return response;
    }
}
