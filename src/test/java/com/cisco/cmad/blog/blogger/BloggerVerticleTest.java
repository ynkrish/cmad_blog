package com.cisco.cmad.blog.blogger;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
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
	public void setUp(TestContext context) throws Exception {
		/*vertx = Vertx.vertx();

		//Find free port and use that in our code for deploying verticle
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("https.port", port));

		//vertx.deployVerticle(BloggerVerticle.class.getName(), options, context.asyncAssertSuccess()); */
	}

	@After
	public void tearDown(TestContext context) {
		//vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testMyApplication(TestContext context) {
		
	/*	final Async async = context.async();

		vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
			response.handler(body -> {
				System.out.println("Body :: " + body.toString());
				context.assertTrue(true);
				async.complete();
			});
		});

		// NOTE: Without this close, the execution does not end and test fails
		// due to TimeoutException
		// Not sure if this is correct given that tearDown is also doing a close
		vertx.close();*/
	}

}
