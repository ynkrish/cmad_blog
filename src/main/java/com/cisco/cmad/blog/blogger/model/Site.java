package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * Created by kyechcha on 21-Apr-16.
 */

@Entity(value = "site", noClassnameStored = true)
@Indexes({
        @Index(value = "siteName", fields = @Field("siteName")),
        @Index(value = "siteAndCompany", unique = true, fields = {@Field("siteName"), @Field("companyId")})
})
@ToString(doNotUseGetters = true)
public class Site {

    @Id private ObjectId id;
    private ObjectId companyId;
    @Getter @Setter private String siteName;
    @Getter @Setter private String subdomain;

    public String getId() {
        return id.toHexString();
    }

    public void setId(String id) {
        this.id = new ObjectId(id);
    }

     public String getCompanyId() {
        return (companyId != null) ? companyId.toHexString() : "";
    }

    public void setCompanyId(String companyId) {
        if (companyId != null && ObjectId.isValid(companyId))
            this.companyId = new ObjectId(companyId);
    }

}
