package test.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.managers.taskManager.InMemoryTaskManager;
import main.servers.HttpTaskServer;
import main.servers.adapters.DurationAdapter;
import main.servers.adapters.LocalDateTimeAdapter;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("успешно создана"));
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals("Test Task", taskManager.getAllTasks().get(0).getTitle());
    }

    @Test
    void shouldReturnAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.DONE, Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task to Delete", "Description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);
        int taskId = taskManager.getAllTasks().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно удалена"));
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Description", TaskStatus.NEW, Duration.ofHours(2), LocalDateTime.now());
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("успешно создан"));
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Retrieve", "Description", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic to Retrieve", retrievedEpic.getTitle());
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for Subtask", "Description", TaskStatus.NEW, Duration.ofHours(2), LocalDateTime.now());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        Subtask subtask = new Subtask("Test Subtask", "Description", TaskStatus.NEW, epicId, Duration.ofMinutes(30), LocalDateTime.now());
        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("успешно создана"));
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for Subtask Deletion", "Description", TaskStatus.NEW, Duration.ofHours(2), LocalDateTime.now());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        Subtask subtask = new Subtask("Subtask to Delete", "Description", TaskStatus.NEW, epicId, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        int subtaskId = taskManager.getAllSubtasks().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно удалена"));
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        // Создаём и добавляем новую задачу
        Task task = new Task("Original Task", "Original Description", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.createTask(task);
        int taskId = taskManager.getAllTasks().get(0).getId();

        // Обновляем данные задачи
        Task updatedTask = new Task("Updated Task", "Updated Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(90), LocalDateTime.now().plusDays(1));
        updatedTask.setId(taskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updatedTask)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что данные обновлены
        Task retrievedTask = taskManager.getTaskById(taskId);
        assertNotNull(retrievedTask);
        assertEquals("Updated Task", retrievedTask.getTitle());
        assertEquals("Updated Description", retrievedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, retrievedTask.getStatus());
        assertEquals(Duration.ofMinutes(90), retrievedTask.getDuration());
    }

    @Test
    void shouldReturnEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Retrieve", "Description", TaskStatus.NEW, Duration.ofHours(2), LocalDateTime.now());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Epic to Retrieve", retrievedEpic.getTitle());
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for Subtask Update", "Description", TaskStatus.NEW, Duration.ofHours(2), LocalDateTime.now());
        taskManager.createEpic(epic);
        int epicId = taskManager.getAllEpics().get(0).getId();

        Subtask subtask = new Subtask("Old Subtask", "Old Description", TaskStatus.NEW, epicId, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createSubtask(subtask);
        int subtaskId = taskManager.getAllSubtasks().get(0).getId();

        Subtask updatedSubtask = new Subtask("Updated Subtask", "Updated Description", TaskStatus.DONE,
                epicId, Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));
        updatedSubtask.setId(subtaskId);
        String json = gson.toJson(updatedSubtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("успешно обновлена"));

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtaskId);
        assertEquals("Updated Subtask", retrievedSubtask.getTitle());
    }

    @Test
    void shouldReturn404ForInvalidTaskId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

}
