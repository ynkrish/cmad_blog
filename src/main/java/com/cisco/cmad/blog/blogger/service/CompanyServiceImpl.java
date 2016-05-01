package com.cisco.cmad.blog.blogger.service;

import com.cisco.cmad.blog.blogger.dao.CompanyDAO;
import com.cisco.cmad.blog.blogger.dao.DepartmentDAO;
import com.cisco.cmad.blog.blogger.dao.SiteDAO;
import com.cisco.cmad.blog.blogger.model.Company;
import com.cisco.cmad.blog.blogger.model.Department;
import com.cisco.cmad.blog.blogger.model.Site;
import com.google.inject.Inject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;

/**
 * Created by kyechcha on 21-Apr-16.
 */
public class CompanyServiceImpl implements CompanyService {

    Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);

    CompanyDAO companyDao;
    SiteDAO siteDao;
    DepartmentDAO deptDao;

    @Inject
    public CompanyServiceImpl(CompanyDAO companyDao, SiteDAO siteDao, DepartmentDAO deptDao) {
        if (logger.isDebugEnabled())
            logger.debug("Created CompanyServiceImpl..");
        this.companyDao = companyDao;
        this.siteDao = siteDao;
        this.deptDao = deptDao;
    }

    @Override
    public List<Company> getCompanyList() {
        return companyDao.find().asList();
    }

    @Override
    public String storeCompanyDetails(Company company) {
        return companyDao.save(company).getId().toString();
    }

    @Override
    public List<Site> getSitesListForCompany(String companyId) {
        return siteDao.getSitesListForCompany(companyId);
    }

    @Override
    public String storeSiteDetails(Site site) {
        return siteDao.save(site).getId().toString();
    }

    @Override
    public String storeDepartmentDetails(Department department) {
        return deptDao.save(department).getId().toString();
    }

    @Override
    public List<Department> getDeptListForSite(String companyId, String siteId) {
        return deptDao.getDeptListForSite(companyId, siteId);
    }

}
