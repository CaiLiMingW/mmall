package org.clm.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.ProductMapper;
import org.clm.Pojo.Product;
import org.clm.Service.IProductService;
import org.clm.VO.ProductListVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.RedisTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ccc
 * @date 2018/10/10 0010 下午 2:34
 */
@Service
public class IProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Override
    public ServiceResponse getProductDetail(Integer productId) {
        if (productId==null){
            return ServiceResponse.createByErrorMessage("产品ID错误");
        }

        Product product = redisTemplateUtil.get(Const.objType.PRODUCT,""+productId);

        List s = new ArrayList();
        s.add("1");
        s.add("2");
        redisTemplateUtil.lsetx("1","2",s);
        //从缓存获取product

        //若为Null，从数据库取最新数据,并更新到缓存中
        if (product==null){
            product = productMapper.selectByPrimaryKey(productId);
            redisTemplateUtil.set(Const.objType.PRODUCT,""+productId,product);
            /*RedisUtil.set(Const.objType.PRODUCT,""+productId,product);*/
        }

        //如果product还为NULL，则可能数据库不存在该商品
        if (product==null){
            return ServiceResponse.createByErrorMessage("该商品已下架或删除");
        }
        return ServiceResponse.createBySucces(product);
    }

    @Override
    public ServiceResponse getProductList( Integer categoryId, Integer pageNum, Integer pageSize, String keyword, String orderBy) {
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = null;
        if (StringUtils.isNotBlank(orderBy)){
            orderBy = StringUtils.equals("price_desc",orderBy)?Const.OrderBy.PRICE_DESC:Const.OrderBy.PRICE_ASC;
        }
        if (categoryId==null){

        }
        pageInfo = redisTemplateUtil.get(Const.objType.PRODOCTLISTVO,
                "" + (categoryId==null?"search":categoryId) + pageNum + pageSize + keyword+orderBy);
      /*  List<ProductListVo> productListVos = redisTemplateUtil.lget(Const.objType.PRODOCTLISTVO,
                                                     ""+categoryId+pageNum+pageSize+keyword+orderBy);*/
        /*productListVos = RedisUtil.getList(Const.objType.PRODOCTLISTVO,
                "" + categoryId + pageNum + pageSize + keyword+orderBy,
                new TypeReference<List<ProductListVo>>() {
        });*/

        if(pageInfo==null){

            List<ProductListVo> productListVos = productMapper.selectProductBycategoryIdAndKeywordOrdeBy(categoryId, keyword, orderBy);
            pageInfo = new PageInfo(productListVos);
            redisTemplateUtil.set(Const.objType.PRODOCTLISTVO,""+(categoryId==null?"search":categoryId)+pageNum+pageSize+keyword+orderBy,pageInfo);
            /*redisTemplateUtil.lset(Const.objType.PRODOCTLISTVO,
                    ""+categoryId+pageNum+pageSize+keyword+orderBy,productListVos);*/
        }

        if (pageInfo==null){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySucces(pageInfo);
    }
}
