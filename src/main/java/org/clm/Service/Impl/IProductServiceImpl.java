package org.clm.Service.Impl;

import ch.qos.logback.classic.turbo.TurboFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.ProductMapper;
import org.clm.Pojo.Product;
import org.clm.Service.IProductService;
import org.clm.VO.ProductListVo;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.clm.util.RedisLock;
import org.clm.util.RedisTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import java.util.*;

/**
 * @author Ccc
 * @date 2018/10/10 0010 下午 2:34
 */
@Service
public class IProductServiceImpl implements IProductService {
    private final static Logger log = LoggerFactory.getLogger(IProductServiceImpl.class);
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private RedisLock redisLock;


    @Override
    public ServiceResponse getProductDetail(Integer productId) {
        if (productId==null){
            return ServiceResponse.createByErrorMessage("产品ID错误");
        }
        //从缓存获取product
        Object obj = redisTemplateUtil.get(Const.objType.PRODUCT, "" + productId);
        if (StringUtils.equals("noExits", (CharSequence) obj)){
            return ServiceResponse.createByErrorMessage("该商品已下架或删除");
        }
        Product product = (Product) obj ;

        //若为Null，从数据库取最新数据,并更新到缓存中
        if (product==null){
            log.info("\n缓存被清空,从数据库获取数据");
            boolean b = redisLock.setLock(Const.objType.PRODUCT);
            if (b){
                log.info("\n当前线程获取锁,查询数据并存入缓存");
                product = productMapper.selectByPrimaryKey(productId);
                redisTemplateUtil.set(Const.objType.PRODUCT,""+productId,product);
                boolean b1 = redisLock.delLock(Const.objType.PRODUCT);
            }else {
                log.info("\n→→线程:{},锁状态,进入休眠",Thread.currentThread().getId());
                //todo 定时任务修改 thread.sleep?
                try {
                    log.info("\n线程{}:循环中{}毫秒,",Thread.currentThread().getId());
                    //取锁失败,已有线程在执行该模块代码，休眠0.5秒后再从缓存中取信息
                    for(int i=0;i <6 && product!=null ; i++){
                        Thread.sleep(150);
                        Object obj2 = redisTemplateUtil.get(Const.objType.PRODUCT, "" + productId);
                        if (StringUtils.equals("noExits", (CharSequence) obj)){
                            return ServiceResponse.createByErrorMessage("该商品已下架或删除");
                        }
                        product = (Product) obj2 ;
                        log.info("\n线程{}:循环{}次,",Thread.currentThread().getId(),i++);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("线程休眠出错:{}",e);
                }

            }
            //如果product还为NULL，则可能数据库不存在该商品
            if (product==null){
                return ServiceResponse.createByErrorMessage("该商品已下架或删除");
            }
        }
        return ServiceResponse.createBySucces(product);
    }

    @Override
    public ServiceResponse getProductList( Integer categoryId, Integer pageNum, Integer pageSize, String keyword, String orderBy) {
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = null;
        String key = null;
        if (StringUtils.isNotBlank(orderBy)){
            orderBy = StringUtils.equals("price_desc",orderBy)?Const.OrderBy.PRICE_DESC:Const.OrderBy.PRICE_ASC;
        }
        key = "" + (categoryId==null?"search":categoryId) + pageNum + pageSize + keyword+orderBy;

        Object obj = redisTemplateUtil.get(Const.objType.PRODOCTLISTVO,key);
        if (StringUtils.equals("noExits", (CharSequence) obj)){
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        pageInfo = (PageInfo) obj ;

        if(pageInfo==null){
            boolean b = redisLock.setLock(Const.objType.PRODOCTLISTVO);
            if (b){
                List<ProductListVo> productListVos = productMapper.selectProductBycategoryIdAndKeywordOrdeBy(categoryId, keyword, orderBy);
                if (productListVos.size()==0){
                    pageInfo = null;
                }else {
                    pageInfo = new PageInfo(productListVos);
                }
                redisTemplateUtil.set(Const.objType.PRODOCTLISTVO,key,pageInfo);
                redisLock.delLock(Const.objType.PRODOCTLISTVO);
            }else {
                try {
                    //取锁失败,已有线程在执行该模块代码，休眠0.5秒后再从缓存中取信息
                    for(int i = 0;i <6 && pageInfo!=null ; i++){
                        Object obj2 = redisTemplateUtil.get(Const.objType.PRODOCTLISTVO,key);
                        if (StringUtils.equals("noExits", (CharSequence) obj)){
                            return ServiceResponse.createByErrorMessage("参数错误");
                        }
                        pageInfo = (PageInfo) obj2 ;
                        Thread.sleep(150);
                        log.info("\n线程{}:循环{}次,",Thread.currentThread().getId(),i+1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error("线程休眠出错:{}",e);
                }
            }

            if (pageInfo==null){
                return ServiceResponse.createByErrorMessage("参数错误");
            }

        }


        return ServiceResponse.createBySucces(pageInfo);
    }

    public static void main(String[] args) {
        //编译时自动引入了StringBuilder,使用append()方法拼接字符串
        String str1 = " "+"a"+"b"+"c"+"";
        List list = Lists.newArrayList();
        //StringBuilder线程不安全 StringBuffer线程安全
        StringBuilder str2 = new StringBuilder();
        str2.append("a").append("b").append("c");
        System.out.println(str1);
        System.out.println(str2);
        String str3 = str2.toString();
        System.out.println("unTrim:"+str1);
        //trim()删除字符串两端空白符,若无修改返回原始对象
        System.out.println("Trim:"+str1.trim());


        System.out.println(str1.substring(1,4));

    }

}
