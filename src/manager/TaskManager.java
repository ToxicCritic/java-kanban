package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;

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

    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.remove(id);
            if (task instanceof Subtask) {
                deleteSubtaskById(id);
            } else if (task instanceof Epic) {
                deleteEpicById(id);
            }
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            // Удаляем подзадачу из списка подзадач эпика, если это подзадача эпика
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.updateStatus();
            }
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            // Удаляем все подзадачи эпика из списка подзадач
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
        if (updatedTask instanceof Subtask) {
            // При обновлении подзадачи обновляем статус соответствующего эпика
            Subtask subtask = (Subtask) updatedTask;
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
            }
        }
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allTasks.add(epic);
            allTasks.addAll(epic.getSubtasks());
        }
        for (Task task : tasks.values()) {
            allTasks.add(task);
        }
        return allTasks;
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }
    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }
}