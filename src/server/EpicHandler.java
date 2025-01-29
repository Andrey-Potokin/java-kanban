package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskIntersectionException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager manager;

    public EpicHandler(Gson gson, TaskManager taskManager) {
        this.gson = gson;
        this.manager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPIC: {
                handleGetEpic(exchange);
                break;
            }
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                handleGetEpicSubtasks(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
                break;
            }
            case POST_CREATE_EPIC: {
                handlePostCreateEpic(exchange);
                break;
            }
            case POST_UPDATE_EPIC: {
                handlePostUpdateEpic(exchange);
                break;
            }
            default:
                sendText(exchange, "Некорректный запрос. Попробуйте еще раз", 400);
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            String jsonResponse = gson.toJson(manager.getEpicById(epicId).toString());
            sendText(exchange, jsonResponse, 200);
        } catch (IllegalArgumentException i) {
            sendNotFound(exchange, epicId);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = manager.getEpics().stream()
                .map(Epic::toString)
                .collect(Collectors.joining("\n"));
        String jsonResponse = gson.toJson(response);
        sendText(exchange, jsonResponse, 200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            String response = manager.getEpicById(epicId).getSubtaskList().stream()
                    .map(Subtask::toString)
                    .collect(Collectors.joining("\n"));
            String jsonResponse = gson.toJson(response);
            sendText(exchange, jsonResponse, 200);
        } catch (IllegalArgumentException i) {
            sendNotFound(exchange, epicId);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int epicId = epicIdOpt.get();
        manager.deleteEpicById(epicId);
        sendText(exchange, "Задача с идентификатором " + epicId + " удалена", 200);
    }

    private void handlePostCreateEpic(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            manager.createEpic(epic);
            sendText(exchange, "Задача создана", 201);
        } catch (TaskIntersectionException i) {
            sendHasInteractions(exchange);
        } catch (JsonSyntaxException j) {
            sendText(exchange, "Некорректный JSON", 400);
        } catch (Exception e) {
            System.out.println("Ошибка сериализации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlePostUpdateEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendText(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            if (manager.getEpics().contains(manager.getEpicById(epicId))) {
                String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                Epic epic = gson.fromJson(body, Epic.class);
                epic.setId(epicId);
                manager.updateEpic(epic);
                sendText(exchange, "Задача обновлена", 201);
            } else {
                sendNotFound(exchange, epicId);
            }
        } catch (TaskIntersectionException i) {
            sendHasInteractions(exchange);
        } catch (IllegalArgumentException i) {
            sendNotFound(exchange, epicId);
        } catch (JsonSyntaxException j) {
            sendText(exchange, "Некорректный JSON", 400);
        } catch (Exception e) {
            System.out.println("Ошибка сериализации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET_EPICS;
        } else if (pathParts.length == 2 && requestMethod.equals("POST")) {
            return Endpoint.POST_CREATE_EPIC;
        } else if (pathParts.length == 3 && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC;
        } else if (pathParts.length == 3 && requestMethod.equals("POST")) {
            return Endpoint.POST_UPDATE_EPIC;
        } else if (pathParts.length == 3 && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_EPIC;
        } else if (pathParts.length == 4 && requestMethod.equals("GET") && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }
}
