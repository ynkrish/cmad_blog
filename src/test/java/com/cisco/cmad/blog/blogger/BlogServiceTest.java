package com.cisco.cmad.blog.blogger;

import com.cisco.cmad.blog.blogger.dao.BlogDAO;
import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.service.BlogService;
import com.cisco.cmad.blog.blogger.service.BlogServiceImpl;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.lang.System.out;

/**
 * Created by kyechcha on 03-May-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogServiceTest {

    @Mock private BlogDAO blogDaoMock;

    @Mock private Query<Blog> blogQueryMock;

    @InjectMocks private BlogService blogService = new BlogServiceImpl(blogDaoMock);

    @Test
    public void testBlogList() {

        List<Blog> blogList = new ArrayList<>();

        Blog blog = Mockito.mock(Blog.class);
        Mockito.when(blog.getId()).thenReturn(ObjectId.get().toHexString());
        Mockito.when(blog.getTitle()).thenReturn("Dummy Title");
        blogList.add(blog);

        blogList.forEach(b -> out.println("Blog ::" + b.getTitle()));

        BDDMockito.given(blogDaoMock.getBlogs(Optional.empty())).willReturn(blogList);

        List<Blog> allBlogList = blogService.getBlogs(Optional.empty());

        allBlogList.forEach(b -> out.println("AllBlogList ::" + b));

        Assert.assertEquals("Blog size should match ", 1, allBlogList.size());
        Assert.assertEquals("Blog titles shoudl match ", allBlogList.get(0).getTitle(), blogList.get(0).getTitle());

    }


}
