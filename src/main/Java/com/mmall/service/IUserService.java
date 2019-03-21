package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @Interfacename IUserService
 * @Description
 * @Date 2019/3/15 14:40
 * @Created by godFather
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetPassword(String username, String passwordNew,
                                          String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld,
                                         String passwordNew, User user);

    ServerResponse<User> updateUserInfo(User user);

    ServerResponse<User> getUserDetailInfo(Integer userId);

    ServerResponse checkAdminRole(User user);
}
