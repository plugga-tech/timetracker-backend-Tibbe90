package com.tibbelin.tajmtrackr.models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tibbelin.tajmtrackr.enums.TimerStatus;

/*
Själva timern körs i frontend.
Kör instant i konstruktor för att säkerställa samma tid som Date.getTime() i react
https://stackoverflow.com/questions/32437550/whats-the-difference-between-instant-and-localdatetime

kollektionen innehåller ändå timeStart för att kunna återuppta en session som blivit avslutad
*/


@Document(collection = "time_entries")
public class TimeTracker {
    

    @Id
    private String id;
    private String userId;
    private String categoryId;
    private LocalDateTime timeStart;
    private LocalDateTime timeStop;
    private Long duration;
    private TimerStatus status;
    private LocalDate creationDate;

    public TimeTracker(String userId, String categoryId, Instant timeStart) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.timeStart = LocalDateTime.ofInstant(timeStart, ZoneId.ofOffset("", ZoneOffset.UTC));
        this.status = TimerStatus.STARTED;
        this.creationDate = LocalDate.now();
        this.duration = 0L;
    }

    public TimeTracker() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public TimerStatus getStatus() {
        return status;
    }

    public void setStatus(TimerStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeStop() {
        return timeStop;
    }

    public void setTimeStop(LocalDateTime timeStop) {
        this.timeStop = timeStop;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }
}