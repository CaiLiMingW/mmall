package org.clm.Service;

import org.clm.Pojo.User;
import org.clm.common.ServiceResponse;

import javax.servlet.http.HttpSession;

/**
 * @author Ccc
 * @date 2018/9/27 0027 上午 8:36
 */
public interface IUserService {
    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str,String type);

    ServiceResponse selectQuestion(String username);

    ServiceResponse<String> checkAnswer(String username,String question,String answer);

    ServiceResponse<String> forgetrestPassword(String username,String password,String fotgetToken);

    ServiceResponse<String> restPassword(String passwordOld, String passwordNew, User user);

    ServiceResponse<User> updateInfomation(User user);

    ServiceResponse<User> getInfomation(Integer id);

    ServiceResponse checkAdminRole(HttpSession session);

    ServiceResponse<User> checkUserLogin(HttpSession session);
}
