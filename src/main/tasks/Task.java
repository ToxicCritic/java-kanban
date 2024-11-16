package main.tasks;
import main.managers.taskManager.FileBackedTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ZERO;
        this.startTime = null;
    }

    public Task(String title, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStartTime = (startTime != null) ? startTime.format(formatter) : "Не задано";
        String formattedDuration = formatDuration(duration);
        String formattedEndTime = (getEndTime() != null) ? getEndTime().format(formatter) : "Не задано";

        return String.format("ID: %d | Название: %s | Статус: %s | Описание: %s | Продолжительность: %s" +
                        " | Время начала: %s  | Время завершения: %s",
                id, title, status, description, formattedDuration, formattedStartTime, formattedEndTime);
    }

    private String formatDuration(Duration duration) {
        if (duration == null) {
            return "Не задано";
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%d ч. %d мин.", hours, minutes);
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}