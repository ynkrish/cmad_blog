package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Department;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public class DepartmentDAOImpl extends BasicDAO<Department, ObjectId> implements DepartmentDAO {

    @Inject
    public DepartmentDAOImpl(Datastore ds) {
        super(ds);
    }

    public List<Department> getDeptListForSite(String companyId, String siteId) {
        //We ignore companyId as in current data model, it is not relevant, in case
        //we change the underlying data model wherein both params are required to query
        //then, we can use the additional param
        return createQuery().field("siteId").equal(new ObjectId(siteId)).asList();
    }
}
