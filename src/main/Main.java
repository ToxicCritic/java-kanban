package main;

import manager.InMemoryTaskManager;
import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault();
        Scanner s = new Scanner(System.in);

        System.out.println("Добро пожаловать в TaskManager!");
        int input = 0;
        while (input != 6) {
            showMenu();

            input = s.nextInt();
            switch(input) {
                case 1:
                    showAllTasks(taskManager);
                    break;
                case 2:
                    createTask(taskManager);
                    break;
                case 3:
                    createEpic(taskManager);
                    break;
                case 4:
                    createSubtask(taskManager);
                    break;
                case 5:
                    showHistory(taskManager);
                    break;
                case 6:
                    return;
            }
        }
    }

    private static void showAllTasks(InMemoryTaskManager manager) {
        System.out.println("\nЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println("ID " + task.getId() + ". " + task.getTitle() + ": " + task.getDescription()
                    + " [" + task.getStatus() + "]");
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println("ID " + epic.getId() + ". " + epic.getTitle() + ": " + epic.getDescription()
                    + " [" + epic.getStatus() + "]");
            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + "ID " + task.getId() + ". " + task.getTitle() + ": " + task.getDescription()
                        + " [" + task.getStatus() + "]");
            }
        }

    }

    private static void showHistory(InMemoryTaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task.getTitle() + " (" + task.getId() + ")");
        }
    }

    private static void showMenu() {
        System.out.println("\nВыберите действие:");
        System.out.println("1. Просмотреть все задачи");
        System.out.println("2. Добавить задачу");
        System.out.println("3. Добавить эпик");
        System.out.println("4. Добавить подзадачу");
        System.out.println("5. Обновить статус задачи");
        System.out.println("5. Просмотреть историю");
        System.out.println("6. Выход");
    }

    private static void createTask(InMemoryTaskManager taskManager) {
        Scanner s = new Scanner(System.in);

        System.out.println("Введите название задачи:");
        String title = s.nextLine();

        System.out.println("Введите описание задачи:");
        String description = s.nextLine();

        if (title.isEmpty() || description.isEmpty()) {
            System.out.println("Недопустимое название или описание!");
            return;
        }

        Task task = new Task(title, description, TaskStatus.NEW);
        taskManager.createTask(task);
        System.out.println("Задача \"" + title + "\" была успешно добавлена!");
    }

    private static void createEpic(InMemoryTaskManager taskManager) {
        Scanner s = new Scanner(System.in);

        System.out.println("Введите название эпика:");
        String title = s.nextLine();

        System.out.println("Введите описание эпика:");
        String description = s.nextLine();

        if (title.isEmpty() || description.isEmpty()) {
            System.out.println("Недопустимое название или описание!");
            return;
        }

        Epic epic = new Epic(title, description, TaskStatus.NEW);
        taskManager.createEpic(epic);
        System.out.println("Эпик \"" + title + "\" был успешно добавлена!");
    }

    private static void createSubtask(InMemoryTaskManager taskManager) {
        if (taskManager.getAllEpics().isEmpty()) {
            System.out.println("Нет эпиков, в которые можно добавить подзадачи!");
            return;
        }

        Scanner s = new Scanner(System.in);

        System.out.println("Выберите эпик, для которого нужно создать подзадачу:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getId() + ". " + epic.getTitle());
        }
        int id = Integer.parseInt(s.nextLine());

        boolean isValid = false;
        for (Epic epic : taskManager.getAllEpics()) {
            if (epic.getId() == id) {
                isValid = true;
                break;
            }
        }
        if (!isValid) return;

        System.out.println("Введите название подзадачи:");
        String title = s.nextLine();

        System.out.println("Введите описание подзадачи:");
        String description = s.nextLine();

        if (title.isEmpty() || description.isEmpty()) {
            System.out.println("Недопустимое название или описание!");
            return;
        }

        Subtask subtask = new Subtask(title, description, TaskStatus.NEW, id);
        taskManager.createSubtask(subtask);
        System.out.println("Подзадача \"" + title + "\" была успешно добавлена в эпик \"" +
                taskManager.getEpicById(id).getTitle() + "\"");
    }
}