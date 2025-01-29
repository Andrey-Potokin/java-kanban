package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.stream.Collectors;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager manager;

    public PrioritizedHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && exchange.getRequestMethod().equals("GET")) {
            String response = manager.getPrioritizedTasks().stream()
                    .map(Task::toString)
                    .collect(Collectors.joining("\n"));
            String jsonResponse = gson.toJson(response);
            sendText(exchange, jsonResponse, 200);
        } else {
            sendText(exchange, "Некорректный запрос. Попробуйте еще раз", 400);
        }
    }
}