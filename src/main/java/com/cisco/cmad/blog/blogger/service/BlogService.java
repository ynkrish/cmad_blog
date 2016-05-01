package com.cisco.cmad.blog.blogger.service;

import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.model.Comment;

import java.util.List;

/**
 * Created by kyechcha on 21-Apr-16.
 */
public interface BlogService {

    /**
     * Retrieves all the blogs present
     *
     * @return List of all blogs
     */
    List<Blog> getBlogs();

    /**
     * Retrieves only those blogs whose tags match the search keyword
     *
     * @param searchKeyword Keyword to be used for searching
     * @return List of all blogs matching the search keyword, viz tag
     */
    List<Blog> getBlogs(String searchKeyword);

    /**
     * Persists the blog data
     *
     * @param blog Blog that is to be persisted
     * @return Unique identifier for the blog
     */
    String storeBlog(Blog blog);

    /**
     * Updates the Blog with specified comment
     * @param blogId Blog identifier that uniquely identifies the blog
     * @param comment Comment that is to be updated against the blog
     */
    void updateBlogWithComments(String blogId, Comment comment);

}
