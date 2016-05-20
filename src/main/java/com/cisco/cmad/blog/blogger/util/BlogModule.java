package com.cisco.cmad.blog.blogger.util;

import com.cisco.cmad.blog.blogger.dao.*;
import com.cisco.cmad.blog.blogger.service.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.ConfigurationModule;
import net.jmob.guice.conf.core.InjectConfig;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Created by kyechcha on 21-Apr-16.
 */
@BindConfig(value = "blogger")
@Singleton
public class BlogModule extends AbstractModule {

    Logger logger = LoggerFactory.getLogger(BlogModule.class);

    @InjectConfig Optional<Integer> dbPort;
    @InjectConfig Optional<String> dbHost;
    @InjectConfig Optional<String> dbName;

    @Override protected void configure() {

        install(ConfigurationModule.create());
        bind(BlogService.class).to(BlogServiceImpl.class);
        bind(UserService.class).to(UserServiceImpl.class);
        bind(CompanyService.class).to(CompanyServiceImpl.class);
        bind(UserDAO.class).to(UserDAOImpl.class).in(Singleton.class);
        bind(CompanyDAO.class).to(CompanyDAOImpl.class).in(Singleton.class);
        bind(SiteDAO.class).to(SiteDAOImpl.class).in(Singleton.class);
        bind(DepartmentDAO.class).to(DepartmentDAOImpl.class).in(Singleton.class);
        bind(BlogDAO.class).to(BlogDAOImpl.class).in(Singleton.class);

        logger.info("Configured BlogModule successfully");
    }

    @Provides @Singleton  Datastore getDatastore() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + dbHost.orElse("localhost") + ":" + dbPort.orElse(27017)));
        Datastore datastore = new Morphia()
                .mapPackage("com.cisco.cmad.blog.blogger.model")
                .createDatastore(mongoClient, dbName.orElse("cmad-blog"));
        datastore.ensureIndexes();
        return datastore;
    }

    @Provides @Singleton ModelMapper getMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //without this, mapping will fail :)
        mapper.addMappings(PropertyMapper.getCompanyPropertyMap());
        mapper.addMappings(PropertyMapper.getSitePropertyMap());
        mapper.addMappings(PropertyMapper.getDepartmentPropertyMap());
        mapper.addMappings(PropertyMapper.getUserPropertyMap());
        return mapper;
    }

    public static void main (String [] args) {
        Injector injector = Guice.createInjector(new BlogModule());
        BlogModule module = injector.getInstance(BlogModule.class);
        System.out.println("Host :" + module.dbHost.orElse("localhost-else"));
        System.out.println("Port :" + module.dbPort.orElse(-1));
        System.out.println("Name :" + module.dbName.orElse("cmad-blog-else"));
    }

}
