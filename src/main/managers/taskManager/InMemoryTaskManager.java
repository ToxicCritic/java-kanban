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
import java.util.stream.Collectors;

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
        if (task.getStartTime() != null && task.getEndTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void removeTaskFromPrioritizedList(Task task) {
        prioritizedTasks.remove(task);
    }

    protected void isTimeOverlap(Task newTask) {
        prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() != newTask.getId())
                .filter(existingTask -> isTimeOverlap(newTask, existingTask))
                .findFirst()
                .ifPresent(overlappingTask -> {
                    throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
                });
    }

    protected boolean isTimeOverlap(Task task1, Task task2) {
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(task2.getEndTime()));
    }

    protected void updateEpicDuration(Epic epic) {
        Duration sumDuration = epic.getSubtasks().stream()
                .map(this::getSubtaskById)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(sumDuration);
    }

    protected void updateEpicEndTime(Epic epic) {
        LocalDateTime latestEndTime = epic.getSubtasks().stream()
                .map(this::getSubtaskById)
                .map(Subtask::getEndTime)
                .filter(endTime -> endTime != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setEndTime(latestEndTime);
    }

    protected void updateEpicStartTime(Epic epic) {
        LocalDateTime earliestStartTime = epic.getSubtasks().stream()
                .map(this::getSubtaskById)
                .map(Subtask::getStartTime)
                .filter(startTime -> startTime != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

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
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicData(epic);
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

        return epic == null ? new ArrayList<>() :
                epic.getSubtasks().stream()
                        .map(subtasks::get)
                        .filter(subtask -> subtask != null)
                        .collect(Collectors.toCollection(ArrayList::new));
    }


    public void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        boolean allNew = epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(subtask -> subtask != null)
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);

        boolean allDone = epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(subtask -> subtask != null)
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }


    @Override
    public void updateTask(Task updatedTask) {
        isTimeOverlap(updatedTask);
        prioritizedTasks.remove(updatedTask);
        tasks.put(updatedTask.getId(), updatedTask);
        addTaskToPrioritizedList(updatedTask);

    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        isTimeOverlap(updatedSubtask);
        prioritizedTasks.remove(updatedSubtask);
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicData(epics.get(updatedSubtask.getEpicId()));
        addTaskToPrioritizedList(updatedSubtask);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
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
        isTimeOverlap(task);
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        addTaskToPrioritizedList(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        isTimeOverlap(subtask);
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicData(epic);
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
        tasks.values().stream()
                .forEach(task -> {
                    tasks.remove(task.getId());
                    historyManager.remove(task.getId());
                    prioritizedTasks.remove(task);});
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().stream().forEach(subtask -> {
            int epicId = subtask.getEpicId();
            subtasks.remove(subtask.getId());
            prioritizedTasks.remove(subtask);

            Epic epic = getEpicById(epicId);
            updateEpicData(epic);
        });
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.values().stream()
                        .forEach(epic -> {getEpicSubtasks(epic.getId()).stream()
                            .forEach(subtask -> {
                                subtasks.remove(subtask.getId());
                                historyManager.remove(subtask.getId());
                                prioritizedTasks.remove(subtask);
                            });
        });
        epics.clear();
    }

    protected void updateEpicData(Epic epic) {
        if (epic != null) {
            updateEpicStatus(epic);
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
            updateEpicEndTime(epic);
        }
    }
}