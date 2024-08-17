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
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Закончить проект", "Сдать до конца недели", TaskStatus.NEW);
        Task task2 = new Task("Купить продукты", "Молоко, хлеб, яйца", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Переезд", "Организовать переезд на новую квартиру", TaskStatus.NEW);
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Упаковать вещи", "Собрать вещи в коробки", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Заказать грузовик", "Заказать машину для перевозки", TaskStatus.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Перевезти вещи", "Доставить вещи на новое место", TaskStatus.NEW, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Epic epic2 = new Epic("Организовать отпуск", "Планирование поездки в горы", TaskStatus.NEW);
        manager.createEpic(epic2);

        // Просматриваем задачи для добавления их в историю
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask2.getId());

        // Выводим все задачи, эпики и подзадачи
        System.out.println("Текущие задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nТекущие эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nТекущие подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        // Выводим историю просмотров
        System.out.println("\nИстория просмотра задач:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);

        System.out.println("\nОбновленные задачи и подзадачи:");
        System.out.println(manager.getTaskById(task1.getId()));
        System.out.println(manager.getSubtaskById(subtask2.getId()));
        System.out.println(manager.getEpicById(epic1.getId()));

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("\nОставшиеся задачи после удаления:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nОставшиеся эпики после удаления:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nОставшиеся подзадачи после удаления:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        // Загружаем менеджер из файла и выводим задачи для проверки
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nЗадачи после загрузки из файла:");
        for (Task task : loadedManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики после загрузки из файла:");
        for (Epic epic : loadedManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nПодзадачи после загрузки из файла:");
        for (Subtask subtask : loadedManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория просмотра задач после загрузки:");
        for (Task task : loadedManager.getHistory()) {
            System.out.println(task);
        }
    }
}