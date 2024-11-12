package test.managers.taskManager;

import main.managers.taskManager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;


public class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createSubtaskShouldUpdateEpicDurationAndStartTime() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);

        LocalDateTime startTime1 = LocalDateTime.of(2023, 10, 1, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 10, 2, 12, 0);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), startTime1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), startTime2);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(startTime1, epic.getStartTime());
        assertEquals(Duration.ofHours(3), epic.getDuration());
    }

    @Test
    void updateSubtaskShouldUpdateEpicDurationAndStartTime() {
         Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);

        LocalDateTime startTime1 = LocalDateTime.of(2023, 10, 1, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 10, 2, 12, 0);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(2), startTime1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), startTime2);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        subtask1.setStartTime(startTime2);
        subtask1.setDuration(Duration.ofHours(3));
        taskManager.updateSubtask(subtask1);

        assertEquals(startTime2, epic.getStartTime());
        assertEquals(Duration.ofHours(4), epic.getDuration());
    }

    @Test
    void utilityManagersInitialized() {
        assertNotNull(taskManager.getHistoryManager()); // Убеждаемся, что HistoryManager проинициализирован
    }

    @Test
    void taskManagerAddsDifferentTaskTypes() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        Subtask subtask = new Subtask("Тестовая Подзадача", "Описание подзадачи", TaskStatus.NEW, 1, Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        taskManager.createTask(task);
        taskManager.createSubtask(subtask);
        taskManager.createEpic(epic);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void taskIdConflictWithGeneratedIds() {
        Task task1 = new Task("Тестовая Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task("Тестовая Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
    }

    @Test
    void taskImmutableAfterAddingToManager() {
        Task originalTask = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(originalTask);
        assertEquals(originalTask.getTitle(), taskManager.getTaskById(originalTask.getId()).getTitle());
    }

    @Test
    void tasksAddedToHistoryManager() {
        Task task = new Task("Тестовая Задача", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        assertTrue(taskManager.getHistoryManager().getHistory().contains(task)); // Убеждаемся, что задача добавлена в историю
    }

    @Test
    void updateEpicStatus() {
        Epic epic = new Epic("Тестовый Эпик", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic.getId(), Duration.ofHours(2), LocalDateTime.now().plusHours(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateEpicStatus(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSetEpicStatusToNewIfAllSubtasksAreNew() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи со статусом NEW");
    }

    @Test
    void shouldSetEpicStatusToDoneIfAllSubtasksAreDone() {
        Epic epic = new Epic("Эпик 2", "Описание эпика 2", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи со статусом DONE");
    }

    @Test
    void shouldSetEpicStatusToInProgressIfSubtasksAreMixedNewAndDone() {
        Epic epic = new Epic("Эпик 3", "Описание эпика 3", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют статусы NEW и DONE");
    }

    @Test
    void shouldSetEpicStatusToInProgressIfAllSubtasksAreInProgress() {
        Epic epic = new Epic("Эпик 4", "Описание эпика 4", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusHours(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если все подзадачи имеют статус IN_PROGRESS");
    }

    @Test
    void shouldThrowExceptionWhenCreatingOverlappingSubtasks() {
        Epic epic = new Epic("Эпик для проверки пересечений", "Описание эпика", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача без пересечений", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubtask(subtask);

        Subtask overlappingSubtask = new Subtask("Пересекающаяся подзадача", "Описание подзадачи", TaskStatus.NEW, epic.getId(), Duration.ofHours(1), LocalDateTime.now().plusMinutes(30));
        
        assertThrows(IllegalArgumentException.class, () -> taskManager.createSubtask(overlappingSubtask),
                "Создание подзадачи с пересечением по времени должно вызвать IllegalArgumentException.");
    }


    @Test
    void shouldUpdateEpicStatusWhenSubtaskStatusChanges() {
        Epic epic = new Epic("Эпик 5", "Описание эпика 5", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic.getId() , Duration.ofHours(1), LocalDateTime.now());
        taskManager.createSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен обновляться при изменении статуса подзадачи");
    }

    // Тесты на пересечение интервалов
    @Test
    void shouldNotAllowOverlappingTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task1);

        Task overlappingTask = new Task("Задача 2", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now().plusMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(overlappingTask),
                "Создание задачи с пересекающимся временем должно вызывать исключение");
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание задачи", TaskStatus.NEW, Duration.ofHours(1),  LocalDateTime.now().plusHours(1).plusMinutes(1));

        assertDoesNotThrow(() -> {
            taskManager.createTask(task1);
            taskManager.createTask(task2);
        }, "Создание задач с непересекающимся временем не должно вызывать исключение");
    }
}
