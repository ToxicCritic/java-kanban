package manager;

public class Managers {
    public static TaskManager getDefault() {
        // Здесь может быть логика выбора конкретной реализации TaskManager по умолчанию
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
