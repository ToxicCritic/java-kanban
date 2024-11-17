package main.servers;

import main.managers.Managers;
import main.managers.taskManager.TaskManager;
import main.servers.handlers.*;
import com.sun.net.httpserver.HttpServer;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager; // Общий экземпляр TaskManager

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager; // Инициализируем TaskManager через Managers
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        setupEndpoints();
    }

    private void setupEndpoints() {
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        System.out.println("Сервер запущен на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен.");
    }

    private static void initTestData(TaskManager manager) {
        Duration duration = Duration.ofHours(1);
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime startTime2 = startTime1.plusHours(2);
        LocalDateTime startTime3 = startTime1.plusHours(4);

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, duration, startTime1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS, duration, startTime2);
        Task task3 = new Task("Задача 3", "Описание задачи 3", TaskStatus.DONE, duration, startTime3);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId(), duration, startTime2.plusHours(5));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1.getId(), duration.plusHours(3), startTime3.plusHours(12));

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        initTestData(taskManager);
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}
