package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Company;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import javax.inject.Inject;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public class CompanyDAOImpl extends BasicDAO<Company, ObjectId> implements CompanyDAO {

    @Inject
    public CompanyDAOImpl(Datastore ds) {
        super(ds);
    }

}
