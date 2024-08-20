package managers.taskManager;

import managers.exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }



    // Метод автосохранения данных в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Заголовок файла
            writer.write("id,type,name,status,description,epic\n");

            // Сохранение задач
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }

            // Сохранение эпиков
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
            }

            // Сохранение подзадач
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }

            // Сохранение истории
            writer.write("\n");
            writer.write(historyToString());

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    // История начинается после пустой строки
                    String historyLine = reader.readLine();
                    if (historyLine != null) {
                        List<Integer> historyIds = historyFromString(historyLine);
                        for (int id : historyIds) {
                            Task task = manager.getTaskById(id);
                            if (task != null) {
                                manager.getHistoryManager().add(task);
                            }
                        }
                    }
                } else if (!line.startsWith("id")) {
                    Task task = taskFromString(line);
                    task.addToManager(manager); // Полиморфный вызов метода
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
        return manager;
    }



    private String taskToString(Task task) {
        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.format("%d,%s,%s,%s,%s,%s\n",
                task.getId(),
                task.getClass().getSimpleName().toUpperCase(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId
        );
    }

    private static Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskTypes type = TaskTypes.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    private String historyToString() {
        return getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        String[] fields = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String field : fields) {
            history.add(Integer.parseInt(field));
        }
        return history;
    }
}