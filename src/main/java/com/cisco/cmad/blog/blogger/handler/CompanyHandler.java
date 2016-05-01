package com.cisco.cmad.blog.blogger.handler;

import com.cisco.cmad.blog.blogger.model.Company;
import com.cisco.cmad.blog.blogger.model.Department;
import com.cisco.cmad.blog.blogger.model.Site;
import com.cisco.cmad.blog.blogger.service.CompanyService;
import com.cisco.cmad.blog.blogger.util.BlogConstants;
import com.cisco.cmad.blog.blogger.util.HttpResponseCode;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by kyechcha on 29-Apr-16.
 */
public class CompanyHandler {
    Logger logger = LoggerFactory.getLogger(CompanyHandler.class);

    @Inject CompanyService companyService;

    /**
     * GET the list of *all* companies
     * <p>
     * Returns 200 Status code and data of companies in JSON format In case no company data exists,
     * then, it will return a empty JSON string
     * HTTP 500 in case of any server error while processing the request
     *
     * @param rc
     */
    public void getCompanyList(RoutingContext rc) {

        rc.vertx().executeBlocking(future -> {
            //No criteria - get all companies
            try {
                List<Company> companies = companyService.getCompanyList();

                String retVal = null;
                if (companies != null && !companies.isEmpty()) {
                    retVal = Json.encodePrettily(companies);
                } else {
                    //Sending empty info instead of 404 on purpose. No company data being present does not
                    //indicate an error
                    retVal = "{}";
                }
                future.complete(retVal);
            } catch (Exception ex) {
                logger.error("Error in fetching company list ", ex);
                future.fail(ex.getCause());
            }
        }, res -> {
            HttpServerResponse response = rc.response();
            if (res.succeeded()) {
                response.setStatusCode(HttpResponseCode.OK.get()).end(res.result().toString());
            } else {
                response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                        .end("Error in fetching company list :" + res.cause().getMessage());
            }
        });
    }

    /**
     * Returns a list of Sites for a given Company
     * <p>
     * Returns HTTP 400 in case no company id is passed
     * Returns HTTP 200 with empty response in case no sites are found for company
     * Returns HTTP 200 with list of sites in JSON format in case sites are found for company
     *
     * @param rc
     */
    public void getSitesForCompany(RoutingContext rc) {

        HttpServerResponse response = rc.response();
        String companyId = rc.request().getParam("id");

        rc.vertx().executeBlocking(future -> {

            if (logger.isDebugEnabled())
                logger.debug("Company Id :" + companyId);

            if (companyId == null || companyId.trim().length() == 0) {
                //Invalid company id
                response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Empty Company Id");
            } else {
                try {
                    List<Site> sites = companyService.getSitesListForCompany(companyId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Sites for company :" + companyId + " ::" + sites);
                    }
                    future.complete(sites);
                } catch (Exception ex) {
                    logger.error("Error while fetching sites for company :" + companyId, ex);
                    future.fail(ex.getCause());
                }
            }
        }, res -> {
            if (res.succeeded()) {
                Object obj = res.result();
                List<Site> sites = null;

                response.putHeader(BlogConstants.CONTENT_TYPE, BlogConstants.JSON);

                if (obj == null) {
                    response.setStatusCode(HttpResponseCode.OK.get()).end("{}");
                    logger.warn("No sites found for Company :" + companyId);
                } else {
                    sites = (List<Site>) obj;
                    response.setStatusCode(HttpResponseCode.OK.get()).end(Json.encodePrettily(sites));
                }
            } else {
                response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                        .end("Error while getting site info for company : " + companyId
                                + " Error :" + res.cause().getMessage());
            }
        });
    }

    /**
     * Returns a list of Departments for a given Site
     * <p>
     * Note: Currently even though company is passed as parameter, this value is not used
     * for querying as we can directly query sites for getting the departments within the site
     * <p>
     * Returns HTTP 400 in case no site id is passed
     * Returns HTTP 200 with empty response in case no departments are found for site
     * Returns HTTP 200 with list of departments in JSON format in case departments are found for site
     *
     * @param rc
     */
    public void getDepartmentsForCompanyAndSite(RoutingContext rc) {
        HttpServerResponse response = rc.response();
        String companyId = rc.request().getParam("companyId");
        String siteId = rc.request().getParam("siteId");

        rc.vertx().executeBlocking(future -> {

            if (logger.isDebugEnabled()) {
                logger.debug("Company Id :" + companyId + " Site Id" + siteId);
            }

            //Ignoring company id as we can directly query departments based on sites in current design

            if (siteId == null || siteId.trim().length() == 0) {
                //Invalid site id
                response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Empty Site Id");
            } else {
                try {
                    List<Department> departments = companyService.getDeptListForSite(companyId, siteId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Departments for Site :" + siteId + " ::" + departments);
                    }
                    future.complete(departments);
                } catch (Exception ex) {
                    logger.error("Error while fetching departments for Company :"
                            + companyId + " and Site :" + siteId, ex);
                    future.fail(ex.getCause());
                }
            }
        }, res -> {
            if (res.succeeded()) {
                Object obj = res.result();
                List<Department> departments = null;

                response.putHeader(BlogConstants.CONTENT_TYPE, BlogConstants.JSON);

                if (obj == null) {
                    response.setStatusCode(HttpResponseCode.OK.get()).end("{}");
                    logger.info("No Department found for Site :" + siteId);
                } else {

                    departments = (List<Department>) obj;
                    if (departments.isEmpty()) {
                        response.setStatusCode(HttpResponseCode.OK.get()).end("{}");
                        logger.info("No Department found for Site :" + siteId);

                    } else {
                        response.setStatusCode(HttpResponseCode.OK.get()).end(Json.encodePrettily(departments));
                    }
                }
            } else {
                response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                        .end("Error while getting department info for Company : " + companyId + " Site :" + siteId
                                + " Error :" + res.cause().getMessage());
            }
        });

    }
}
