package com.cisco.cmad.blog.blogger.util;

import com.cisco.cmad.blog.blogger.model.*;
import org.modelmapper.PropertyMap;

/**
 * Created by kyechcha on 25-Apr-16.
 */
public class PropertyMapper {

    public static PropertyMap<RegistrationDTO, Company> getCompanyPropertyMap() {
        return new PropertyMap<RegistrationDTO, Company>() {
            @Override
            protected void configure() {
                map().setCompanyName(source.getCompanyName());
                map().setSubdomain(source.getSubdomain());
            }
        };
    }

    public static PropertyMap<RegistrationDTO, Site> getSitePropertyMap() {
        return new PropertyMap<RegistrationDTO, Site>() {
            @Override
            protected void configure() {
                map().setSubdomain(source.getSubdomain());
            }
        };
    }

    public static PropertyMap<RegistrationDTO, Department> getDepartmentPropertyMap() {
        return new PropertyMap<RegistrationDTO, Department>() {
            @Override
            protected void configure() {
                map().setDeptName(source.getDeptName());
            }
        };
    }

    public static PropertyMap<RegistrationDTO, User> getUserPropertyMap() {
        return new PropertyMap<RegistrationDTO, User>() {
            @Override
            protected void configure() {
                map().setFirst(source.getFirst());
                map().setLast(source.getLast());
                map().setUserName(source.getUserName());
                map().setPassword(source.getPassword());
                map().setEmail(source.getEmail());
            }
        };
    }


}
