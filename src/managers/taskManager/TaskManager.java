package managers.taskManager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.Collection;

public interface TaskManager {
    Collection<Task> getHistory(); // Не очень понял зачем, ведь метод в HistoryManager статический

    void createTask(Task task);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    Collection<Subtask> getEpicSubtasks(int id);

    void updateTask(Task updatedTask);

    void updateSubtask(Subtask updatedSubtask);

    void updateEpic(Epic updatedEpic);

    Collection<Task> getAllTasks();

    Collection<Subtask> getAllSubtasks();

    Collection<Epic> getAllEpics();

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();
}
