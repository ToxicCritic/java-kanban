package main.servers.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.managers.taskManager.TaskManager;
import main.servers.adapters.DurationAdapter;
import main.servers.adapters.LocalDateTimeAdapter;
import main.tasks.Epic;
import main.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicsHandler(TaskManager taskManager) {
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
        if (path.startsWith("/epics/") && path.endsWith("/subtasks")) {
            handleGetEpicSubtasks(exchange, path);
        } else if (path.startsWith("/epics/")) {
            handleGetEpicById(exchange, path);
        } else if (path.equals("/epics")) {
            handleGetAllEpics(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        String idString = path.substring("/epics/".length(), path.lastIndexOf("/subtasks"));
        try {
            int epicId = Integer.parseInt(idString);
            List<Subtask> subtasks = (List<Subtask>) taskManager.getEpicSubtasks(epicId);
            if (subtasks == null || subtasks.isEmpty()) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(subtasks), 200);
        } catch (NumberFormatException e) {
            sendError(exchange, "Некорректный формат ID эпика.");
        }
    }

    private void handleGetEpicById(HttpExchange exchange, String path) throws IOException {
        String idString = path.substring("/epics/".length());
        try {
            int epicId = Integer.parseInt(idString);
            Epic epic = taskManager.getEpicById(epicId);
            if (epic == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(epic), 200);
        } catch (NumberFormatException e) {
            sendError(exchange, "Некорректный формат ID эпика.");
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(requestBody, Epic.class);

            if (epic == null || epic.getTitle() == null || epic.getDescription() == null) {
                sendError(exchange, "Некорректные данные для создания или обновления эпика.");
                return;
            }

            if (path.equals("/epics")) {
                taskManager.createEpic(epic);
                sendText(exchange, "{\"статус\":\"Эпик успешно создан\"}", 201);
            } else if (path.startsWith("/epics/")) {
                String idString = path.substring("/epics/".length());
                try {
                    int epicId = Integer.parseInt(idString);
                    epic.setId(epicId);
                    taskManager.updateEpic(epic);
                    sendText(exchange, "{\"статус\":\"Эпик успешно обновлён\"}", 200);
                } catch (NumberFormatException e) {
                    sendError(exchange, "Некорректный формат ID эпика.");
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendError(exchange, "Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/epics/")) {
            String idString = path.substring("/epics/".length());
            try {
                int epicId = Integer.parseInt(idString);
                if (taskManager.getEpicById(epicId) == null) {
                    sendNotFound(exchange);
                    return;
                }
                taskManager.deleteEpicById(epicId);
                sendText(exchange, "{\"статус\":\"Эпик успешно удалён\"}", 200);
            } catch (NumberFormatException e) {
                sendError(exchange, "Некорректный формат ID эпика.");
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
