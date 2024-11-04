package main.managers.taskManager;

import main.managers.Managers;
import main.managers.historyManager.HistoryManager;
import main.managers.historyManager.InMemoryHistoryManager;

import main.managers.taskManager.comparators.TaskStartTimeComparator;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    private final TreeSet<Task> prioritizedTasks;

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    private final InMemoryHistoryManager historyManager;
    protected int idCounter;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        this.prioritizedTasks = new TreeSet<Task>(new TaskStartTimeComparator());
        this.historyManager = Managers.getDefaultHistory();
        idCounter = 1;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void addTaskToPrioritizedList(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void updateEpicDuration(Epic epic) {
        Duration sumDuration = Duration.ZERO;
        for (int subtaskID : epic.getSubtasks()) {
            Duration subtaskDuration = getSubtaskById(subtaskID).getDuration();
            sumDuration = sumDuration.plus(subtaskDuration);
        }
        epic.setDuration(sumDuration);
    }

    protected void updateEpicEndTime(Epic epic) {
        LocalDateTime latestEndTime = null;

        for (int subtaskID : epic.getSubtasks()) {
            LocalDateTime subtaskEndTime = getSubtaskById(subtaskID).getEndTime();

            if (latestEndTime == null || subtaskEndTime.isAfter(latestEndTime)) {
                latestEndTime = subtaskEndTime;
            }
        }

        epic.setEndTime(latestEndTime);
    }

    protected void updateEpicStartTime(Epic epic) {
        LocalDateTime earliestStartTime = null;

        for (int subtaskID : epic.getSubtasks()) {
            LocalDateTime subtaskStartTime = getSubtaskById(subtaskID).getStartTime();

            if (earliestStartTime == null || subtaskStartTime.isBefore(earliestStartTime)) {
                earliestStartTime = subtaskStartTime;
            }
        }

        epic.setStartTime(earliestStartTime);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {

        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
                updateEpicStartTime(epic);
                updateEpicDuration(epic);
                updateEpicEndTime(epic);
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                if (subtasks.containsKey(subtaskId)) {
                    subtasksOfEpic.add(subtasks.get(subtaskId));
                }
            }
        }
        return subtasksOfEpic;
    }

    public void updateEpicStatus(Epic epic) {
            boolean allSubtasksNew = true;
            boolean allSubtasksDone = true;
            if (epic != null) {
                for (int id : epic.getSubtasks()) {
                    if (subtasks.get(id) != null && subtasks.get(id).getStatus() != TaskStatus.DONE) {
                        allSubtasksDone = false;
                    }
                    if (subtasks.get(id) != null && subtasks.get(id).getStatus() != TaskStatus.NEW) {
                        allSubtasksNew = false;
                    }
                }
            }
            if (allSubtasksNew) {
                assert epic != null;
                epic.setStatus(TaskStatus.NEW);
            }
            else if (allSubtasksDone) epic.setStatus(TaskStatus.DONE);
            else epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    @Override
    public void updateTask(Task updatedTask) {
        prioritizedTasks.remove(updatedTask);
        tasks.put(updatedTask.getId(), updatedTask);
        addTaskToPrioritizedList(updatedTask);
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        prioritizedTasks.remove(updatedSubtask);
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(epics.get(updatedSubtask.getEpicId()));
        updateEpicStartTime(epics.get(updatedSubtask.getEpicId()));
        updateEpicDuration(epics.get(updatedSubtask.getEpicId()));
        updateEpicEndTime(epics.get(updatedSubtask.getEpicId()));
        addTaskToPrioritizedList(updatedSubtask);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        prioritizedTasks.remove(updatedEpic);
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        addTaskToPrioritizedList(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
            updateEpicEndTime(epic);
            addTaskToPrioritizedList(subtask);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeAllTasks() {
        ArrayList<Task> tasksToDelete = new ArrayList<>((tasks.values()));
        tasks.clear();
        for (Task task : tasksToDelete) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void removeAllSubtasks() {
        ArrayList<Subtask> subtasksToDelete = new ArrayList<>(subtasks.values());
        for (Subtask subtask : subtasksToDelete) {
            int epicId = subtask.getEpicId();
            subtasks.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            Epic epic = getEpicById(epicId);
            updateEpicStatus(epic);
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
            updateEpicEndTime(epic);
        }
    }

    @Override
    public void removeAllEpics() {
        ArrayList<Epic> epicsToDelete = new ArrayList<>();
        for (Epic epic : epics.values()) {
            if (epic != null) {
                ArrayList<Subtask> subtasksOfEpic = getEpicSubtasks(epic.getId());
                for (Subtask subtask : subtasksOfEpic) {
                    subtasks.remove(subtask.getId());
                }
                epicsToDelete.add(epic);
            }
        }
        for (Epic epic : epicsToDelete) {
            epics.remove(epic.getId());
        }
    }
}