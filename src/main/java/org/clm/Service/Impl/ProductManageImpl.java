package org.clm.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mchange.v1.db.sql.ConnectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.CategoryMapper;
import org.clm.Dao.ProductMapper;
import org.clm.Pojo.Category;
import org.clm.Pojo.Product;
import org.clm.Service.IFileService;
import org.clm.Service.IProductService;
import org.clm.Service.IUserService;
import org.clm.VO.ProductDetailVo;
import org.clm.VO.ProductListVo;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.clm.util.DataTimeUtil;
import org.clm.util.PropertiesUtil;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Ccc
 * @date 2018/9/29 0029 下午 7:05
 */
@Service
public class ProductManageImpl implements IProductService {
    @Autowired
    private IFileService iFileService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServiceResponse saveOrUpdateProduct(Product product) {
        if(product.getId()!=null){
            int i = productMapper.updateByPrimaryKeySelective(product);
            if(i>0){
                return ServiceResponse.createBySuccessMessage("新增产品成功");
            }
            return ServiceResponse.createByErrorMessage("新增产品成功");
        }

        int resultCount = productMapper.insertSelective(product);
        if(resultCount >0){
            return ServiceResponse.createBySuccessMessage("更新产品成功");
        }
        return ServiceResponse.createByErrorMessage("更新产品失败");
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
            productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
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
        if (StringUtils.isBlank(productName)){
            return ServiceResponse.createByErrorMessage("搜索参数为空");
        }
        productName=new StringBuffer().append("%").append(productName).append("%").toString();
        PageHelper.startPage(pageNum,pageSize);
        List<Product> list = productMapper.selectByIdAndName(productName,productId);
        List<ProductListVo> productListVolist = new ArrayList<>();
        for (Product product : list) {
            ProductListVo productListVo = new ProductListVo();
            BeanUtils.copyProperties(product,productListVo);
            productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
            productListVolist.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(list);
        return ServiceResponse.createBySucces(pageInfo);
    }

    private ProductDetailVo copy(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectById(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        return productDetailVo;
    }

}
