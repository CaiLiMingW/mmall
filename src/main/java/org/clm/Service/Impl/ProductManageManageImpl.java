package org.clm.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.CategoryMapper;
import org.clm.Dao.ProductMapper;
import org.clm.Pojo.Category;
import org.clm.Pojo.Product;
import org.clm.Service.IFileService;
import org.clm.Service.IProductManageService;
import org.clm.VO.ProductDetailVo;
import org.clm.VO.ProductListVo;
import org.clm.common.Const;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.clm.util.JsonUtil;
import org.clm.util.PropertiesUtil;
import org.clm.util.RedisTemplateUtil;
import org.clm.util.bak.RedisUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ccc
 * @date 2018/9/29 0029 下午 7:05
 */
@Service
public class ProductManageManageImpl implements IProductManageService {

    @Autowired
    private IFileService iFileService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplateUtil redisTemplateUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public ServiceResponse saveOrUpdateProduct(Product product) {
        Product pd = productMapper.selectByPrimaryKey(product.getId());
        if(product.getId()!=null){
            //所有图片信息清空时,主页面图片也清空
            if (StringUtils.isBlank(product.getSubImages())){
                product.setMainImage("");
            }
            int i = productMapper.updateByPrimaryKeySelective(product);
            if(i>0){
//                rabbitTemplate.convertAndSend(Const.Routingkey.PRODUCTUPDATE, JsonUtil.objToString(product));
                //删除产品详情在redis中的缓存
                redisTemplateUtil.del(Const.objType.PRODUCT, "" + product.getId());
//
//                //如果修改了价格
//                //删除商品列表详情缓存
               redisTemplateUtil.delByKey(Const.objType.PRODOCTLISTVO, "search");
               redisTemplateUtil.delByKey(Const.objType.PRODOCTLISTVO,""+pd.getCategoryId());


                return ServiceResponse.createBySucces("更新产品成功");
            }
            return ServiceResponse.createByError("更新产品失败");
        }

        //设置第一张图主页
        if (product.getMainImage()==null){
            product.setMainImage(product.getSubImages());
        }
        int resultCount = productMapper.insertSelective(product);
        if(resultCount >0){
            return ServiceResponse.createBySucces("新增产品成功");
        }
        return ServiceResponse.createByError("新增产品失败");
    }

    @Override
    public ServiceResponse<PageInfo> selectAllProduct(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> list = productMapper.selectAllProduct();
        if (list==null){
            return ServiceResponse.createByErrorMessage("出现错误");
        }
        List<ProductListVo> productListVolist = new ArrayList<>();
        for (Product product : list) {
            ProductListVo productListVo = new ProductListVo();
            BeanUtils.copyProperties(product,productListVo);
            productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://120.78.128.136/"));
            productListVolist.add(productListVo);
        }


        PageInfo pageInfo = new PageInfo(list);
        return ServiceResponse.createBySucces(pageInfo);
    }


    @Override
    public ServiceResponse setSaleStatus(Integer productId, Integer status) {
        if (productId==null||status==null){
            return ServiceResponse.createByCodeError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServiceResponse.createByErrorMessage("产品不存在");
        }
        product.setStatus(status);
        int i = productMapper.updateByPrimaryKeySelective(product);
        if(i>0){
            return ServiceResponse.createBySuccessMessage("修改产品状态成功");
        }
        return ServiceResponse.createBySuccessMessage("修改产品状态失败");
    }

    @Override
    public ServiceResponse<ProductDetailVo> geDetail(Integer productId) {
        if(productId==null){
            return ServiceResponse.createByCodeError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServiceResponse.createByErrorMessage("产品被删除或已下架");
        }
        ProductDetailVo productDetailVo = copy(product);

         /* productDetailVo.setCreateTime(DataTimeUtil.dateToStr(product.getCreateTime()));
          productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));*/

        return ServiceResponse.createBySucces(productDetailVo);
    }

    @Override
    public ServiceResponse<PageInfo> searchProductByIdAndName(String productName, Integer productId, int pageNum, int pageSize) {
       /**#｛%productName%｝防注入*/
       if(!StringUtils.isBlank(productName)){
           productName=new StringBuffer().append("%").append(productName).append("%").toString();
       }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> list = productMapper.selectByIdAndName(productName,productId);
        List<ProductListVo> productListVolist = new ArrayList<>();
        for (Product product : list) {
            ProductListVo productListVo = new ProductListVo();
            BeanUtils.copyProperties(product,productListVo);
            productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://120.78.128.136/"));
            productListVolist.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(list);
        return ServiceResponse.createBySucces(pageInfo);
    }





    private ProductDetailVo copy(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://120.78.128.136/"));

        Category category = categoryMapper.selectById(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        return productDetailVo;
    }

}
