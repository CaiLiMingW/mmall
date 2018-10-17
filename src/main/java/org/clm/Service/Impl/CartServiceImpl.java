package org.clm.Service.Impl;

import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.clm.Dao.CartMapper;
import org.clm.Dao.ProductMapper;
import org.clm.Pojo.Cart;
import org.clm.Pojo.User;
import org.clm.Service.ICartService;
import org.clm.VO.CartProductVo;
import org.clm.VO.CartVo;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.clm.util.BigDecimalUtil;
import org.clm.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ccc
 * @date 2018/10/4 0004 下午 3:10
 */
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServiceResponse getCartProductList(User user){
        CartVo cartVo = this.list(user);
        return ServiceResponse.createBySucces(cartVo);
    }

    @Override
    public ServiceResponse addCartProduct(User user,Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServiceResponse.createByCodeError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        /**检查购物车*/
        Cart cart = cartMapper.checkStockAndCar(user.getId(), productId);


        if( productMapper.selectByPrimaryKey(productId)==null){
            return ServiceResponse.createByErrorMessage("该商品不存在");
        }
        if(cart==null){
            Cart newcart = new Cart();
            newcart.setUserId(user.getId());
            newcart.setProductId(productId);
            newcart.setQuantity(count);
            cartMapper.insertSelective(newcart);
        }else{
            count = count + cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.getCartProductList(user);
    }

    @Override
    public ServiceResponse updateCart(User user, Integer productId, Integer count) {
        if(productId == null || count == null){
            return ServiceResponse.createByCodeError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.checkStockAndCar(user.getId(), productId);

        if(cart.getQuantity() - count>0){
            count = cart.getQuantity() - count;
            cart.setQuantity(count);
        }

        int updateRow = cartMapper.updateByPrimaryKeySelective(cart);
        if (updateRow==0){
            return ServiceResponse.createByErrorMessage("更新产品数量失败");
        }
        return this.getCartProductList(user);
    }

    @Override
    public ServiceResponse deleteCartProduct(User user, String ids) {
        List<String> productList = Splitter.on(",").splitToList(ids);
        if(CollectionUtils.isEmpty(productList)){
            return ServiceResponse.createByCodeError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByIds(user.getId(),productList);
        return this.getCartProductList(user);
    }



    @Override
    public ServiceResponse updateCheck(User user, Integer checked,Integer productId) {
        int i = cartMapper.updateCheck(user.getId(), checked, productId);
        if (i>0){
            return this.getCartProductList(user);
        }
       return ServiceResponse.createByErrorMessage("失败");
    }

    @Override
    public ServiceResponse selectCartProductCount(User user) {
        int reultCount = cartMapper.selectCartProductCount(user.getId());
        return ServiceResponse.createBySucces(reultCount);
    }


    private CartVo list(User user){

        List<CartProductVo> cartList = cartMapper.selectAll(user.getId());

        CartVo cartVo = new CartVo();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        for (CartProductVo cartProductVo : cartList) {
            /**设置单个产品总价*/
            cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getProductPrice().doubleValue(),cartProductVo.getQuantity()));
            /**库存校验*/
            cartProductVo.setLimitQuantity("LIMIT_NUM_SUCCESS");

            if(cartProductVo.getProductChecked() == 1){
                //如果已经勾选,增加到整个的购物车总价中
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
        }
        cartVo.setCartProductVoList(cartList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        /**cartVo.setAllChecked(this.getAllCheckedStatus(userId));*/
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

}
