package org.clm.Service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.UserMapper;
import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ccc
 * @date 2018/9/27 0027 上午 8:37
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     *用户登录验证功能
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount ==0){
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }
        /**验证密码*/
        User user = userMapper.selectLogin(username, password);
        if(user==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServiceResponse<String> register(User user){
        /**检查用户名是否存在*/
        ServiceResponse<String> vaildResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }

        /**检查邮箱是否被注册*/
        vaildResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }
        /**设置为普通用户*/
        user.setRole(Const.Role.ROLE_CUSTOMER);
        /**MD5加密*/

        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 用户名邮箱验证工具
     * @param str 验证数值 str
     * @param type 验证类型 username或email
     * @return
     */
    @Override
    public ServiceResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            /**验证username*/
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            /**验证email/*/
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServiceResponse.createByErrorMessage("邮箱已被使用");
                }
            }

        }else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccessMessage("校验成功");
    }
}
