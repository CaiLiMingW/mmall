package org.clm.Dao;

import org.apache.ibatis.annotations.Param;
import org.clm.Pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    int checkAnswer(@Param("username") String username,@Param("question") String question, @Param("answer") String answer);

    int checkPassword(@Param("password") String password, @Param("userId") Integer userId);

    String  selectQuestionByUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int updatePasswordByusername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);
}