package com.crawl.backend;

import com.crawl.backend.dto.QueryCreationDTO;
import com.crawl.backend.query.QueryController;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.util.NoSuchElementException;

import static spark.Spark.*;

public class Main {
    private static final URI baseUrl = URI.create(System.getenv("BASE_URL"));
    private static final QueryController queryController;
    private static final Gson gson = new Gson();

    static {
        try {
            queryController = new QueryController(baseUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        get("/crawl/:id", (req, res) -> {
            var statusDTO = queryController.get(req.params("id")).orElseThrow();

            res.status(HttpStatus.OK_200);
            res.type("application/json");

            return statusDTO;
        }, gson::toJson);

        post("/crawl", (req, res) -> {
            var creationDTO = gson.fromJson(req.body(), QueryCreationDTO.class);

            var idDTO = queryController.create(creationDTO).orElseThrow();

            res.status(HttpStatus.ACCEPTED_202);
            res.type("application/json");

            return idDTO;
        }, gson::toJson);

        exception(NoSuchElementException.class, (e, req, res) -> res.status(HttpStatus.NOT_FOUND_404));

        exception(JsonSyntaxException.class, (e, req, res) -> res.status(HttpStatus.BAD_REQUEST_400));
    }
}
