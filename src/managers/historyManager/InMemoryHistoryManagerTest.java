package managers.historyManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        task3 = new Task("Task 3", "Description 3", TaskStatus.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @Test
    void addTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
    }

    @Test
    void addSameTaskTwice() {
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
    }

    @Test
    void addMultipleTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertTrue(history.contains(task3));
    }

    @Test
    void removeTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task3));
    }

    @Test
    void removeTaskNotInHistory() {
        historyManager.add(task1);
        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
    }

    @Test
    void getHistoryEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistoryAfterAddingAndRemovingAllTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void addNullTask() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
}
