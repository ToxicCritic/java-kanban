package main.managers.taskManager;

import main.managers.exceptions.ManagerSaveException;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TaskStatus;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FileBackedTaskManager(File file) {
        this.file = file;
    }



    // Метод автосохранения данных в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Записываем заголовок
            writer.write("id,type,name,description,status,epicId\n");

            // Записываем задачи
            for (Task task : getAllTasks()) {
                writer.write(taskToCsvString(task));
                writer.newLine();
            }

            // Записываем эпики
            for (Epic epic : getAllEpics()) {
                writer.write(taskToCsvString(epic));
                writer.newLine();
            }

            // Записываем подзадачи
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToCsvString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("id") && !line.isEmpty()) {
                    Task task = taskFromString(line);
                    switch (getTaskType(line)) {
                        case TASK:
                            manager.createTask(task);
                            break;
                        case EPIC:
                            manager.createEpic((Epic) task);
                            break;
                        case SUBTASK:
                            manager.createSubtask((Subtask) task);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
        return manager;
    }


    private static TaskTypes getTaskType(String line) {
        String[] fields = line.split(",");
        return TaskTypes.valueOf(fields[1]);
    }

    private String taskToCsvString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                task.getId(),
                TaskTypes.TASK,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration().getSeconds(),
                task.getStartTime().format(dateTimeFormatter));
    }

    // Для эпика
    private String taskToCsvString(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                epic.getId(),
                TaskTypes.EPIC,
                epic.getTitle(),
                epic.getDescription(),
                epic.getStatus(),
                epic.getDuration().getSeconds(),
                epic.getStartTime().format(dateTimeFormatter));
    }

    // Для подзадачи
    private String taskToCsvString(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%d",
                subtask.getId(),
                TaskTypes.SUBTASK,
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getDuration().getSeconds(),
                subtask.getStartTime().format(dateTimeFormatter),
                subtask.getEpicId());
    }

    private static Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskTypes type = TaskTypes.valueOf(fields[1]);
        String name = fields[2];
        String description = fields[3];
        TaskStatus status = TaskStatus.valueOf(fields[4]);
        Duration duration = Duration.ofSeconds(Integer.parseInt(fields[5]));
        LocalDateTime startTime = LocalDateTime.parse(fields[6], dateTimeFormatter);

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status, duration, startTime);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
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

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
}