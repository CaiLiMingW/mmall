package org.clm.Service;

import org.clm.Pojo.User;
import org.clm.common.ServiceResponse;

/**
 * @author Ccc
 * @date 2018/9/27 0027 上午 8:36
 */
public interface IUserService {
    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str,String type);
}
