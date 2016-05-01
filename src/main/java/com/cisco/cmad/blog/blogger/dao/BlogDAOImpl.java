package com.cisco.cmad.blog.blogger.dao;

import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.model.Comment;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by kyechcha on 30-Apr-16.
 */
public class BlogDAOImpl extends BasicDAO<Blog, ObjectId> implements BlogDAO {

    @Inject
    public BlogDAOImpl(Datastore ds) {
        super(ds);
    }

    public List<Blog> getBlogs(String searchKeyword) {
        return createQuery().field("tags").contains(searchKeyword).asList();
    }

    @Override
    public void updateBlogWithComments(String blogId, Comment comment) {
        UpdateOperations<Blog> ops;
        Query<Blog> query = createQuery().field(Mapper.ID_KEY).equal(new ObjectId(blogId));
        update(query, createUpdateOperations().add("comments", comment));
    }
}
