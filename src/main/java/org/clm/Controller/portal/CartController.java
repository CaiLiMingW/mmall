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
        User user = (User)request.getAttribute("user");
        return iCartService.addCartProduct(user,productId,count);

    }

    @RequestMapping("/update.do")
    public ServiceResponse updateCart(HttpServletRequest request,Integer productId,Integer count){
        User user = (User)request.getAttribute("user");
        return iCartService.updateCart(user,productId,count);
    }

    @RequestMapping("/delete_product.do")
    public ServiceResponse deleteProductCart(HttpServletRequest request,String productIds){
        User user = (User)request.getAttribute("user");
            return iCartService.deleteCartProduct(user,productIds);
    }

    @RequestMapping("/list.do")
    public ServiceResponse getCartList(HttpServletRequest request){

        User user = (User)request.getAttribute("user");
        return iCartService.getCartProductList(user);
    }

    @RequestMapping("/select_all.do")
    public ServiceResponse chooseAll(HttpServletRequest request){

        User user = (User)request.getAttribute("user");
        return iCartService.updateCheck(user, Const.Cart.CHECKED,null);
    }

    @RequestMapping("/un_select_all.do")
    public ServiceResponse unchooseAll(HttpServletRequest request){

        User user = (User)request.getAttribute("user");
        return iCartService.updateCheck(user, Const.Cart.UN_CHECKED,null);

    }

    @RequestMapping("/un_select.do")
    public ServiceResponse unChooseOne(HttpServletRequest request,Integer productId){

        User user = (User)request.getAttribute("user");
            return iCartService.updateCheck(user, Const.Cart.UN_CHECKED,productId);
    }

    @RequestMapping("/select.do")
    public ServiceResponse ChooseOne(HttpServletRequest request,Integer productId){

        User user = (User)request.getAttribute("user");
            return iCartService.updateCheck(user, Const.Cart.CHECKED,productId);
    }

    @RequestMapping("/get_cart_product_count.do")
    public ServiceResponse getCartProductcount(HttpServletRequest request){

        User user = (User)request.getAttribute("user");
        return iCartService.selectCartProductCount(user);

    }
}
