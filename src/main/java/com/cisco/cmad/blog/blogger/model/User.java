package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "users", noClassnameStored = true)
@Indexes({
        @Index(value = "userAndCompany", unique = true, fields = {@Field("userName"), @Field("companyId")}),
        @Index(value = "email", unique = true, fields = @Field("email"))
})
@ToString(doNotUseGetters = true)
public class User {

    @Id private ObjectId id;

    @Getter @Setter private String first;
    @Getter @Setter private String last;
    @Getter @Setter private String userName;
    @Getter @Setter private String password;  //FIXME: Enc/Dec
    @Getter @Setter private String email;

    private ObjectId companyId;
    private ObjectId siteId;
    private ObjectId deptId;

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

    public String getSiteId() {
        return (siteId != null) ? siteId.toHexString() : "";
    }

    public void setSiteId(String siteId) {
        if (siteId != null && ObjectId.isValid(siteId))
            this.siteId = new ObjectId(siteId);
    }

    public String getDeptId() {
        return (deptId != null) ? deptId.toHexString() : "";
    }

    public void setDeptId(String deptId) {
        if (deptId != null && ObjectId.isValid(deptId))
            this.deptId = new ObjectId(deptId);
    }

}