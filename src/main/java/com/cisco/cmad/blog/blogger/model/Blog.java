package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by kyechcha on 25-Apr-16.
 */

@Entity(value = "blog", noClassnameStored = true)
@Indexes({
        @Index(fields = @Field("tags")),
        @Index(fields = @Field("userId"))
})
@ToString(doNotUseGetters = true)
public class Blog {

    @Id private ObjectId id;
    private ObjectId userId;
    @Getter @Setter private String content;
    @Getter @Setter private String tags;
    @Getter @Setter private String userFirst;
    @Getter @Setter private String userLast;
    @Getter @Setter private String title;
    @Getter @Setter private Date date;
    @Embedded @Getter @Setter private List<Comment> comments;

    public String getId() {
        return (id != null) ? id.toHexString() : "";
    }

    public void setId(String id) {
        if (id != null)
            this.id = new ObjectId(id);
    }

    public String getUserId() {
        return (userId != null) ? userId.toHexString() : "";
    }

    public void setUserId(String userId) {
        if (userId != null)
            this.userId = new ObjectId(userId);
    }

    public void addComment(Comment comment) {
        if (comments != null)
            comments.add(comment);
    }

}
