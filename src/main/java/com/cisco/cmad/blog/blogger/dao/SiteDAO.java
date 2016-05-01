package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Site;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public interface SiteDAO extends DAO<Site, ObjectId> {
    public List<Site> getSitesListForCompany(String companyId);

}
