package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

@ToString(doNotUseGetters = true)
public class RegistrationDTO {

    @Setter @Getter private String first;
    @Setter @Getter private String last;
    @Setter @Getter private String userName;
    @Setter @Getter private String password;  //NOTE: Setter data encryption
    @Setter @Getter private String email;
    @Setter @Getter private String companyName;
    @Setter @Getter private String subdomain;
    @Setter @Getter private String deptName;
    @Setter @Getter private Boolean isCompany;

    private ObjectId companyId;
    private ObjectId siteId;
    private ObjectId deptId;

    public String getCompanyId() {
        return (companyId != null) ? companyId.toHexString() : "";
    }

    public void setCompanyId(String companyId) {
        this.companyId = new ObjectId(companyId);
    }

    public String getSiteId() {
        return (siteId != null) ? siteId.toHexString() : "";
    }

    public void setSiteId(String siteId) {
        this.siteId = new ObjectId(siteId);
    }

    public String getDeptId() {
        return (deptId != null) ? deptId.toHexString() : "";
    }

    public void setDeptId(String deptId) {
        this.deptId = new ObjectId(deptId);
    }
}