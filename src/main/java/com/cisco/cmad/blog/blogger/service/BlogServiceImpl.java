package com.cisco.cmad.blog.blogger.service;

import com.cisco.cmad.blog.blogger.dao.BlogDAO;
import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.model.Comment;
import com.google.inject.Inject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;

/**
 * Created by kyechcha on 21-Apr-16.
 */
public class BlogServiceImpl implements BlogService {

    Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);

    BlogDAO blogDao;

    @Inject
    public BlogServiceImpl(BlogDAO blogdao) {
        if (logger.isDebugEnabled())
            logger.debug("Created BlogServiceImpl..");
        this.blogDao = blogdao;
    }

    @Override
    public List<Blog> getBlogs() {
        return blogDao.find().asList();
    }

    @Override
    public List<Blog> getBlogs(String searchKeyword) {
        return blogDao.getBlogs(searchKeyword);
    }

    @Override
    public String storeBlog(Blog blog) {
        return blogDao.save(blog).getId().toString();
    }

    @Override
    public void updateBlogWithComments(String blogId, Comment comment) {
        blogDao.updateBlogWithComments(blogId, comment);
    }
}
