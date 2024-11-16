package main;

import main.managers.taskManager.FileBackedTaskManager;
import main.managers.Managers;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File(System.getProperty("user.dir") + "/src/main/", Managers.FILENAME);
        FileBackedTaskManager manager = (FileBackedTaskManager) Managers.getDefault();

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

        System.out.println("Задачи по приоритету (сортировка по времени начала):");
        for (Task prioritizedTask : manager.getPrioritizedTasks()) {
            System.out.println(prioritizedTask);
        }

        // Загрузка данных из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверка загрузки задач
        System.out.println("\nЗагруженные задачи:");
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

        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(""); // Очищаем содержимое файла
            System.out.println("\nФайл был успешно очищен.");
        } catch (IOException e) {
            System.out.println("\nОшибка при очистке файла: " + e.getMessage());
        }
    }
}
