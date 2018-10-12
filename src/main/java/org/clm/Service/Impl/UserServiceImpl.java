package org.clm.Service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.clm.Dao.CategoryMapper;
import org.clm.Dao.UserMapper;
import org.clm.Pojo.User;
import org.clm.Service.IUserService;
import org.clm.common.Const;
import org.clm.common.ResponseCode;
import org.clm.common.ServiceResponse;
import org.clm.common.TokenCache;
import org.clm.util.CookieUtil;
import org.clm.util.JsonUtil;
import org.clm.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

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
        /**验证密码1*/
        User user = userMapper.selectLogin(username, password);
        if(user==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }

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

    /**
     * 查找账号提示问题
     * @param username
     * @return
     */
    @Override
    public ServiceResponse<String> selectQuestion(String username){
        /**验证用户是否存在*/
        ServiceResponse response = this.checkValid(username, Const.USERNAME);
        /**如果该用户存在*/
        if(!response.isSuccess()){
            /**查询该用户的提示问题*/
            String question = userMapper.selectQuestionByUsername(username);
            /**如果该用户的问题不为空,返回question*/
            if(StringUtils.isNotBlank(question)){
                return ServiceResponse.createBySucces(question);
            }
            /**问题为空返回错误信息错误信息*/
            return ServiceResponse.createByErrorMessage("该用户问题未设置");
        }
        /**用户不存在返回错误信息*/
        return ServiceResponse.createBySuccessMessage("用户名不存在");
    }

    /**
     * 验证用户的问题和答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServiceResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            /**问题答案正确*/
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setkey("token_"+username,forgetToken);
            return ServiceResponse.createBySucces(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("答案错误");
    }

    @Override
    public ServiceResponse<String> forgetrestPassword(String username,String passwordNew,String fotgetToken){
        if(StringUtils.isBlank(fotgetToken)){
            return ServiceResponse.createByErrorMessage("参数错误，token为空");
        }
        String token = TokenCache.getkey("token_"+username);
        if(StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("Token无效或已过期");
        }
        if(!StringUtils.equals(token,fotgetToken)){

            return ServiceResponse.createByErrorMessage("token错误,请重新进行修改");
        }
        userMapper.updatePasswordByusername(username, passwordNew);
        return ServiceResponse.createBySuccessMessage("修改密码成功");
    }

    @Override
    public ServiceResponse<String> restPassword(String passwordOld, String passwordNew, User user){
        /**防止横向越权,校验旧密码是否正常*/
        int resultCount = userMapper.checkPassword(passwordOld, user.getId());
        if (resultCount == 0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        int i = userMapper.updatePasswordByusername(user.getUsername(), passwordNew);
        if(i>0){
            user.setPassword(passwordNew);
            return ServiceResponse.createBySuccessMessage("修改成功");
        }
        return ServiceResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServiceResponse<User> updateInfomation(User user) {
        //检查邮箱是否被使用
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount>0){
            return ServiceResponse.createByErrorMessage("邮箱已存在");
        }
        /**只修改以下内容,防止越权修改*/
        User updateuser = new User();
        updateuser.setId(user.getId());
        updateuser.setEmail(user.getEmail());
        updateuser.setQuestion(user.getQuestion());
        updateuser.setAnswer(user.getAnswer());
        updateuser.setPhone(user.getPhone());

        resultCount = userMapper.updateByPrimaryKeySelective(updateuser);
        if (resultCount>0){
            return ServiceResponse.createBySuccessMessage("修改成功");
        }
       return ServiceResponse.createByErrorMessage("修改失败");
    }

    @Override
    public ServiceResponse<User> getInfomation(Integer id){
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        return ServiceResponse.createBySucces(user);
    }

    @Override
    public ServiceResponse checkAdminRole(HttpServletRequest request){
        /**从本地cookie获取sessionId*/
        String sessionId = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(sessionId)){
            return ServiceResponse.createByErrorMessage("用户登录时间已过期,无法获取用户信息");
        }
        /**根据sessionId从Redis获取当前登录用户信息的json字符串*/
        String str = RedisPoolUtil.get(sessionId);
        if (str==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        /**将jsonStr转换为pojo对象*/
        User user = JsonUtil.StringToObj(str, User.class);
        if (user==null){
            return ServiceResponse.createByCodeError(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }

        //登录信息进行验证
        User user1 = userMapper.selectByPrimaryKey(user.getId());
        if (user1==null){
            ServiceResponse.createByErrorMessage("用户不存在");
        }

        if(user1.getRole()== Const.Role.ROLE_ADMIN){
            return ServiceResponse.createBySucces(user);
        }
        return ServiceResponse.createByErrorMessage("没有权限");
    }

    @Override
    public ServiceResponse<User> checkUserLoginUnNeedLogin(HttpServletRequest request) {
        String sessionId = CookieUtil.readLoginToken(request);
        if (StringUtils.isBlank(sessionId)){
            return ServiceResponse.createByErrorMessage("登录已超时");
        }
        String userJsonStr = RedisPoolUtil.get(sessionId);
        User user = JsonUtil.StringToObj(userJsonStr, User.class);
        if (user==null){
            return ServiceResponse.createByErrorMessage("用户信息出错，请重新登录");
        }
        return ServiceResponse.createBySucces(user);
    }

    @Override
    public ServiceResponse<User> checkUserLoginCookie(HttpServletRequest request) {
        String sessionId = CookieUtil.readLoginToken(request);
        //判断redis中sessionID是否过期
        if (StringUtils.isEmpty(sessionId)){
            return ServiceResponse.createByCodeError(ResponseCode.NEED_LOGIN.getCode(),"用户登录已过期,无法获取用户信息");
        }
        //从redis获取登录用户信息
        String userJsonStr = RedisPoolUtil.get(sessionId);
        User user = JsonUtil.StringToObj(userJsonStr, User.class);
        if (user==null){
            return ServiceResponse.createByCodeError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
        }

        return ServiceResponse.createBySucces(user);
    }


}
