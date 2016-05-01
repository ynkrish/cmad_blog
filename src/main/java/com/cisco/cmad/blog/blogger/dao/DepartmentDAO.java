package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Department;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public interface DepartmentDAO extends DAO<Department, ObjectId> {
    public List<Department> getDeptListForSite(String companyId, String siteId);
}
