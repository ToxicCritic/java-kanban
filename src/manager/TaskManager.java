package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private int idCounter;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        idCounter = 1;
    }

    public void createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            // Удаляем подзадачу из списка подзадач эпика, если это подзадача эпика
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
            }
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            // Удаляем все подзадачи эпика из списка подзадач
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
    }
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

    protected void updateEpicStatus(Epic epic) {
            boolean allSubtasksNew = true;
            boolean allSubtasksDone = true;
            for (int id : epic.getSubtasks()) {
                if (subtasks.get(id).getStatus() != TaskStatus.DONE) {
                    allSubtasksDone = false;
                }
                if (subtasks.get(id).getStatus() != TaskStatus.NEW) {
                    allSubtasksNew = false;
                }
            }
            if (allSubtasksNew) epic.setStatus(TaskStatus.NEW);
            else if (allSubtasksDone) epic.setStatus(TaskStatus.DONE);
            else epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void updateSubtask(Subtask updatedSubtask) {
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(epics.get(updatedSubtask.getEpicId()));
    }

    public void updateEpic(Epic updatedEpic) {
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList(epics.values());
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int epicId = subtask.getEpicId();
            subtasks.remove(subtask);
            updateEpicStatus(getEpicById(epicId));
        }
    }

    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            if (epic != null) {
                ArrayList<Subtask> subtasksOfEpic = getEpicSubtasks(epic.getId());
                for (Subtask subtask : subtasksOfEpic) {
                    subtasks.remove(subtask);
                }
                epics.remove(epic);
            }
        }
    }
}