package com.cisco.cmad.blog.blogger.service;

import com.cisco.cmad.blog.blogger.model.Company;
import com.cisco.cmad.blog.blogger.model.Department;
import com.cisco.cmad.blog.blogger.model.Site;

import java.util.List;

/**
 * Created by kyechcha on 26-Apr-16.
 */
public interface CompanyService {

    /**
     * Gets list of all companies
     *
     * @return List of companies present
     */
    List<Company> getCompanyList();

    /**
     * Gets sites for a given company
     *
     * @param companyId Identifier for company for which sites is to be returned
     * @return List of sites for the given Company
     */
    List<Site> getSitesListForCompany(String companyId);

    /**
     * Gets departments for given Company and Site
     *
     * @param companyId Unique identifier for the Company
     * @param siteId Unique identifier for the site within the company
     * @return List of Departments for the given Company and Site
     */
    List<Department> getDeptListForSite(String companyId, String siteId);

    /**
     * Persists the Company details
     *
     * @param company Company data model that is to be persisted
     * @return Unique identifier for the Company in the data store
     */
    String storeCompanyDetails(Company company);

    /**
     * Persists the Site details
     *
     * @param site Site data model that is to be persisted
     * @return Unique identifier for the site in the data store
     */
    String storeSiteDetails(Site site);

    /**
     * Persists the Department details
     *
     * @param department Department data model that is to be persisted
     * @return Unique identifier for the department in the data store
     */
    String storeDepartmentDetails(Department department);
}
