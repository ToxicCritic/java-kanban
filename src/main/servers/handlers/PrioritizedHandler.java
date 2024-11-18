package main.servers.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.managers.Managers;
import main.managers.taskManager.TaskManager;
import main.tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (method.equals("GET") && path.equals("/prioritized")) {
            handleGetPrioritizedTasks(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = (List<Task>) taskManager.getPrioritizedTasks();

        if (prioritizedTasks.isEmpty()) {
            sendText(exchange, "{\"статус\":\"Приоритетных задач нет\"}", 200);
        } else {
            sendText(exchange, new Gson().toJson(prioritizedTasks), 200);
        }
    }
}
