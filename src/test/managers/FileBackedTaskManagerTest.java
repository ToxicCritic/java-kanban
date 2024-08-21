package test.managers;

import main.managers.taskManager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File testFile;

    @BeforeEach
    public void setUp() throws IOException {
        testFile = File.createTempFile("taskManagerTest", ".csv");
        manager = FileBackedTaskManager.loadFromFile(testFile);
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        Task loadedTask = loadedManager.getTaskById(task.getId());
        assertNotNull(loadedTask, "Задача не была загружена из файла");
        assertEquals(task.getTitle(), loadedTask.getTitle(), "Названия задач не совпадают");
        assertEquals(task.getDescription(), loadedTask.getDescription(), "Описания задач не совпадают");
    }

    @Test
    public void shouldSaveAndLoadEpicsAndSubtasks() {
        Epic epic = new Epic("Test Epic", "Epic Description", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        assertNotNull(loadedEpic, "Эпик не был загружен из файла");
        assertEquals(epic.getTitle(), loadedEpic.getTitle(), "Названия эпиков не совпадают");

        Subtask loadedSubtask = loadedManager.getSubtaskById(subtask.getId());
        assertNotNull(loadedSubtask, "Подзадача не была загружена из файла");
        assertEquals(subtask.getTitle(), loadedSubtask.getTitle(), "Названия подзадач не совпадают");
        assertEquals(epic.getId(), loadedSubtask.getEpicId(), "ID эпика у подзадачи не совпадают");
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        List<Task> history = loadedManager.getHistory();
        assertTrue(history.isEmpty(), "История задач должна быть пустой");
    }

    @Test
    public void shouldHandleEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пуст");
        assertTrue(loadedManager.getHistory().isEmpty(), "История задач должна быть пустой");
    }

    @Test
    public void shouldSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(2, loadedManager.getAllTasks().size(), "Количество задач не совпадает");
    }
}
