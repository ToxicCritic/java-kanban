package test.managers.taskManager;

import main.managers.taskManager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
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
        Task task = new Task("Тестовая задача", "Тестовое описание", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        Task loadedTask = loadedManager.getTaskById(task.getId());
        assertNotNull(loadedTask, "Задача не была загружена из файла");
        assertEquals(task.getTitle(), loadedTask.getTitle(), "Названия задач не совпадают");
        assertEquals(task.getDescription(), loadedTask.getDescription(), "Описания задач не совпадают");
    }

    @Test
    public void shouldSaveAndLoadEpicsAndSubtasks() {
        Epic epic = new Epic("Тестовый эпик", "Описание эпика", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Тестовая подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
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
        Task task = new Task("Тестовая задача", "Тестовое описание", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
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
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        manager.createTask(task1);
        manager.createTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(2, loadedManager.getAllTasks().size(), "Количество задач не совпадает");
    }

    @Test
    void testTaskCreationDeletionAndRecreation() throws IOException {
        // Создаем временный файл для тестов
        // Создаем менеджер задач и добавляем задачи
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(testFile);

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        manager.createTask(task1);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW, Duration.ofHours(0), LocalDateTime.now());
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        manager.createSubtask(subtask1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(5));
        manager.createTask(task2);

        // Удаляем первую задачу и добавляем новую
        manager.deleteTaskById(task1.getId());
        Task task3 = new Task("Задача 3", "Описание задачи 3", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(12));
        manager.createTask(task3);

        // Загружаем новый менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        // Проверяем количество задач и их идентификаторы
        assertEquals(2, loadedManager.getAllTasks().size(), "Количество задач должно быть 2");
        assertNotNull(loadedManager.getTaskById(task2.getId()), "Задача 2 должна присутствовать");
        assertNotNull(loadedManager.getTaskById(task3.getId()), "Задача 3 должна присутствовать");
        assertEquals(task2.getId(), loadedManager.getTaskById(task2.getId()).getId(), "Идентификатор задачи 2 должен совпадать");
        assertEquals(task3.getId(), loadedManager.getTaskById(task3.getId()).getId(), "Идентификатор задачи 3 должен совпадать");
    }
}
