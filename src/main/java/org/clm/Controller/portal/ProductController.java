package org.clm.Controller.portal;

import org.clm.Pojo.User;
import org.clm.Service.IProductManageService;
import org.clm.Service.IUserService;
import org.clm.Service.Impl.IProductService;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ccc
 * @date 2018/10/10 0010 下午 2:15
 */
@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping("/detail.do")
    public ServiceResponse getProductDetail(HttpServletRequest request,@PathVariable("productId") Integer productId){
            return iProductService.getProductDetail(productId);
    }

    @RequestMapping("/list.do")
    public ServiceResponse getProductDetail(HttpServletRequest request,
                                            @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                            @RequestParam(value = "keyword",required = false)String keyword,
                                            @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                            @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                            @RequestParam(value = "orderBy",defaultValue = "")String orderBy){

        return iProductService.getProductList(categoryId,pageNum,pageSize,keyword,orderBy);
    }

}
