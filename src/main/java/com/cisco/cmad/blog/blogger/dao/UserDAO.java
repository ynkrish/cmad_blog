package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public interface UserDAO extends DAO<User, ObjectId> {
    public User getUserByUserName(String userName);
    public User getUserById(String id);
}
