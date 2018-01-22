package hand.sample.vertxNio;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        DeploymentOptions opt = new DeploymentOptions();
        opt.setInstances(2);
        Vertx vx = Vertx.vertx();
        vx.deployVerticle(HttpServerVerticle.class.getName(), opt);
    }
}
