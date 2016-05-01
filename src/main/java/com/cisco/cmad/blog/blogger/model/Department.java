package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * Created by kyechcha on 21-Apr-16.
 */

@Entity(value = "department", noClassnameStored = true)
@Indexes({
        @Index(value = "deptName", fields = @Field("deptName")),
        @Index(value = "deptAndSite", unique = true, fields = {@Field("deptName"), @Field("siteId")})
})
@ToString(doNotUseGetters = true)
public class Department {

    @Id private ObjectId id;
    @Getter @Setter private String deptName;
    private ObjectId siteId;

    public String getId() {
        return id.toHexString();
    }

    public void setId(String id) {
        this.id = new ObjectId(id);
    }

    public String getSiteId() {
        return (siteId != null) ? siteId.toHexString() : "";
    }

    public void setSiteId(String siteId) {
        if (siteId != null && ObjectId.isValid(siteId))
            this.siteId = new ObjectId(siteId);
    }
}
