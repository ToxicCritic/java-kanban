package main;

import main.managers.taskManager.FileBackedTaskManager;
import main.managers.Managers;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + "/src/main/", Managers.FILENAME);
        FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getDefault();

        // Создание задач
        Duration duration = Duration.ofHours(1);
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, duration, startTime);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS, duration, startTime);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создание эпика с подзадачами
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId(), duration, startTime);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1.getId(), duration.plusHours(3), startTime);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Доступ к задачам для добавления в историю
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        // Загрузка данных из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверка загрузки задач
        System.out.println("Загруженные задачи:");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЗагруженные эпики:");
        for (Epic epic : loadedManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nЗагруженные подзадачи:");
        for (Subtask subtask : loadedManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        // Проверка загрузки истории
        System.out.println("\nЗагруженная история:");
        for (Task task : loadedManager.getHistory()) {
            System.out.println(task);
        }
    }
}
