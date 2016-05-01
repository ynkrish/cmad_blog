package com.cisco.cmad.blog.blogger.service;

import com.cisco.cmad.blog.blogger.model.User;

import java.util.List;

/**
 * Created by kyechcha on 26-Apr-16.
 */
public interface UserService {


    /**
     * Gets User based on User ID
     *
     * @param userId Unique identifier for the User
     * @return User object if found for the given Id
     */
    User getUserDetails(String userId);

    /**
     * Authenticates the user name and password provided
     *
     * @param userName UserName
     * @param password Password
     * @return If authentication is successful, then, returns unique identifier for the user, else
     * returns an empty String
     */
    String authenticateUser(String userName, String password);

    /**
     * Persists the user details in the data store
     *
     * @param user User data model that is to be persisted
     * @return Unique identifier for the User in the data store
     */
    String storeUserDetails(User user);

}
