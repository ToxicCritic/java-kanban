package managers.taskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;


public class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createTask() {
        Task task1 = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        Task task2 = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertNotEquals(task1.getId(), task2.getId()); // Убеждаемся, что id разные
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
        assertEquals(task2, taskManager.getTaskById(task2.getId()));
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertNotEquals(subtask1.getId(), subtask2.getId()); // Убеждаемся, что id разные
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
        assertEquals(subtask2, taskManager.getSubtaskById(subtask2.getId()));
    }

    @Test
    void createEpic() {
        Epic epic1 = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        Epic epic2 = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertNotEquals(epic1.getId(), epic2.getId()); // Убеждаемся, что id разные
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()));
        assertEquals(epic2, taskManager.getEpicById(epic2.getId()));
    }

    @Test
    void utilityManagersInitialized() {
        assertNotNull(taskManager.getHistoryManager()); // Убеждаемся, что HistoryManager проинициализирован
    }

    @Test
    void taskManagerAddsDifferentTaskTypes() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        Subtask subtask = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, 1);
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask);
        taskManager.createEpic(epic);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void taskIdConflictWithGeneratedIds() {
        Task task1 = new Task("Тестовая Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Тестовая Задача 2", "Описание задачи 2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
    }

    @Test
    void taskImmutableAfterAddingToManager() {
        Task originalTask = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(originalTask);
        assertEquals(originalTask.getTitle(), taskManager.getTaskById(originalTask.getId()).getTitle());
    }

    @Test
    void tasksAddedToHistoryManager() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        assertTrue(taskManager.getHistoryManager().getHistory().contains(task)); // Убеждаемся, что задача добавлена в историю
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertFalse(epic.getSubtasks().contains(subtask.getId()));
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void getAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика", TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        assertEquals(2, taskManager.getAllEpics().size());
    }

    @Test
    void getAllSubtasks() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(2, taskManager.getAllSubtasks().size());
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getEpicSubtasks() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        ArrayList<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void getTaskById() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void updateTask() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW);
        taskManager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void updateEpicStatus() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpicStatus(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
