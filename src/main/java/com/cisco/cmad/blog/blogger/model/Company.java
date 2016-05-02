package com.cisco.cmad.blog.blogger.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * Created by kyechcha on 19-Apr-16.
 */

@Entity(value = "company", noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("companyName"), @Field("subdomain")}, options = @IndexOptions(unique = true))
})
@ToString(doNotUseGetters = true)
public class Company {
    @Id private ObjectId id;
    @Getter @Setter private String companyName;
    @Getter @Setter private String subdomain;

    public String getId() {
        return id.toHexString();
    }

    public void setId(String id) {
        this.id = new ObjectId(id);
    }

}
