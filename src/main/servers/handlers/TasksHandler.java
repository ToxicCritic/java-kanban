package main.servers.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sun.net.httpserver.HttpExchange;

import main.managers.taskManager.TaskManager;
import main.servers.adapters.DurationAdapter;
import main.servers.adapters.LocalDateTimeAdapter;
import main.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetRequest(exchange, path);
                break;
            case "POST":
                handlePostRequest(exchange, path);
                break;
            case "DELETE":
                handleDeleteRequest(exchange, path);
                break;
            default:
                sendError(exchange, "Метод HTTP не поддерживается: " + method);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
        } else if (path.startsWith("/tasks/")) {
            String idString = path.substring("/tasks/".length());
            try {
                int taskId = Integer.parseInt(idString);
                Task task = taskManager.getTaskById(taskId);
                if (task == null) {
                    sendNotFound(exchange);
                } else {
                    sendText(exchange, new Gson().toJson(task), 200);
                }
            } catch (NumberFormatException e) {
                sendError(exchange, "Некорректный формат ID задачи.");
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(requestBody, Task.class);

            if (task == null || task.getTitle() == null || task.getDescription() == null
                    || task.getStartTime() == null || task.getDuration() == null) {
                sendError(exchange, "Некорректные данные задачи. Проверьте обязательные поля.");
                return;
            }

            if (path.equals("/tasks")) {
                try {
                    taskManager.createTask(task);
                    sendText(exchange, "{\"статус\":\"Задача успешно создана\"}", 201);
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            } else if (path.startsWith("/tasks/")) {
                String idString = path.substring("/tasks/".length());
                try {
                    int taskId = Integer.parseInt(idString);
                    task.setId(taskId);
                    taskManager.updateTask(task);
                    sendText(exchange, "{\"статус\":\"Задача успешно обновлена\"}", 200);
                } catch (NumberFormatException e) {
                    sendError(exchange, "Некорректный формат ID задачи.");
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendError(exchange, "Ошибка при обработке запроса: " + e.getMessage());
        }
    }


    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/tasks/")) {
            String idString = path.substring("/tasks/".length());
            try {
                int taskId = Integer.parseInt(idString);

                boolean taskExists = taskManager.getAllTasks().stream()
                        .anyMatch(task -> task.getId() == taskId);

                if (!taskExists) {
                    sendNotFound(exchange);
                    return;
                }

                taskManager.deleteTaskById(taskId);
                sendText(exchange, "{\"статус\":\"Задача успешно удалена\"}", 200);

            } catch (NumberFormatException e) {
                sendError(exchange, "Некорректный формат ID задачи.");
            } catch (Exception e) {
                sendError(exchange, "Произошла ошибка при удалении задачи.");
            }
        } else {
            sendNotFound(exchange);
        }
    }

}
