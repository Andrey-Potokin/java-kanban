package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskIntersectionException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager manager;

    public TaskHandler(Gson gson, TaskManager taskManager) {
        this.gson = gson;
        this.manager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK: {
                handleGetTask(exchange);
                break;
            }
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            case POST_CREATE_TASK: {
                handlePostCreateTask(exchange);
                break;
            }
            case POST_UPDATE_TASK: {
                handlePostUpdateTask(exchange);
                break;
            }
            case UNKNOWN:
                sendText(exchange, "Некорректный запрос. Попробуйте еще раз", 400);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int taskId = taskIdOpt.get();
        try {
            String jsonResponse = gson.toJson(manager.getTaskById(taskId).toString());
            sendText(exchange, jsonResponse, 200);
        } catch (IllegalArgumentException i) {
            sendNotFound(exchange, taskId);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = manager.getTasks().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        String jsonResponse = gson.toJson(response);
        sendText(exchange, jsonResponse, 200);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int taskId = taskIdOpt.get();
        manager.deleteTaskById(taskId);
        sendText(exchange, "Задача с идентификатором " + taskId + " удалена", 200);
    }

    private void handlePostCreateTask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);
            manager.createTask(task);
            sendText(exchange, "Задача создана", 201);
        } catch (TaskIntersectionException i) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendText(exchange, "Ошибка сервера", 500);
        }
    }

    private void handlePostUpdateTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int taskId = taskIdOpt.get();
        try {
            if (manager.getTasks().contains(manager.getTaskById(taskId))) {
                String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                Task task = gson.fromJson(body, Task.class);
                task.setId(taskId);
                manager.updateTask(task);
                sendText(exchange, "Задача обновлена", 201);
            } else {
                sendNotFound(exchange, taskId);
            }
        } catch (TaskIntersectionException i) {
            sendHasInteractions(exchange);
        } catch (IllegalArgumentException i) {
            sendNotFound(exchange, taskId);
        } catch (Exception e) {
            sendText(exchange, "Ошибка сервера", 500);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET_TASKS;
        } else if (pathParts.length == 2 && requestMethod.equals("POST")) {
            return Endpoint.POST_CREATE_TASK;
        } else if (pathParts.length == 3 && requestMethod.equals("GET")) {
            return Endpoint.GET_TASK;
        } else if (pathParts.length == 3 && requestMethod.equals("POST")) {
            return Endpoint.POST_UPDATE_TASK;
        } else if (pathParts.length == 3 && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_TASK;
        }
        return Endpoint.UNKNOWN;
    }
}