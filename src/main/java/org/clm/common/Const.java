package org.clm.common;

/** 常态常量
 * @author Ccc
 * @date 2018/9/27 0027 上午 10:34
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Cart{
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    public interface Role{
        /**普通用户*/
        int ROLE_CUSTOMER = 0;
        /**管理员*/
        int ROLE_ADMIN = 1;

    }
}
