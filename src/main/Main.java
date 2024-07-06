package main;

import manager.InMemoryTaskManager;
import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;


public class Main {
    public static void main(String[] args) {

        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();


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

        showAllTasks(taskManager);
        showHistory(taskManager);

        // Обновляем статус подзадачи
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        System.out.println("\nСтатус эпика " + epic1.getTitle() + ": " + epic1.getStatus());

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println("Статус эпика " + epic1.getTitle() + ": " + epic1.getStatus());

        // Удаляем задачу по идентификатору
        taskManager.deleteSubtaskById(subtask1.getId());
        System.out.println("\nПодзадача " + subtask1.getTitle() + " удалена из эпика " +
                taskManager.getEpicById(subtask1.getEpicId()).getTitle() + "!");

        // Выводим список всех задач
        showAllTasks(taskManager);

        taskManager.deleteEpicById(epic1.getId());
        System.out.println("Эпик " + epic1.getTitle() + " был удален!");

        showAllTasks(taskManager);
    }

    private static void showAllTasks(InMemoryTaskManager manager) {
        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getTitle());
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic.getTitle());

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask.getTitle());
        }
    }

    private static void showHistory(InMemoryTaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task.getTitle() + " (" + task.getId() + ")");
        }
    }
}