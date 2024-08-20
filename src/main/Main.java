package main;

import managers.taskManager.FileBackedTaskManager;
import managers.taskManager.InMemoryTaskManager;
import managers.Managers;
import managers.taskManager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;

public class Main {
        public static void main(String[] args) {
            File file = new File(Managers.FILENAME);
            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

            // Создание задач
            Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
            Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);
            manager.createTask(task1);
            manager.createTask(task2);

            // Создание эпика с подзадачами
            Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
            manager.createEpic(epic1);

            Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId());
            Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic1.getId());
            manager.createSubtask(subtask1);
            manager.createSubtask(subtask2);

            // Доступ к задачам для добавления в историю
            manager.getTaskById(task1.getId());
            manager.getEpicById(epic1.getId());
            manager.getSubtaskById(subtask1.getId());

            // Сохранение в файл
            manager.save();

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