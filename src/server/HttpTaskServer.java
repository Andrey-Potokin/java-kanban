package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static managers.Managers.getDefault;
import static server.GsonFactory.getGson;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager manager;
    private static HttpServer server;
    private static boolean isRunning = false;

    public HttpTaskServer(TaskManager manager) throws IOException {
        Gson gson = getGson();
        HttpTaskServer.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        setupContexts(gson);
    }

    private void setupContexts(Gson gson) {
        server.createContext("/tasks", new TaskHandler(gson, manager));
        server.createContext("/epics", new EpicHandler(gson, manager));
        server.createContext("/subtasks", new SubtaskHandler(gson, manager));
        server.createContext("/history", new HistoryHandler(gson, manager));
        server.createContext("/prioritized", new PrioritizedHandler(gson, manager));
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(getDefault());
            httpTaskServer.startServer();
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }

    public void startServer() {
        if (!isRunning) {
            server.start();
            isRunning = true;
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } else {
            System.out.println("Сервер уже запущен.");
        }
    }

    public void stopServer() {
        if (isRunning) {
            server.stop(0);
            isRunning = false;
            System.out.println("HTTP-сервер остановлен.");
        } else {
            System.out.println("Сервер не работает.");
        }
    }
}