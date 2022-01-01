
package app.web.pavelk.server9;

import app.web.pavelk.server9.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.http.Http;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import lombok.extern.java.Log;

import javax.json.JsonObject;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;
import java.util.ArrayList;
import java.util.List;

@Log
public class UserService implements Service {

    @Context
    private Providers providers;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    List<User> users;
    Integer id = 4;

    public UserService() {
        users = new ArrayList<>();
        users.add(User.builder().id(1).name("Bob").email("d@g").state("N").code("432").zip(133).build());
        users.add(User.builder().id(2).name("Jon").email("d@y").state("F").code("465").zip(534).build());
        users.add(User.builder().id(3).name("Ford").email("a@r").state("H").code("654").zip(654).build());
        users.add(User.builder().id(4).name("Tom").email("d@t").state("G").code("23").zip(23).build());
    }


    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/", this::getOne)
                .post("/", this::posOne)
                .put("/", this::putOne)
                .delete("/", this::deleteOne);
    }

    private void getOne(ServerRequest request, ServerResponse response) {
        response(response);
    }

    private void posOne(ServerRequest request, ServerResponse response) {
        request.content()
                .as(JsonObject.class)
                .thenAccept(jo -> {
                    log.info(jo.toString());
                    try {
                        User user = objectMapper.readValue(jo.toString(), User.class);
                        users.add(user);
                        response(response);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                })
                .exceptionally(ex -> {
                    response.status(Http.Status.BAD_REQUEST_400).send("error");
                    return null;
                });
    }

    private void putOne(ServerRequest request, ServerResponse response) {
        System.out.println(request.content());
        response(response);
    }

    private void deleteOne(ServerRequest request, ServerResponse response) {
        System.out.println(request.content());
        response(response);
    }

    private void response(ServerResponse response) {
        try {
            response.send(objectMapper.writeValueAsString(users));
        } catch (JsonProcessingException e) {
            log.warning(e.getMessage());
            response.send("error");
        }
    }
}