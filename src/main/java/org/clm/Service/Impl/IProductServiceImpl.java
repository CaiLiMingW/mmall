package org.clm.Service.Impl;

import ch.qos.logback.classic.turbo.TurboFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
        Product product = redisTemplateUtil.get(Const.objType.PRODUCT,""+productId);

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
                    boolean loop = true;
                    for (Long i = System.currentTimeMillis();loop; i++) {
                        if (System.currentTimeMillis()-i>=50){
                            int j = 50;
                            log.info("\n线程{}:循环中{}毫秒,",Thread.currentThread().getId(),j+=50);
                            i = System.currentTimeMillis();
                            if(!redisTemplateUtil.haskey(Const.objType.LOCK,Const.objType.PRODUCT)){
                                product = redisTemplateUtil.get(Const.objType.PRODUCT,""+productId);
                                if(product!=null){
                                    loop = false;
                                }
                            }
                        }
                    }
                    //取锁失败,已有线程在执行该模块代码，休眠0.5秒后再从缓存中取信息
//                    Thread.sleep(300);
//                    for(int i=0;i <6 ; i++){
//
//                        Thread.sleep(150);
//                        log.info("\n线程{}:循环{}次,",Thread.currentThread().getId(),i++);
//                    }
                    product = productMapper.selectByPrimaryKey(productId);
                    redisTemplateUtil.set(Const.objType.PRODUCT,""+productId,product);
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
        pageInfo = redisTemplateUtil.get(Const.objType.PRODOCTLISTVO,key);


        if(pageInfo==null){
            boolean b = redisLock.setLock(Const.objType.PRODOCTLISTVO);
            if (b){
                List<ProductListVo> productListVos = productMapper.selectProductBycategoryIdAndKeywordOrdeBy(categoryId, keyword, orderBy);
                pageInfo = new PageInfo(productListVos);
                redisTemplateUtil.set(Const.objType.PRODOCTLISTVO,key,pageInfo);
                redisLock.delLock(Const.objType.PRODOCTLISTVO);
            }else {
                try {
                    //取锁失败,已有线程在执行该模块代码，休眠0.5秒后再从缓存中取信息
                    Thread.sleep(300);
                    for(int i=0;i <6 ; i++){
                        pageInfo = redisTemplateUtil.get(Const.objType.PRODOCTLISTVO,key);
                        Thread.sleep(150);
                        log.info("\n线程{}:循环{}次,",Thread.currentThread().getId(),i+1);
                    }
                    log.info("\n→→线程:{}取锁失败,休眠",Thread.currentThread().getId());
                    pageInfo = redisTemplateUtil.get(Const.objType.PRODOCTLISTVO,key);
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
        boolean loop = true;
        int j = 5000;
        for (Long i = System.currentTimeMillis();loop; i++) {
            if (System.currentTimeMillis()-i>=5000){
                System.out.println(j+=500);
                log.info("\n线程{}:循环中{}毫秒,",Thread.currentThread().getId(),j+=50);
                i = System.currentTimeMillis();
                if (j==200){
                    loop =false;
                }
                }
            }
        }

}
