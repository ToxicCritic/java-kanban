package main.servers.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.managers.Managers;
import main.managers.taskManager.TaskManager;
import main.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (method.equals("GET") && path.equals("/history")) {
            handleGetHistory(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = (List<Task>) taskManager.getHistory();
        if (history.isEmpty()) {
            sendText(exchange, "{\"статус\":\"История пуста\"}", 200);
        } else {
            sendText(exchange, new Gson().toJson(history), 200);
        }
    }
}
