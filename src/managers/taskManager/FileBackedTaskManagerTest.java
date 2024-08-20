package managers.taskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        manager = FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        manager.createTask(task);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(1, tasks.size(), "Должна быть загружена одна задача");
        assertEquals(task.getTitle(), tasks.get(0).getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), tasks.get(0).getDescription(), "Описание задачи должно совпадать");
        assertEquals(task.getStatus(), tasks.get(0).getStatus(), "Статус задачи должен совпадать");
    }

    @Test
    void shouldSaveAndLoadEpicsAndSubtasks() {
        Epic epic = new Epic("Эпик", "Описание эпика", TaskStatus.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Epic> epics = loadedManager.getAllEpics();
        List<Subtask> subtasks = loadedManager.getAllSubtasks();

        assertEquals(1, epics.size(), "Должен быть загружен один эпик");
        assertEquals(epic.getTitle(), epics.get(0).getTitle(), "Название эпика должно совпадать");

        assertEquals(1, subtasks.size(), "Должна быть загружена одна подзадача");
        assertEquals(subtask.getTitle(), subtasks.get(0).getTitle(), "Название подзадачи должно совпадать");
        assertEquals(subtask.getEpicId(), subtasks.get(0).getEpicId(), "ID эпика подзадачи должен совпадать");
    }

    @Test
    void shouldMaintainHistoryAfterLoad() {
        Task task1 = new Task("Первая задача с историей", "Описание первой задачи", TaskStatus.NEW);
        Task task2 = new Task("Вторая задача с историей", "Описание второй задачи", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> history = loadedManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две записи после загрузки");
        assertEquals(task1.getId(), history.get(0).getId(), "ID первой задачи в истории должен совпадать");
        assertEquals(task2.getId(), history.get(1).getId(), "ID второй задачи в истории должен совпадать");
    }

    @Test
    void shouldSaveAndLoadTaskWithHistory() {
        Task task = new Task("Задача с историей", "Описание задачи", TaskStatus.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId()); // Добавляем в историю

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> history = loadedManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну запись после загрузки");
        assertEquals(task.getId(), history.get(0).getId(), "ID задачи в истории должен совпадать");
    }

    @Test
    void shouldHandleSavingAndLoadingMultipleTasks() {
        Task task1 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Task task2 = new Task("Вторая задача", "Описание второй задачи", TaskStatus.IN_PROGRESS);
        Epic epic = new Epic("Эпик", "Описание эпика", TaskStatus.NEW);
        Subtask subtask = new Subtask("Подзадача эпика", "Описание подзадачи", TaskStatus.DONE, epic.getId());

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loadedManager.getAllTasks().size(), "Должны загрузиться две задачи");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен загрузиться один эпик");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Должна загрузиться одна подзадача");
    }
}