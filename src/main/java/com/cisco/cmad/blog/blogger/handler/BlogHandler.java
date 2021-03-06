package com.cisco.cmad.blog.blogger.handler;

import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.model.Comment;
import com.cisco.cmad.blog.blogger.model.User;
import com.cisco.cmad.blog.blogger.service.BlogService;
import com.cisco.cmad.blog.blogger.service.UserService;
import com.cisco.cmad.blog.blogger.util.BlogConstants;
import com.cisco.cmad.blog.blogger.util.HttpResponseCode;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by kyechcha on 29-Apr-16.
 */
public class BlogHandler {

    Logger logger = LoggerFactory.getLogger(BlogHandler.class);

    @Inject BlogService blogService;
    @Inject UserService userService;

    /**
     * Retreives all blogs or returns blogs based on tag search
     * <p>
     * Returns
     * HTTP 200 in case of successful result. Result would contain JSON data representing either
     * all the blogs or all blogs that match the search criteria
     * HTTP 500 in case of any error. Response would currently just contain the error message
     *
     * @param rc
     */
    public void getBlogs(RoutingContext rc) {

        HttpServerResponse response = rc.response();

        String queryParam = rc.request().getParam("tag");

        if (logger.isDebugEnabled()) {
            logger.debug("Tag search ? , tag :" + queryParam);
        }

        rc.vertx().executeBlocking(future -> {
            //Get all blogs
            try {
                if (queryParam != null && queryParam.trim().length() > 0) {
                    //Search by tag
                    List<Blog> blogs = blogService.getBlogs(Optional.ofNullable(queryParam));
                    future.complete(Json.encodePrettily(blogs));
                } else {
                    //get all blogs
                    List<Blog> blogs = blogService.getBlogs(Optional.empty());
                    future.complete(Json.encodePrettily(blogs));
                }
            } catch (Exception ex) {
                logger.error("Exception while trying to fetch blogs " + queryParam, ex);
                future.fail(ex);
            }
        }, res -> {
            if (res.succeeded()) {
                response.putHeader(BlogConstants.CONTENT_TYPE, BlogConstants.JSON);
                response.setStatusCode(HttpResponseCode.OK.get()).end(res.result().toString());
            } else {
                response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                        .end("Problem in fetching blogs :" + res.cause().getMessage());
            }
        });
    }

    /**
     * Persists the blog details
     * <p>
     * Returns
     * HTTP 400 in case User details was not found in session
     * HTTP 404 in case User details is not found in data store
     * HTTP 200 in case blog was successfully stored. In this case, the blog id would be returned
     * in response.
     *
     * @param rc
     */
    public void storeBlog(RoutingContext rc) {

        HttpServerResponse response = rc.response();

        String jSonString = rc.getBodyAsString(); //get JSON body as String

        if (logger.isDebugEnabled())
            logger.debug("JSON String from POST " + jSonString);

        Blog blog = Json.decodeValue(jSonString, Blog.class);

        if (logger.isDebugEnabled())
            logger.debug("RegistrationDTO object after json Decode : " + blog);

        //Add the user details from session into the blog

        String id = rc.session().get("id");

        logger.info("Session User id is :" + id);

        if (id == null || id.trim().length() == 0) {
            response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Empty ID");
        } else {
            rc.vertx().executeBlocking(future -> {
                try {
                    User user = userService.getUserDetails(id);
                    future.complete(user);
                } catch (Exception ex) {
                    logger.error("Error while retrieving user details for id :" + id, ex);
                    future.fail(ex);
                }
            }, res -> {

                if (res.succeeded()) {
                    Object obj = res.result();
                    if (obj != null) {
                        User user = (User) obj;
                        blog.setUserFirst(user.getFirst());
                        blog.setUserLast(user.getLast());
                        blog.setUserId(user.getId());
                        logger.debug("Blog object after adding user info : " + blog);
                    } else {
                        response.setStatusCode(HttpResponseCode.NOT_FOUND.get()).end("User Not Found :" + id);
                    }
                } else {
                    response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                            .end("Error while storing blog details, unable to fetch user details :" + res.cause().getMessage());
                }
            });
        }

        rc.vertx().executeBlocking(future -> {
            try {

                //Add date just before storing :)
                blog.setDate(new Date());
                future.complete(blogService.storeBlog(blog));
            } catch (Exception ex) {
                logger.error("Error occurred while trying to save Blog details ", ex);
                future.fail(ex);
            }
        }, res -> {
            if (res.succeeded())
                response.setStatusCode(HttpResponseCode.CREATED.get()).end(res.result().toString());
            else
                response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                        .end("Error while storing blog details :" + res.cause().getMessage());
        });
    }

    /**
     * Persists the Comment details for a given blog
     * <p>
     * Returns
     * HTTP 400 in case User details was not found in session
     * HTTP 404 in case User details is not found in data store
     * HTTP 200 in case comment was successfully updated.
     *
     * @param rc
     */
    public void submitComment(RoutingContext rc) {

        HttpServerResponse response = rc.response();

        //Get the blog id from URL and use that to store the details
        String jSonString = rc.getBodyAsString(); //get JSON body as String

        String blogId = rc.request().getParam("blogId");

        if (logger.isDebugEnabled())
            logger.debug("JSON String from POST " + jSonString + " Blog Id :" + blogId);

        Comment comment = Json.decodeValue(jSonString, Comment.class);

        if (logger.isDebugEnabled())
            logger.debug("Comment object : " + comment);

        String id = rc.session().get("id");
        logger.info("Session User id is :" + id);

        if (id == null || id.trim().length() == 0) {
            response.setStatusCode(HttpResponseCode.BAD_REQUEST.get()).end("Empty ID");
        } else {
            rc.vertx().executeBlocking(future -> {
                try {
                    User user = userService.getUserDetails(id);
                    future.complete(user);
                } catch (Exception ex) {
                    logger.error("Error while trying to fetch user details ", ex);
                    future.fail(ex);
                }
            }, res -> {
                if (res.succeeded()) {

                    Object obj = res.result();
                    User user = null;

                    if (obj != null) {
                        user = (User) obj;

                        comment.setUserFirst(user.getFirst());
                        comment.setUserLast(user.getLast());
                        comment.setUserId(user.getId());

                        if (logger.isDebugEnabled()) {
                            logger.debug("Comment object after adding user info : " + comment);
                        }
                    } else {
                        response.setStatusCode(HttpResponseCode.NOT_FOUND.get()).end("User details not found, id :" + id);
                    }
                } else {
                    response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                            .end("Error while getting uesr details, id : " + res.cause().getMessage());
                }
            });
        }

        rc.vertx().executeBlocking(future -> {

            try {
                //Add date just before storing :)
                comment.setDate(new Date());
                blogService.updateBlogWithComments(blogId, comment);
                if (logger.isDebugEnabled())
                    logger.debug("Comment updated in blog successfully");
                future.complete();
            } catch (Exception ex) {
                logger.error("Error occurred while trying to save Comment details for blog : " + blogId, ex);
                future.fail(ex);
            }
        }, res -> {
            if (res.succeeded())
                response.setStatusCode(HttpResponseCode.CREATED.get()).end();
            else
                response.setStatusCode(HttpResponseCode.INTERNAL_ERROR.get())
                        .end("Error while saving comment " + res.cause().getMessage());
        });
    }

}
