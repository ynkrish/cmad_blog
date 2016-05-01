package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Company;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public interface CompanyDAO extends DAO<Company, ObjectId> {


}
