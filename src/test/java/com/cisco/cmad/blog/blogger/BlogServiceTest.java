package com.cisco.cmad.blog.blogger;

import com.cisco.cmad.blog.blogger.dao.BlogDAO;
import com.cisco.cmad.blog.blogger.model.Blog;
import com.cisco.cmad.blog.blogger.model.Comment;
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
import org.mongodb.morphia.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Created by kyechcha on 03-May-16.
 *
 * Unit test case for CompanyService
 */
@RunWith(MockitoJUnitRunner.class)
public class BlogServiceTest {

    @Mock private BlogDAO blogDaoMock;

    @Mock private Key<Blog> blogKey;

    @InjectMocks private BlogService blogService = new BlogServiceImpl(blogDaoMock);


    private Blog getBlog(String title, Optional<String> tag) {
        Blog blog = Mockito.mock(Blog.class);
        Mockito.when(blog.getId()).thenReturn(ObjectId.get().toHexString());
        Mockito.when(blog.getTitle()).thenReturn(title);
        Mockito.when(blog.getTags()).thenReturn(tag.orElse(""));
        return blog;
    }

    /**
     * Try to fetch all blogs
     */
    @Test
    public void testBlogList() {

        List<Blog> blogList = new ArrayList<>();
        blogList.add(getBlog("Dummy Title", Optional.empty()));
        blogList.add(getBlog("Dummy Title #2", Optional.empty()));

        blogList.forEach(b -> out.println("Blog ::" + b.getTitle()));

        BDDMockito.given(blogDaoMock.getBlogs(Optional.empty())).willReturn(blogList);

        List<Blog> allBlogList = blogService.getBlogs(Optional.empty());

        allBlogList.forEach(b -> out.println("AllBlogList ::" + b));

        Assert.assertEquals("Blog size should match ", 2, allBlogList.size());
        Assert.assertEquals("Blog titles should match ", allBlogList.get(0).getTitle(), blogList.get(0).getTitle());
    }

    /**
     * Try to fetch blogs using keyword
     */
    @Test
    public void testBlogListUsingKeyword() {
        List<Blog> blogList = new ArrayList<>();
        blogList.add(getBlog("Dummy Title #2", Optional.ofNullable("tag1, tag2")));
        blogList.add(getBlog("Title #2", Optional.ofNullable("tag3, tag4")));

        BDDMockito.given(blogDaoMock.getBlogs(Optional.ofNullable("tag1"))).willReturn(blogList.subList(0,1));

        List<Blog> taggedBlogList = blogService.getBlogs(Optional.ofNullable("tag1"));

        Assert.assertEquals("Blog size should match ", 1, taggedBlogList.size());
        Assert.assertEquals("Blog titles should match ", taggedBlogList.get(0).getTitle(), blogList.get(0).getTitle());
    }

    /**
     * Try to fetch blogs using a tag that does not exist.
     */
    @Test
    public void testBlogListUsingMissingKeyword() {
        List<Blog> blogList = new ArrayList<>();

        blogList.add(getBlog("Dummy Title", Optional.ofNullable("tag1, tag2")));

        BDDMockito.given(blogDaoMock.getBlogs(Optional.ofNullable("tag1"))).willReturn(blogList);
        BDDMockito.given(blogDaoMock.getBlogs(Optional.ofNullable("tag2"))).willReturn(blogList);

        List<Blog> taggedBlogList = blogService.getBlogs(Optional.ofNullable("tag3"));

        Assert.assertEquals("Blog size should match ", 0, taggedBlogList.size());
    }

    /**
     * Test that on saving a Blog, an identifier representing the blog is
     * returned
     */
    @Test
    public void testStoreBlog() {

        Blog blog = getBlog("Test Title", Optional.ofNullable("tag1, tag2"));

        String objId = (new ObjectId()).toHexString();

        BDDMockito.given(blogDaoMock.save(blog)).willReturn(blogKey);
        BDDMockito.given(blogKey.getId()).willReturn(objId);

        String retVal = blogService.storeBlog(blog);

        Assert.assertEquals("ObjectId should be returned when Blog is saved ", retVal, objId);

    }

    /**
     * Test to ensure that the blog that is saved can be retrieved back post saving
     */
    @Test
    public void testStoreAndRetrieveBlog() {

        List<Blog> blogList = new ArrayList<>();
        Blog blog = getBlog("Dummy Title", Optional.ofNullable("tag1, tag2"));
        blogList.add(blog);

        String objId = (new ObjectId()).toHexString();
        BDDMockito.given(blogDaoMock.save(blog)).willReturn(blogKey);
        BDDMockito.given(blogKey.getId()).willReturn(objId);
        BDDMockito.given(blogDaoMock.getBlogs(Optional.empty())).willReturn(blogList);

        blogService.storeBlog(blog);

        List<Blog> allBlogList = blogService.getBlogs(Optional.empty());

        Assert.assertEquals("Blog size should match ", 1, allBlogList.size());
    }

    @Test
    public void testUpdateBlogWithComments() {

        String firstName = "Krishnan";
        String lastName = "Y";

        List<Comment> commentList = new ArrayList<>();

        Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.getContent()).thenReturn("Comment #1");
        commentList.add(comment);

        List<Blog> blogList = new ArrayList<>();
        Blog blog = getBlog("Photography", Optional.ofNullable("macro, dslr"));

        Mockito.when(blog.getComments()).thenReturn(commentList);
        blogList.add(blog);

        BDDMockito.given(blogDaoMock.getBlogs(Optional.empty())).willReturn(blogList);

        List<Blog> allBlogList = blogService.getBlogs(Optional.empty());

        Assert.assertEquals("Comment saved should be same when retrieved ", allBlogList.get(0).getComments(), blogList.get(0).getComments());
        Assert.assertEquals("Blog size should match ", 1, allBlogList.size());

    }
}
