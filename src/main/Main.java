package main;

import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();


        // Создаем обычную задачу
        Task task1 = new Task ("Поход в магазин", "Поход в магазин за продуктами", TaskStatus.NEW);
        taskManager.createTask(task1);
        // Создаем эпик
        Epic epic1 = new Epic("Разработка приложения", "Разработка нового приложения", TaskStatus.NEW);
        taskManager.createEpic(epic1);

        // Создаем подзадачи для эпика
        Subtask subtask1 = new Subtask("Планирование", "Подготовка плана разработки", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Программирование", "Написание кода", TaskStatus.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        epic1.updateStatus();

        // Выводим информацию о подзадачах эпика
        System.out.println("\nПодзадачи эпика " + epic1.getTitle() + ": (" + epic1.getStatus() + ")");
        for (Subtask subtask : epic1.getSubtasks()) {
            System.out.println(subtask.getTitle() + " (" + subtask.getStatus() + ")");
        }

        // Обновляем статус подзадачи
        subtask2.setStatus(TaskStatus.DONE);
        epic1.updateStatus();
        System.out.println("\nСтатус эпика " + epic1.getTitle() + ": " + epic1.getStatus());

        subtask1.setStatus(TaskStatus.DONE);
        epic1.updateStatus();
        System.out.println("\nСтатус эпика " + epic1.getTitle() + ": " + epic1.getStatus());


        // Удаляем задачу по идентификатору
        taskManager.deleteSubtaskById(subtask1.getId());

        // Выводим список всех задач
        showAllTasks(taskManager);

        taskManager.deleteEpicById(epic1.getId());

        showAllTasks(taskManager);
    }

    public static void showAllTasks(TaskManager taskManager) {
        System.out.println("\nСписок всех задач:");
        ArrayList<Task> allTasks = taskManager.getAllTasks();
        for (Task task : allTasks) {
            System.out.println(task.getTitle() + " - " + task.getStatus() + " (" + task.getClass() + ") ");
        }
    }
}