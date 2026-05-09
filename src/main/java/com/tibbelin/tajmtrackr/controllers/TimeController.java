package com.tibbelin.tajmtrackr.controllers;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibbelin.tajmtrackr.Services.TimeService;
import com.tibbelin.tajmtrackr.Services.UserService;
import com.tibbelin.tajmtrackr.dto.CategoryHistoryDTO;
import com.tibbelin.tajmtrackr.dto.UpdateTimeTrackerDTO;
import com.tibbelin.tajmtrackr.models.TimeTracker;
import com.tibbelin.tajmtrackr.models.User;

/*
https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html
*/

@RestController
@CrossOrigin
@RequestMapping("api/time")
public class TimeController {
    private final TimeService timeService;
    private final UserService userService;

    public TimeController(TimeService timeService, UserService userService) {
        this.timeService = timeService;
        this.userService = userService;
    }
    
    @PostMapping("/{id}/{categoryId}/start")
    public ResponseEntity<TimeTracker> startTimer(@RequestBody Long timeStart, @PathVariable String categoryId, @PathVariable String id) {
        User user = userService.getUserById(id);
        Instant instant = Instant.ofEpochMilli(timeStart);
        return ResponseEntity.ok(timeService.startTime(instant, user, categoryId));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<TimeTracker> pauseTimer(@PathVariable String id, @RequestBody Long pauseTime) {
        User user = userService.getUserById(id);
        Instant instant = Instant.ofEpochMilli(pauseTime);
        timeService.pauseTime(user, instant);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<TimeTracker> resumeTimer(@PathVariable String id, @RequestBody Long resumeTime) {
        User user = userService.getUserById(id);
        Instant instant = Instant.ofEpochMilli(resumeTime);
        timeService.resumeTimer(user, instant);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<String> stopTimer(@PathVariable String id, @RequestBody Long stopTime) {
        User user = userService.getUserById(id);
        Instant instant = Instant.ofEpochMilli(stopTime);
        timeService.stopTimer(user, instant);
        return ResponseEntity.status(200).body("Timer Stopped");
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<TimeTracker> cancelTimer(@PathVariable String id) {
        User user = userService.getUserById(id);
        timeService.cancelTimer(user);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<TimeTracker> getCurrentTimer(@PathVariable String id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(timeService.getActiveTimer(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<List<TimeTracker>> getTrackers(@PathVariable String id) {
        return ResponseEntity.ok(timeService.getTrackersByUser(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CategoryHistoryDTO>> getHistory(@PathVariable String id){
        return ResponseEntity.ok(timeService.getCategoryHistory(id));
    }
    
    @PatchMapping("/category/{id}")
    public ResponseEntity<TimeTracker> updateCategoryName(@PathVariable String id, @RequestBody UpdateTimeTrackerDTO timeTrackerDTO) {
        timeService.updateTimeTrackerCategory(timeTrackerDTO, id);
        return ResponseEntity.noContent().build();
    }

    /*
    Unsure if necessary
    @GetMapping("/{id}")
    public ResponseEntity<TimeTracker> getTimerById(@PathVariable String id) {
        return null;
    }
    */

    //Extra function, will possibly be unused in V1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimer(@RequestParam String id) {
        return null;
    }
}
