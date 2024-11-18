package main.servers.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import main.managers.Managers;
import main.managers.taskManager.TaskManager;
import main.servers.adapters.DurationAdapter;
import main.servers.adapters.LocalDateTimeAdapter;
import main.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SubtasksHandler(TaskManager taskManager) {
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
                sendError(exchange, "Метод не поддерживается.");
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/subtasks/")) {
            String idString = path.substring("/subtasks/".length());
            try {
                int subtaskId = Integer.parseInt(idString);
                Subtask subtask = taskManager.getSubtaskById(subtaskId);
                if (subtask == null) {
                    sendNotFound(exchange);
                    return;
                }
                sendText(exchange, new Gson().toJson(subtask), 200);
            } catch (NumberFormatException e) {
                sendError(exchange, "Некорректный формат ID подзадачи.");
            }
        } else if (path.equals("/subtasks")) {
            sendText(exchange, new Gson().toJson(taskManager.getAllSubtasks()), 200);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

            if (subtask == null || subtask.getTitle() == null || subtask.getDescription() == null
                    || subtask.getStartTime() == null || subtask.getDuration() == null) {
                sendError(exchange, "Некорректные данные подзадачи. Проверьте обязательные поля.");
                return;
            }

            if (path.equals("/subtasks")) {
                try {
                    taskManager.createSubtask(subtask);
                    sendText(exchange, "{\"статус\":\"Подзадача успешно создана\"}", 201);
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            } else if (path.startsWith("/subtasks/")) {
                String idString = path.substring("/subtasks/".length());
                try {
                    int subtaskId = Integer.parseInt(idString);
                    subtask.setId(subtaskId);
                    taskManager.updateSubtask(subtask);
                    sendText(exchange, "{\"статус\":\"Подзадача успешно обновлена\"}", 200);
                } catch (NumberFormatException e) {
                    sendError(exchange, "Некорректный формат ID подзадачи.");
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
        if (path.startsWith("/subtasks/")) {
            String idString = path.substring("/subtasks/".length());
            try {
                int subtaskId = Integer.parseInt(idString);

                boolean subtaskExists = taskManager.getAllSubtasks().stream()
                        .anyMatch(subtask -> subtask.getId() == subtaskId);

                if (!subtaskExists) {
                    sendNotFound(exchange);
                    return;
                }

                taskManager.deleteSubtaskById(subtaskId);
                sendText(exchange, "{\"статус\":\"Подзадача успешно удалена\"}", 200);
            } catch (NumberFormatException e) {
                sendError(exchange, "Некорректный формат ID подзадачи.");
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
