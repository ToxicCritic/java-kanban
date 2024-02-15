import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // Создаем задачи
        Task task1 = new Task(1, "Новая задача", "Описание новой задачи", TaskStatus.NEW);
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", TaskStatus.IN_PROGRESS, 1);
        Epic epic1 = new Epic(3, "Эпик 1", "Описание эпика 1", TaskStatus.NEW);

        // Добавляем подзадачу в эпик
        epic1.addSubtask(subtask1.getId());

        // Добавляем задачи в менеджер
        taskManager.createTask(task1);
        taskManager.createTask(subtask1);
        taskManager.createTask(epic1);

        // Получаем список всех задач
        ArrayList<Task> allTasks = taskManager.getAllTasks();
        System.out.println("Список всех задач:");
        for (Task task : allTasks) {
            System.out.println(task.getTitle());
        }

        // Получаем список всех подзадач определённого эпика
        ArrayList<Integer> subtasksOfEpic = taskManager.getAllSubtasksOfEpic(epic1.getId());
        System.out.println("\nПодзадачи эпика " + epic1.getTitle() + ":");
        for (Integer subtaskId : subtasksOfEpic) {
            System.out.println(taskManager.getTaskById(subtaskId).getTitle());
        }
    }
}
