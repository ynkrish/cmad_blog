package com.cisco.cmad.blog.blogger.service;

import com.cisco.cmad.blog.blogger.dao.UserDAO;
import com.cisco.cmad.blog.blogger.model.User;
import com.google.inject.Inject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by kyechcha on 21-Apr-16.
 */
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    UserDAO userDao;

    @Inject
    public UserServiceImpl(UserDAO dao) {
        if (logger.isDebugEnabled())
            logger.debug("Created UserService..");
       this.userDao = dao;
    }

    @Override
    public User getUserDetails(String userId) {
        User user =  userDao.getUserById(userId);
        //Note: For now, set the password to empty string in response so that it is not sent
        // back to GUI. Ideal is probably to have another DTO or use custom Json encoding
        user.setPassword("");
        return user;
    }

    @Override
    public String authenticateUser(String userName, String password) {
        User user = userDao.getUserByUserName(userName);
        if (user!= null && user.getPassword().equals(password)) {
            return user.getId();
        }
       return "";
    }

    @Override
    public String storeUserDetails(User user) {
        return userDao.save(user).getId().toString();
    }

}
