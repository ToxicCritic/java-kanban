package managers.taskManager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Метод автосохранения данных в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToCsv(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(taskToCsv(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToCsv(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    private String taskToCsv(Task task) {
        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.join(",",
                String.valueOf(task.getId()),
                task.getClass().getSimpleName().toUpperCase(),
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId
        );
    }

    private static Task taskFromCsv(String csvLine) {
        String[] fields = csvLine.split(",");

        String type = fields[1];
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        Task task = null;
        switch (type) {
            case "TASK":
                task = new Task(name, description, status);
                break;
            case "EPIC":
                task = new Epic(name, description, status);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                task = new Subtask(name, description, status, epicId);
                break;
        }

        if (task != null) {
            int id = Integer.parseInt(fields[0]);
            task.setId(id); // Устанавливаем ID через метод setId
        }

        return task;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        if (!file.exists()) return taskManager;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок

            String line;
            int maxId = 0;
            while ((line = reader.readLine()) != null) {
                Task task = taskFromCsv(line);
                if (task != null) {
                    int id = task.getId();
                    maxId = Math.max(maxId, id);

                    if (task instanceof Epic) {
                        taskManager.createEpic((Epic) task);
                    } else if (task instanceof Subtask) {
                        taskManager.createSubtask((Subtask) task);
                    } else {
                        taskManager.createTask(task);
                    }
                }
            }

            taskManager.setIdCounter(maxId + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки данных из файла", e);
        }

        return taskManager;
    }

    private void setIdCounter(int currentId) {
        this.idCounter = currentId;
    }
}