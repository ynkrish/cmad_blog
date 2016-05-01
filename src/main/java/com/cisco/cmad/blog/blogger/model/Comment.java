package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;

import java.util.Date;

/**
 * Created by kyechcha on 25-Apr-16.
 */
@Embedded
@ToString(doNotUseGetters = true)
public class Comment {

    private ObjectId userId;
    @Getter @Setter private String content;
    @Getter @Setter private String blogId;
    @Getter @Setter private String userFirst;
    @Getter @Setter private String userLast;
    @Getter @Setter private Date date;

    public String getUserId() {
        return (userId != null) ? userId.toHexString() : "";
    }

    public void setUserId(String userId) {
        if (userId != null)
            this.userId = new ObjectId(userId);
    }
}
