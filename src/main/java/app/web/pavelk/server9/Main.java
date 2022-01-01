package app.web.pavelk.server9;

import app.web.pavelk.server9.dto.ResponseInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.helidon.common.LogConfig;
import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.staticcontent.StaticContentSupport;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) {
        LogConfig.configureRuntime();
        Config config = Config.create();
        MetricsSupport metrics = MetricsSupport.create();
        HealthSupport health = HealthSupport.builder().addLiveness(HealthChecks.healthChecks()).build();

        UserService userService = new UserService();
        Routing routing = Routing.builder()
                .register(health)
                .register(metrics)
                .register("/1", userService)
                .get("/i", (request, response) -> {
                    try {
                        ResponseInfo responseInfo = ResponseInfo.builder()
                                .env(System.getenv())
                                .pr(System.getProperties())
                                .custom(config.get("server").asString().stream().toList())
                                .build();
                        response.send(objectMapper.writeValueAsString(responseInfo));
                    } catch (JsonProcessingException e) {
                        response.send("error");
                        e.printStackTrace();
                    }
                })
                .register("/", StaticContentSupport.builder("web")
                        .welcomeFileName("index.html")
                        .build())
                .build();

        WebServer server = WebServer.builder(routing)
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .build();

        Single<WebServer> webserver = server.start();
        webserver.thenAccept(ws -> {
            LOGGER.info("""
                    WEB server is up!
                    http://localhost:%s/ http://localhost:%s/i
                    http://localhost:%s/metrics http://localhost:%s/health
                    """
                    .formatted(ws.port(),ws.port(), ws.port(), ws.port()));
            ws.whenShutdown().thenRun(() -> LOGGER.info("WEB server is DOWN. Good bye!"));
        }).exceptionallyAccept(t -> LOGGER.warning("Startup failed: " + t.getMessage()));
    }
}
