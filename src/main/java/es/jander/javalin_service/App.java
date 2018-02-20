package es.jander.javalin_service;

import io.javalin.BasicAuthCredentials;
import io.javalin.HaltException;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.ApiBuilder.*;

public class App
{
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args)
    {
        Javalin.create()
                .port(7000)
                .enableStandardRequestLogging()
                .routes(() -> {
                    path("/api", () -> {
                        get(ctx -> ctx.result("This is the 'get' response"));
                        get("/:id", ctx -> ctx.result("Got id: " + ctx.param("id")));
                        put(ctx -> {
                            String payload = ctx.body();
                            ctx.result("Received payload (put): '" + payload + "'");
                        });
                        post(ctx -> {
                            String payload = ctx.body();
                            ctx.result("Received payload (post): '" + payload + "'");
                        });
                        delete(ctx -> ctx.status(200));
                    });
                    before("/secure", ctx -> {
                        BasicAuthCredentials bac = ctx.basicAuthCredentials();
                        if (bac == null) {
                            throw new HaltException(401, "Unauthorized");
                        }
                        if ("admin".equalsIgnoreCase(bac.getUsername()) && "pass".equalsIgnoreCase(bac.getPassword())) {
                            ctx.next();
                        } else {
                            throw new HaltException(401, "Unauthorized");
                        }
                    });
                    path("/secure", () -> {
                        get(ctx -> ctx.result("The secret key is 1234"));
                    });
                })
                .start();
    }
}
