package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import javax.inject.Inject;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public class UserDAOImpl extends BasicDAO<User, ObjectId> implements UserDAO {

    @Inject
    public UserDAOImpl(Datastore ds) {
        super(ds);
    }

    @Override
    public User getUserByUserName(String userName) {
        return createQuery().field("userName").equal(userName).get();
    }

    @Override
    public User getUserById(String id) {
        return createQuery().field("id").equal(new ObjectId(id)).get();
    }
}
