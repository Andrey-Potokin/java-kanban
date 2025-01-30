package server;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
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
import static server.GsonFactory.getGson;

public class EpicHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer hts = new HttpTaskServer(manager);
    Gson gson = getGson();

    public EpicHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteSubtasks();
        manager.deleteEpics();
        hts.startServer();
    }

    @AfterEach
    public void shutDown() {
        hts.stopServer();
    }

    @Test
    public void testGetEpicsWith200Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Epic epic2 = new Epic(
                0,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 2, 0)
        );
        manager.createEpic(epic);
        manager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> EpicsFromManager = manager.getEpics();

        assertNotNull(EpicsFromManager, "Задачи не возвращаются");
        assertEquals(2, EpicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicByIdWith200Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        epic.setId(1);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", epicsFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicByIdWith404Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        epic.setId(1);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpicSubtasksWith200Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Subtask subtask = new Subtask(
                0,
                "Подзадача",
                Status.NEW,
                "Описание подзадачи",
                LocalDateTime.of(2025, JANUARY, 1, 2, 0),
                Duration.ofHours(1),
                1
        );
        Subtask subtask2 = new Subtask(
                0,
                "Подзадача2",
                Status.NEW,
                "Описание подзадачи2",
                LocalDateTime.of(2025, JANUARY, 1, 4, 0),
                Duration.ofHours(1),
                1
        );
        epic.setId(1);
        subtask.setId(2);
        subtask2.setId(3);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача", subtasksFromManager.getFirst().getTitle(), "Некорректное id подзадач");
    }

    @Test
    public void testDeleteEpicWith200Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        epic.setId(1);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(200, response.statusCode());
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testCreateEpicWith201Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", epicsFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testCreateEpicWith406Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        Epic epic2 = new Epic(
                0,
                "Задача2",
                Status.NEW,
                "Описание задачи2",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        String epicJson = gson.toJson(epic);
        String epicJson2 = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson2))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateEpicWith200Response() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 0, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 1, 0)
        );
        manager.createEpic(epic);

        Epic newEpic = new Epic(
                1,
                "Новая задача",
                Status.NEW,
                "Описание задачи",
                LocalDateTime.of(2025, JANUARY, 1, 1, 0),
                Duration.ofHours(1),
                LocalDateTime.of(2025, JANUARY, 1, 2, 0)
        );

        String epicJson = gson.toJson(newEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Новая задача", epicsFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }
}
