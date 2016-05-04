package com.cisco.cmad.blog.blogger;

import com.cisco.cmad.blog.blogger.util.BlogModule;
import com.cisco.cmad.blog.blogger.util.HttpResponseCode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class BloggerVerticleTest {

	private int port;
	private Vertx vertx;

    @Before
	public void before(TestContext context) throws Exception {

        Injector injector = Guice.createInjector(new BlogModule());
        BlogModule module = injector.getInstance(BlogModule.class);
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
	public void testHomePageFetch(TestContext context) {
		
		final Async async = context.async();

        vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true).setVerifyHost(false)).getNow(port, "localhost", "/", resp -> {

            context.assertEquals(HttpResponseCode.OK.get(), resp.statusCode(), "Status code should be 200 ");
            resp.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("mysocial"), "Body should contain mysocial tag");
                async.complete();
            });
        });
	}
}
