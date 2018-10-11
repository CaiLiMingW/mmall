package org.clm.Service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.clm.Dao.ShippingMapper;
import org.clm.Pojo.Shipping;
import org.clm.Pojo.User;
import org.clm.Service.IShippingService;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ccc
 * @date 2018/10/5 0005 上午 10:57
 */
@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServiceResponse addShipping(User user, Shipping shipping) {
        if(shipping==null){
            return ServiceResponse.createByErrorMessage("地址信息错误");
        }
        shipping.setUserId(user.getId());
        int i = shippingMapper.insertSelective(shipping);
        if(i>0){
            Map map = new HashMap();
            map.put("shippingId",shipping.getId());
            return ServiceResponse.createBySuccess("新建地址成功",map);
        }
        return ServiceResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServiceResponse deleteShipping(User user, Integer shippingId) {
        if(shippingId==null){
            return ServiceResponse.createByErrorMessage("id错误");
        }
        int i = shippingMapper.deleteByPrimaryKey(shippingId);
        if(i>0){
            return ServiceResponse.createByErrorMessage("删除地址成功");
        }
        return ServiceResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServiceResponse updateShipping(User user, Shipping shipping) {
        int i = shippingMapper.checkShippingById(shipping.getId());
        if(i > 0){
            int updateCow = shippingMapper.updateByPrimaryKeySelective(shipping);
            if(updateCow > 0 ){
                return ServiceResponse.createBySuccessMessage("更新地址成功");
            }
            return ServiceResponse.createByErrorMessage("更新地址失败");
        }
        return ServiceResponse.createByErrorMessage("地址不存在");
    }

    @Override
    public ServiceResponse selectShipping(User user, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(user.getId(), shippingId);
        return ServiceResponse.createBySucces(shipping);
    }

    @Override
    public ServiceResponse getShippingList(User user, Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByPrimaryKeyList(user.getId());
        if (shippingList==null){
            return ServiceResponse.createByErrorMessage("地址列表为空");
        }
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServiceResponse.createBySucces(pageInfo);
    }


}
