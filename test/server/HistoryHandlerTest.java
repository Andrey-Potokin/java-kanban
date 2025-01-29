package server;

import managers.InMemoryTaskManager;
import managers.TaskManager;
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

public class HistoryHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer hts = new HttpTaskServer(manager);

    public HistoryHandlerTest() throws IOException {
    }

    @Test
    public void testHandle() throws IOException, InterruptedException {
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
        task.setId(1);
        task2.setId(2);
        manager.createTask(task);
        manager.createTask(task2);
        manager.getTaskById(1);
        manager.getTaskById(2);

        hts.startServer();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasksHistory = manager.getHistory();
        assertEquals(2, tasksHistory.size(), "Некорректное количество задач");
        assertEquals(2, tasksHistory.getLast().getId(), "Некорректный порядок задач");

        hts.stopServer();
    }
}