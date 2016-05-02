package com.cisco.cmad.blog.blogger.handler;

import com.cisco.cmad.blog.blogger.model.*;
import com.cisco.cmad.blog.blogger.service.CompanyService;
import com.cisco.cmad.blog.blogger.service.UserService;
import com.cisco.cmad.blog.blogger.util.BlogConstants;
import com.cisco.cmad.blog.blogger.util.HttpResponseCode;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;

/**
 * Created by kyechcha on 29-Apr-16.
 */
public class UserHandler {

    Logger logger = LoggerFactory.getLogger(UserHandler.class);

    @Inject UserService userService;
    //Note Refactor to see if we can have single responsibility
    @Inject CompanyService companyService;

    @Inject ModelMapper mapper;

    private final String DEFAULT_SITE_NAME = "Dummy site name";

    /**
     * Handles the GET REST call for user details based on ID Returns Json representation of user if user id is found
     * and returns the following
     * <p>
     * HTTP 400 in case request is sent with empty ID or if we dont get id from session cookie
     * HTTP 404 in case no user is found matching the ID value
     * HTTP 200 in case data is found for given ID - in this case, user data would be returned in JSON format
     * HTTP 500 in case of any server error while processing the request
     *
     * @param rc
     */
    public void getUserDetails(RoutingContext rc) {

        HttpServerResponse response = rc.response();

        String queryParam = rc.request().getParam("signedIn");

        if (queryParam != null && queryParam.trim().equals(Boolean.TRUE.toString())) {

            String id = rc.session().get("id");

            logger.info("Session in getUserDetails is :" + id);


            if (id == null || id.trim().length() == 0) {
                response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Empty ID");
            } else {

                rc.vertx().executeBlocking(future -> {

                    try {
                        User user = userService.getUserDetails(id);
                        future.complete(user);
                    } catch (Exception ex) {
                        logger.error("Error in retrieving user details for id " + id, ex);
                        future.fail(ex);
                    }
                }, res -> {
                    if (res.succeeded()) {

                        Object obj = res.result();
                        User user = null;

                        if (obj != null) {
                            user = (User) obj;
                            response.putHeader(BlogConstants.CONTENT_TYPE, BlogConstants.JSON);
                            response.setStatusCode(HttpResponseCode.OK.get()).end(Json.encodePrettily(user));

                        } else {
                            response.setStatusCode(HttpResponseCode.NOT_FOUND.get()).end("User Not Found !!");
                        }
                    } else {
                        response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                                .end("Error while fetching user details :" + res.cause().getMessage());
                    }

                });
            }

            if (logger.isDebugEnabled())
                logger.debug("GET success :" + Thread.currentThread().getId());
        } else {
            response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Not signed in");
        }
    }

    /**
     * Authenticates the user based on the username and password sent
     * <p>
     * Returns HTTP 400 in case no data is sent
     * Returns HTTP 401 in case username/password combination is not correct or not found
     * Returns HTTP 200 in case of successful auth
     *
     * @param rc
     */
    public void authenticateUser(RoutingContext rc) {

        //Note: Try MongoDB Authentication API instead of current approach
        HttpServerResponse response = rc.response();

        String jsonString = rc.getBodyAsString();

        if (jsonString == null || jsonString.trim().length() == 0) {
            response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Bad Request");
        } else {

            rc.vertx().executeBlocking(future -> {
                try {
                    User dto = Json.decodeValue(jsonString, User.class);
                    String id = userService.authenticateUser(dto.getUserName(), dto.getPassword());
                    future.complete(id);
                } catch (Exception ex) {
                    logger.error("Error while trying to authenticate user ", ex);
                    future.fail(ex);
                }

            }, res -> {
                if (res.succeeded()) {
                    String id = res.result().toString();
                    if (id != null && id.trim().length() > 0) {
                        //add data to session
                        rc.session().put("id", id);
                        response.setStatusCode(HttpResponseCode.OK.get()).end();
                    } else {
                        response.setStatusCode(HttpResponseCode.UNAUTHORIZED.get()).end("Invalid username and/or Password");
                    }
                } else {
                    response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                            .end("Error while authenticating user " + res.cause().getMessage());
                }
            });
        }
    }

    /**
     * Handles the POST REST call for storing user details. In case it is user registration for existing
     * company, then, user is created under given company, site and department.
     * <p>
     * In case it is a user registration in addition to Company registration, then, first company is
     * created, then, site would be created, then, department would be created and finally user would be
     * created in the company, site and department created earlier
     * <p>
     * Returns HTTP 201 with the ID of the created user object
     * Returns HTTP 500 in case company or site or department or user creation fails
     *
     * @param rc
     */
    public void storeUserDetails(RoutingContext rc) {

        String jSonString = rc.getBodyAsString(); //get JSON body as String

        if (logger.isDebugEnabled())
            logger.debug("JSON String from POST " + jSonString);

        RegistrationDTO reg = Json.decodeValue(jSonString, RegistrationDTO.class);

        if (logger.isDebugEnabled())
            logger.debug("RegistrationDTO object : " + reg);

        /*
           Currently, the CMAD project does not have any use case for updating Company, Department or Site
           Given this, modeling them as separate aggregates is ok, but the approach taken would be different
           in case the use case was/is different.

           Given the way we are handling aggregates here and its storage, in case we have any specific collection
           insert failing, then, parent collection removal will need to be handled

           Note: for now, not doing above as this is just a PoC, but this is something we need to handle given the
           way the collection has been designed
         */

        if (reg != null && reg.getIsCompany() != null && reg.getIsCompany()) {
            //Company and user registration
            //first create company, then, create site, then, department and use the ids from these in
            //user creation
            HttpServerResponse response = rc.response();

            rc.vertx().executeBlocking(future -> {
                Company company = mapper.map(reg, Company.class);

                logger.debug("Company object post object mapping :" + company);

                try {
                    String companyId = companyService.storeCompanyDetails(company);
                    logger.info("Company created. Id :" + companyId);

                    Site site = mapper.map(reg, Site.class);
                    site.setCompanyId(companyId);
                    site.setSiteName(DEFAULT_SITE_NAME); //GUI is not sending site name

                    logger.debug("Site object post object mapping :" + site);

                    String siteId = companyService.storeSiteDetails(site);

                    logger.info("Site created. Id :" + siteId);

                    Department dept = mapper.map(reg, Department.class);
                    dept.setSiteId(siteId);
                    logger.debug("Department object post object mapping :" + dept);

                    String deptId = companyService.storeDepartmentDetails(dept);

                    logger.info("Department created. Id :" + deptId);

                    User user = mapper.map(reg, User.class);

                    user.setCompanyId(companyId);
                    user.setSiteId(siteId);
                    user.setDeptId(deptId);

                    logger.debug("User object post object mapping :" + user);

                    String userid = userService.storeUserDetails(user);
                    logger.info("User created. Id: " + userid);
                    future.complete(userid);

                } catch (Exception ex) {
                    logger.error("Error occurred while trying to save data ", ex);
                    future.fail(ex);
                }
            }, res -> {
                if (res.succeeded())
                    response.setStatusCode(HttpResponseCode.CREATED.get()).end(res.result().toString());
                else {
                    response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get()).end(res.cause().getMessage());
                }
            });
        } else {
            //User registration
            rc.vertx().executeBlocking(future -> {

                User user = mapper.map(reg, User.class);

                if (logger.isDebugEnabled()) {
                    logger.debug("User object post object mapping :" + user);
                }

                try {
                    String id = userService.storeUserDetails(user);
                    if (logger.isDebugEnabled())
                        logger.debug("POST success, ID: " + id + " Thread :" + Thread.currentThread().getId());
                    future.complete(id);
                } catch (Exception ex) {
                    logger.error("Error occurred while trying to save User details ", ex);
                    future.fail(ex);
                }
            }, res -> {
                HttpServerResponse response = rc.response();
                if (res.succeeded())
                    response.setStatusCode(HttpResponseCode.CREATED.get()).end(res.result().toString());
                else
                    response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get()).end(res.cause().getMessage());
            });
        }
    }
}
