package com.cisco.cmad.blog.blogger;

import com.cisco.cmad.blog.blogger.model.Company;
import com.cisco.cmad.blog.blogger.model.Department;
import com.cisco.cmad.blog.blogger.model.Site;
import com.cisco.cmad.blog.blogger.service.CompanyService;
import com.cisco.cmad.blog.blogger.util.BlogConstants;
import com.cisco.cmad.blog.blogger.util.BlogModule;
import com.cisco.cmad.blog.blogger.util.HttpResponseCode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public class CompanyRestServicesVerticleTest {

	private int port;
	private Vertx vertx;
    private static BlogModule module;

    @Inject CompanyService companyService;

    @BeforeClass
    public static void setUp() throws Exception {

        System.out.println("In setup Method **************************");

        BlogModule blogModule = new BlogModule();

        Injector injector = Guice.createInjector(blogModule);
        module = injector.getInstance(BlogModule.class);

    }

    @Before
	public void before(TestContext context) throws Exception {

        System.out.println("In before method ^^^^^^^^^^^^^^^^^^^^");
        Guice.createInjector(module).injectMembers(this);

        vertx = Vertx.vertx();

		//Find free port and use that in our code for deploying verticle
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("https.port", port));
		vertx.deployVerticle(BloggerVerticle.class.getName(), options, context.asyncAssertSuccess());
	}

    @After
    public void after(TestContext context) {

        vertx.close(context.asyncAssertSuccess());
    }

	@Test
	public void testGetCompanyDetails(TestContext context) {
		final Async async = context.async();

       final List<Company> companyListFromService = companyService.getCompanyList();

        vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
				.getNow(port, "localhost", "/Services/rest/company", resp -> {
                    context.assertEquals(HttpResponseCode.OK.get(), resp.statusCode(), "Status code should be 200 ");
                    context.assertEquals(resp.getHeader(BlogConstants.CONTENT_TYPE), BlogConstants.JSON, "Response content type should be application/json");
			resp.bodyHandler(body -> {
                List<Company> companyList = new ArrayList<>();
				companyList = Json.decodeValue(body.toString(), companyList.getClass());
                context.assertTrue((companyList != null), "Company list should not be null");
                context.assertEquals(companyListFromService.size(), companyList.size(),
                        "Company list size retrieved should be same between service and REST layers ");
                async.complete();
			});
		});
	}

/*
    @Test
    public void testGetSiteDetailsBasedOnCompanyId(TestContext context) {
        final Async async = context.async();

        List<Company> companyList = companyService.getCompanyList();
        if (companyList.size() > 0) {
            String companyId = companyList.get(0).getId();

            vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
                    .getNow(port, "localhost", "/Services/rest/company/" + companyId + "/sites", resp -> {
                        context.assertEquals(HttpResponseCode.OK.get(), resp.statusCode(), "Status code should be 200 ");
                        context.assertEquals(resp.getHeader(BlogConstants.CONTENT_TYPE), BlogConstants.JSON, "Response content type should be application/json");
                        resp.bodyHandler(body -> {
                            List<Site> siteList = new ArrayList<>();
                            siteList = Json.decodeValue(body.toString(), siteList.getClass());
                            context.assertTrue((siteList != null), "Site list should not be null");  //as we always add a dummy site
                            async.complete();
                        });
                    });
        } else {
            // No companies available for tests
            async.complete();
        }
    }

    @Test
    public void testGetSiteDetailsWithInvalidCompanyId(TestContext context) {
        final Async async = context.async();
        final String invalidCompanyId = "invalidcompanyid";

        vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
                .getNow(port, "localhost", "/Services/rest/company/"+ invalidCompanyId + "/sites", resp -> {
                    context.assertEquals(HttpResponseCode.INTERNAL_ERROR.get(), resp.statusCode(), "Status code should be 500 ");
                    async.complete();
                });
    }

    @Test
    public void testGetSiteDetailsWithDummyCompanyId(TestContext context) {
        final Async async = context.async();
        final String dummyCompanyId = new ObjectId().toHexString();

        vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
                .getNow(port, "localhost", "/Services/rest/company/"+ dummyCompanyId + "/sites", resp -> {
                    context.assertEquals(HttpResponseCode.OK.get(), resp.statusCode(), "Status code should be 200 ");
                    context.assertEquals(resp.getHeader(BlogConstants.CONTENT_TYPE), BlogConstants.JSON, "Response content type should be application/json");
                    resp.bodyHandler(body -> {
                        List<Site> siteList = new ArrayList<>();
                        siteList = Json.decodeValue(body.toString(), siteList.getClass());
                        context.assertTrue((siteList != null && siteList.size() == 0), "Site list should be empty for imaginary company id");
                        async.complete();
                    });
                }
        );
    }

    @Test
    public void testGetDepartmentDetailWithCompanyIdAndSiteId(TestContext context) {
        final Async async = context.async();

        List<Company> companyList = companyService.getCompanyList();
        if (companyList.size() > 0) {
            String companyId = companyList.get(0).getId();
            List<Site> siteList = companyService.getSitesListForCompany(companyId);
            String siteId = siteList.get(0).getId();

            vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
                    .getNow(port, "localhost", "/Services/rest/company/" + companyId + "/sites/" + siteId + "/departments", resp -> {
                        context.assertEquals(HttpResponseCode.OK.get(), resp.statusCode(), "Status code should be 200 ");
                        context.assertEquals(resp.getHeader(BlogConstants.CONTENT_TYPE), BlogConstants.JSON, "Response content type should be application/json");
                        resp.bodyHandler(body -> {
                            List<Department> deptList = new ArrayList<>();
                            deptList = Json.decodeValue(body.toString(), deptList.getClass());
                            context.assertTrue((deptList != null), "Dept list should not be null");  //Treating dept at mandatory under site
                            async.complete();
                        });
                    });
        } else {
            // No companies available for tests
            async.complete();
        }
    }

    @Test
    public void testGetDepartmentDetailsWithInvalidCompanyId(TestContext context) {
        final Async async = context.async();


        String companyId = "dummycompanyid";
        String siteId = "dummysiteid";

        vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
                .getNow(port, "localhost", "/Services/rest/company/" + companyId + "/sites/" + siteId + "/departments", resp -> {
                    context.assertEquals(HttpResponseCode.INTERNAL_ERROR.get(), resp.statusCode(), "Status code should be 500 ");
                    async.complete();
                });
    }

    @Test
    public void testGetDepartmentDetailsWithDummyCompanyId(TestContext context) {
        final Async async = context.async();

        String companyId = new ObjectId().toHexString();
        String siteId = new ObjectId().toHexString();

        vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false))
                .getNow(port, "localhost", "/Services/rest/company/" + companyId + "/sites/" + siteId + "/departments", resp -> {
                    context.assertEquals(HttpResponseCode.OK.get(), resp.statusCode(), "Status code should be 200 ");
                    context.assertEquals(resp.getHeader(BlogConstants.CONTENT_TYPE), BlogConstants.JSON, "Response content type should be application/json");
                    resp.bodyHandler(body -> {
                       String respBody = body.toString();
                        context.assertTrue((respBody != null), "Dept list should not be null");
                        context.assertTrue(("{}".equals(respBody)), "Dept list size should be zero for dummy company and site id");
                        async.complete();
                    });
                });
    }
*/
}
