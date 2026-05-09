package com.tibbelin.tajmtrackr.Services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tibbelin.tajmtrackr.dto.CategoryHistoryDTO;
import com.tibbelin.tajmtrackr.dto.UpdateTimeTrackerDTO;
import com.tibbelin.tajmtrackr.enums.TimerStatus;
import com.tibbelin.tajmtrackr.models.Category;
import com.tibbelin.tajmtrackr.models.TimeTracker;
import com.tibbelin.tajmtrackr.models.User;

/*
https://www.mongodb.com/docs/drivers/java/sync/current/crud/update-documents/
*/

@Service
public class TimeService {
    
    private final MongoOperations mongoOperations;
    private final CategoryService categoryService;

    public TimeService(MongoOperations mongoOperations, CategoryService categoryService) {
        this.mongoOperations = mongoOperations;
        this.categoryService = categoryService;
    }

    public TimeTracker startTime(Instant instant, User user, String categoryId) {
        TimeTracker timeTracker = new TimeTracker(user.getId(), categoryId, instant);
        Query query = Query.query(Criteria.where("status").in("STARTED", "PAUSED").and("userId").is(user.getId()));
        if (mongoOperations.exists(query, TimeTracker.class)) {
            mongoOperations.remove(query, TimeTracker.class);
            throw new IllegalStateException("a timer is already active, removing it for you");
        }
        return mongoOperations.insert(timeTracker);
    }

    public void pauseTime(User user, Instant pauseTime) {
        Query checkForOutOfSync = Query.query(Criteria.where("status").is("PAUSED").and("userId").is(user.getId()));
        Query findTimer = Query.query(Criteria.where("status").is("STARTED").and("userId").is(user.getId()));
        // -----------------------------------------------------------
        // Could add an update to the timer here if backend is out of sync with frontend
        if (mongoOperations.exists(checkForOutOfSync, TimeTracker.class)) {
            throw new IllegalStateException("You already have a paused timer");
            /*
            TimeTracker timeTracker = mongoOperations.findOne(checkForOutOfSync, TimeTracker.class);
            timeTracker.setStatus(TimerStatus.PAUSED);
            return timeTracker;
            */
        }
        TimeTracker timeTracker = mongoOperations.findOne(findTimer, TimeTracker.class);
        timeCalculations(timeTracker, "pause", user, pauseTime);
    }

    public void resumeTimer(User user, Instant resumeTime) {
        Query findTimer = Query.query(Criteria.where("status").is("PAUSED").and("userId").is(user.getId()));
        TimeTracker timeTracker = mongoOperations.findOne(findTimer, TimeTracker.class);
        timeCalculations(timeTracker, "resume", user, resumeTime);
    }

    public void stopTimer(User user, Instant stopTime) {
        Query findTimer = Query.query(Criteria.where("status").in("STARTED", "PAUSED").and("userId").is(user.getId()));
        TimeTracker timeTracker = mongoOperations.findOne(findTimer, TimeTracker.class);
        if (timeTracker == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        timeCalculations(timeTracker, "stop", user, stopTime);
    }

    public void cancelTimer(User user) {
       // String[] cancelQuery = {TimerStatus.PAUSED.toString(), TimerStatus.STARTED.toString(), TimerStatus.STOPPED.toString()};
        Query findTimer = Query.query(Criteria.where("status").in("PAUSED", "STARTED").and("userId").is(user.getId()));
        mongoOperations.remove(findTimer, TimeTracker.class);
    }

    public TimeTracker getActiveTimer(User user) {
        Query findTimer = Query.query(Criteria.where("status").in("STARTED", "PAUSED").and("userId").is(user.getId()));
        TimeTracker timeTracker = mongoOperations.findOne(findTimer, TimeTracker.class);
        if (timeTracker == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return timeTracker;
    }
    
    public List<TimeTracker> getTrackersByUser(String userId) {
        Query findCategory = Query.query(Criteria.where("userId").is(userId));
        return mongoOperations.find(findCategory, TimeTracker.class);
    }

    // https://www.mongodb.com/docs/manual/reference/operator/query/gte/?msockid=0021af1340436d161c1cb81e41146ca9
    //criteria .gte = greater than or equal
    public List<CategoryHistoryDTO> getCategoryHistory(String userId) {
        LocalDate last30Days = LocalDate.now().minusDays(30);
        Query findDurations = Query.query(Criteria.where("userId").is(userId).and("creationDate").gte(last30Days));
        List<TimeTracker> entries = mongoOperations.find(findDurations, TimeTracker.class);
        List<CategoryHistoryDTO> categoryHistoryDTO = calculateDurations(entries, userId);
        return categoryHistoryDTO;
    }
    
        public void updateTimeTrackerCategory(UpdateTimeTrackerDTO timeTrackerDTO, String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("categoryId", timeTrackerDTO.getNewCategoryId());
        mongoOperations.updateFirst(query, update, TimeTracker.class);
        }

private List<CategoryHistoryDTO> calculateDurations(List<TimeTracker> entries, String userId) {
    List<Category> categories = categoryService.getMyCategories(userId);
    List<CategoryHistoryDTO> categoryHistory = new ArrayList<>();
    for (Category category : categories) {
        Long getCategoryDuration = entries.stream()
        .filter(entry -> category.getId().equals(entry.getCategoryId()))
        .mapToLong(entry -> entry.getDuration())
        .sum();
        CategoryHistoryDTO categoryDuration = new CategoryHistoryDTO(category.getId(), category.getCategoryName(), getCategoryDuration);
        categoryHistory.add(categoryDuration);
        }
        return categoryHistory;
    }

    // https://www.geeksforgeeks.org/java/localtime-until-method-in-java-with-examples/
    // https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html#ofInstant-java.time.Instant-java.time.ZoneId-
    //
    // turns out that trying to apply timezone just throws it off

    public void timeCalculations(TimeTracker timeTracker, String operation, User user, Instant instant) {
        LocalDateTime start = timeTracker.getTimeStart();
        Update update;  
        TimerStatus findStatus;

        switch (operation) {
            case "pause":
                LocalDateTime pause = LocalDateTime.ofInstant(instant, ZoneId.ofOffset("", ZoneOffset.UTC));
                Long duration = start.until(pause, ChronoUnit.MILLIS);
                update = Update.update("duration", duration)
                .set("status", TimerStatus.PAUSED);
                findStatus = TimerStatus.STARTED;
                break;
            case "resume":
                update = Update.update("timeStart", LocalDateTime.now())
                .set("status", TimerStatus.STARTED);
                findStatus = TimerStatus.PAUSED;
                break;
            case "stop":
                LocalDateTime stop = LocalDateTime.ofInstant(instant, ZoneId.ofOffset("", ZoneOffset.UTC));
                Long finalDuration = start.until(stop, ChronoUnit.MILLIS);
                finalDuration += timeTracker.getDuration();
                update = Update.update("duration", finalDuration)
                .set("status", TimerStatus.STOPPED);
                findStatus = TimerStatus.STARTED;
                Query findTimer = Query.query(Criteria.where("status").in("STARTED", "PAUSED").and("userId").is(user.getId()));
                mongoOperations.updateFirst(findTimer, update, TimeTracker.class);
                return;

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        Query findTimer = Query.query(Criteria.where("status").is(findStatus).and("userId").is(user.getId()));
        mongoOperations.updateFirst(findTimer, update, TimeTracker.class);
            
        }
    }