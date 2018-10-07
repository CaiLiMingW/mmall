package org.clm.VO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ccc
 * @date 2018/10/7 0007 下午 5:24
 */
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;

    private  String imageHost;

    private BigDecimal productTotalPrice;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }
}
