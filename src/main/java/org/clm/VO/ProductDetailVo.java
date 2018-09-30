package org.clm.VO;

import org.clm.Pojo.Product;

/**
 * @author Ccc
 * @date 2018/9/30 0030 下午 4:28
 */
public class ProductDetailVo extends Product {
    private String imageHost;
    private Integer parentCategoryId;

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
