package com.tibbelin.tajmtrackr.dto;

public class UserDurationsDTO {
    
    private String username;
    private Long totalDuration;
    
public UserDurationsDTO(String username, Long totalDuration) {
    this.username = username;
    this.totalDuration = totalDuration;
}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Long getTotalDuration() {
        return totalDuration;
    }
    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
}
}