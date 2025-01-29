package server;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer hts = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        hts.startServer();
    }

    @AfterEach
    public void shutDown() {
        hts.stopServer();
    }

    @Test
    public void testGetTasksWith200Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        Task task2 = new Task(
                0,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1)
        );
        manager.createTask(task);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskByIdWith200Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        task.setId(1);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskByIdWith404Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        task.setId(1);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTaskWith200Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        task.setId(1);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }


    @Test
    public void testUpdateTaskWith200Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        manager.createTask(task);

        Task newTask = new Task(
                1,
                "Новая задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1)
        );

        String taskJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Новая задача", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testCreateTaskWith201Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testCreateTaskWith406Response() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        Task task2 = new Task(
                0,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1)
        );
        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}