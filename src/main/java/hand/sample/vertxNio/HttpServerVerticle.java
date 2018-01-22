package hand.sample.vertxNio;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {


        HttpServerOptions opt = new HttpServerOptions();
        opt.setPort(8080);


        HttpServer httpServer = vertx.createHttpServer(opt);

        Router mainRouter = Router.router(vertx);

        mainRouter.route("/io").handler(ctx -> {
            System.out.println("event loop id: " + Thread.currentThread().getId());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                ctx.response().setStatusCode(500).end(e.getMessage());
                return;
            }
            ctx.response().setStatusCode(200).end("Wait 5 secs.");
        });
        mainRouter.route("/nio").handler(ctx -> {
            System.out.println("event loop id: " + Thread.currentThread().getId());
            vertx.executeBlocking(fut -> {
                System.out.println("worker id: " + Thread.currentThread().getId());
                String wait = ctx.request().getParam("wait");
                if (wait.equals("5")) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        fut.fail("Fail to wait 5 sec.");
                        return;
                    }
                    fut.complete("wait 5 sec.");
                    return;
                }
                fut.complete("Non wait 5 sec");
            }, false, (AsyncResult<String> res) -> {
                System.out.println("event loop id: " + Thread.currentThread().getId());
                ctx.response().setStatusCode(200)
                        .end(res.result());
            });
        });

        httpServer.requestHandler(mainRouter::accept);

        httpServer.listen(e -> {
            if (e.succeeded()) {
                System.out.println("Start http server success on port: " + httpServer.actualPort());
                startFuture.complete();
                return;
            }
            System.out.println("Fail to up http server");
            e.cause().printStackTrace();
            startFuture.fail(e.cause());
        });

    }
}
