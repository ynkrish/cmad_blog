package com.cisco.cmad.blog.blogger;

import com.cisco.cmad.blog.blogger.handler.BlogHandler;
import com.cisco.cmad.blog.blogger.handler.CompanyHandler;
import com.cisco.cmad.blog.blogger.handler.FailureHandler;
import com.cisco.cmad.blog.blogger.handler.UserHandler;
import com.cisco.cmad.blog.blogger.util.BlogModule;
import com.cisco.cmad.blog.blogger.util.HttpResponseCode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;

import javax.inject.Inject;

public class BloggerVerticle extends AbstractVerticle {

    Logger logger = LoggerFactory.getLogger(BloggerVerticle.class);

    @Inject BlogHandler blogHandler;
    @Inject UserHandler userHandler;
    @Inject CompanyHandler companyHandler;

    @Override
    public void start(Future<Void> startFuture) {

        Injector injector = Guice.createInjector(new BlogModule());
        BlogModule module = injector.getInstance(BlogModule.class);
        Guice.createInjector(module).injectMembers(this);

        logger.info("BloggerVerticle started " + Thread.currentThread().getId());

        //Router object is responsible for dispatching the HTTP requests to the right handler
        Router router = Router.router(vertx);

        /* Session Handler */
        router.route().handler(CookieHandler.create());
        //Not clustered currently, so using local session store
        //Session handler requires a cookie handler before in the chain
        router.route().handler(SessionHandler
                .create(LocalSessionStore.create(vertx))
                .setCookieHttpOnlyFlag(true)
                .setCookieSecureFlag(true)
                .setSessionTimeout(1800000L)); //30 minutes

        //Adds X-XSRF-TOKEN header. XSRF-TOKEN cookie - disabled currently - adds header per request. Check src for behavior
        //router.route().handler(CSRFHandler.create("CMAD - Blogger App, not yet a good secret !!"));

	    /*
         * Perform REST Operations here. This is done *before* the static handler for path /
	     * to ensure that things work ok and requests are not by default routed to static
	     * handler
	     */

        /* Good security practices - from - http://vertx.io/blog/writing-secure-vert-x-web-apps/ */
        router.route().handler(ctx -> {
            ctx.response()
                    // do not allow proxies to cache the data
                    .putHeader("Cache-Control", "no-store, no-cache")
                    // prevents Internet Explorer from MIME - sniffing a
                    // response away from the declared content-type
                    .putHeader("X-Content-Type-Options", "nosniff")
                    //  Strict HTTPS (for about ~6Months) -- NOTE: This gets ignored for Self Signed certs and generates lots of logs in FF, so commented for now
                    //.putHeader("Strict-Transport-Security", "max-age=" + 15768000)
                    // IE8+ do not allow opening of attachments in the context of this resource
                    .putHeader("X-Download-Options", "noopen")
                    // enable XSS for IE
                    .putHeader("X-XSS-Protection", "1; mode=block")
                    // deny frames
                    .putHeader("X-FRAME-OPTIONS", "DENY");

            ctx.next();
        });

        //GET Operations
        router.get("/Services/rest/user").handler(userHandler::getUserDetails);
        router.get("/Services/rest/company").handler(companyHandler::getCompanyList);
        router.get("/Services/rest/company/:id/sites").handler(companyHandler::getSitesForCompany);
        router.get("/Services/rest/company/:companyId/sites/:siteId/departments").handler(companyHandler::getDepartmentsForCompanyAndSite);
        router.get("/Services/rest/blogs").handler(blogHandler::getBlogs);

        router.route().handler(BodyHandler.create());

        //POST operations
        router.post("/Services/rest/user/auth").handler(userHandler::authenticateUser);
        router.post("/Services/rest/user/register").handler(userHandler::storeUserDetails);
        router.post("/Services/rest/blogs").handler(blogHandler::storeBlog);
        router.post("/Services/rest/blogs/:blogId/comments").handler(blogHandler::submitComment);

        //Static handler for resource

        router.route().handler(StaticHandler.create().setCachingEnabled(true)::handle);

        //For any exceptions that are not taken care of in code
        router.route().failureHandler(FailureHandler.create());

        //Enable SSL - currently using self signed certs
        HttpServerOptions httpOpts = new HttpServerOptions();
        httpOpts.setKeyStoreOptions(new JksOptions().setPath("keystore.jks").setPassword("cmad@cisco"));
        httpOpts.setSsl(true);

        HttpServer server = vertx.createHttpServer(httpOpts);

        server.requestHandler(router::accept)
                .listen(
                        config().getInteger("https.port", 8443), result -> {
                            if (result.succeeded()) {
                                startFuture.complete();
                            } else {
                                startFuture.fail(result.cause());
                            }
                        }
                );

        // NOTE: Implement logout in Front end code
        router.route("/logout").handler(context -> {
            context.clearUser();
            logger.info("Logout called");
            // Redirect back to the index page
            context.response()
                    .putHeader("location", "/")
                    .setStatusCode(HttpResponseCode.REDIRECT.get())
                    .end();
        });

    }

    public static void main(String[] args) {

        int port = 8443;

        /*
        Only for testing - in container, we need to pass this as -D params
         */
        //Use SLF4J instead of ugly JUL.
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        System.setProperty("logback.configurationFile", "logback.xml");

        System.out.println("In BloggerVerticle main method ");
        //TBD: Pass no. of workers via configuration on command line during startup
        VertxOptions options = new VertxOptions().setWorkerPoolSize(10);
        Vertx vertx = Vertx.vertx(options);

	    /*
	     * only for testing - in container, we would end up using -conf and pass location of conf file
	     */
        DeploymentOptions depOps = new DeploymentOptions();
        depOps.setConfig(new JsonObject().put("https.port", port));

        vertx.deployVerticle(new BloggerVerticle(), depOps);
    }
}