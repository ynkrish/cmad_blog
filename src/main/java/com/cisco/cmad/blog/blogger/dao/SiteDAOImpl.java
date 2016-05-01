package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Site;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public class SiteDAOImpl extends BasicDAO<Site, ObjectId> implements SiteDAO {

    @Inject
    public SiteDAOImpl(Datastore ds) {
        super(ds);
    }

    public List<Site> getSitesListForCompany(String companyId) {
        return createQuery().field("companyId").equal(new ObjectId(companyId)).asList();
    }
}
