package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.model.Comment;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public interface BlogDAO extends DAO<Blog, ObjectId> {
    public List<Blog> getBlogs(String searchKeyword);

    public void updateBlogWithComments(String blogId, Comment comment);
}
