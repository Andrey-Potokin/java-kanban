# Трекер задач (финальное задание 4-9-го спринтов)


## Описание проекта

Это бэкенд‑решение для трекера задач с HTTP‑API для удалённого управления:
* управление задачами, подзадачами, эпиками;
* отслеживание статусов;
* иерархия работ (эпики и подзадачи);
* история просмотров;
* сохранение состояния (в памяти или файле);
* приоритезация задач по времени старта;
* проверка пересечений временных интервалов;
* **доступ через HTTP‑запросы** (REST‑подобный API).

## Архитектура API

### Базовые пути (эндпоинты)

| Путь | Описание |
|------|--------|
| `/tasks` | Работа с обычными задачами |
| `/subtasks` | Работа с подзадачами |
| `/epics` | Работа с эпиками |
| `/history` | Получение истории просмотров |
| `/prioritized` | Получение задач в порядке приоритета (по `startTime`) |


### Методы и статусы

| Операция | HTTP‑метод | Статус успеха | Статус ошибки |
|--------|-----------|------------|-------------|
| Получение списка | `GET` | `200 OK` | `404 Not Found` (если ресурс пуст/не существует) |
| Создание/обновление | `POST` | `201 Created` | `406 Not Acceptable` (пересечение интервалов)<br>`500 Internal Server Error` (ошибка сервера) |
| Удаление | `DELETE` | `200 OK` | `404 Not Found` |


### Формат данных
* **JSON** — стандартный формат для запросов и ответов.
* Пример тела запроса для создания задачи:
  ```json
  {
    "name": "Task1",
    "description": "Description task1",
    "status": "NEW",
    "duration": 60,
    "startTime": "2025-11-16T10:00"
  }
  ```

## Реализация сервера

### Основные классы

1. **HttpTaskServer**
  * Основной класс приложения.
  * Запускает `HttpServer` на порту `8080`.
  * Содержит метод `main()` для старта.
  * Принимает `TaskManager` через конструктор (для тестирования и продакшена).

  * Методы:
    * `start()` — запуск сервера;
    * `stop()` — остановка сервера.

2. **BaseHttpHandler**
  * Базовый класс для всех обработчиков HTTP‑запросов.
  * Содержит общие методы:
    * `sendText(HttpExchange h, String text)` — отправка JSON с кодом `200`;
    * `sendNotFound(HttpExchange h)` — отправка `404`;
    * `sendHasInteractions(HttpExchange h)` — отправка `406` (пересечение задач);
    * `sendError(HttpExchange h, String message)` — отправка `500`.


3. **Обработчики для эндпоинтов** (наследуются от `BaseHttpHandler`):
  * `TasksHandler` — `/tasks`;
  * `SubtasksHandler` — `/subtasks`;
  * `EpicsHandler` — `/epics`;
  * `HistoryHandler` — `/history`;
  * `PrioritizedHandler` — `/prioritized`.


### Зависимости
* **Gson** — для парсинга JSON (запросы/ответы).
* **HttpServer** (из `com.sun.net.httpserver`) — встроенный HTTP‑сервер Java.

## Логика обработчиков

Каждый обработчик:
1. Получает запрос, парсит JSON в объект `Task`/`Epic`/`Subtask`.
2. Вызывает соответствующий метод `TaskManager` (например, `addTask()`).
3. Обрабатывает исключения:
  * `NotFoundException` → `404`;
  * `IntersectionException` → `406`;
  * Прочие → `500`.
4. Отправляет ответ в формате JSON с нужным статусом.


## Тестирование API


### Структура тестов
* Отдельные классы для каждого эндпоинта:
  * `HttpTaskManagerTasksTest`;
  * `HttpTaskManagerSubtasksTest`;
  * `HttpTaskManagerEpicsTest`;
  * `HttpTaskManagerHistoryTest`;
  * `HttpTaskManagerPrioritizedTest`.


### Пример теста (создание задачи)

```java
@Test
public void testAddTask() throws IOException, InterruptedException {
    // Подготовка
    Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, 
        Duration.ofMinutes(5), LocalDateTime.now());
    String taskJson = gson.toJson(task);

    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks");
    HttpRequest request = HttpRequest.newBuilder()
        .uri(url)
        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
        .build();

    // Выполнение запроса
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // Проверка статуса
    assertEquals(201, response.statusCode());

    // Проверка данных в менеджере
    List<Task> tasks = manager.getTasks();
    assertNotNull(tasks);
    assertEquals(1, tasks.size());
    assertEquals("Test 2", tasks.get(0).getName());
}
```

### Общие методы для тестов
* `@BeforeEach setUp()` — очистка менеджера, запуск сервера;
* `@AfterEach shutDown()` — остановка сервера.

### Проверяемые сценарии
Для каждого эндпоинта:
1. Успешный `GET` (список, одиночный объект).
2. Успешный `POST` (создание/обновление).
3. `POST` с пересечением интервалов → `406`.
4. `DELETE` (удаление).
5. Запросы к несуществующим ресурсам → `404`.
6. Ошибки сервера → `500`.


## Требования к среде
* Java SE8+;
* JUnit5 (для тестов);
* Библиотека **Gson**;
* Доступ к порту `8080` (для сервера).