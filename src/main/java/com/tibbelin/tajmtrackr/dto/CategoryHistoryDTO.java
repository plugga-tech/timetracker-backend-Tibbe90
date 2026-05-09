package com.tibbelin.tajmtrackr.dto;

public class CategoryHistoryDTO {
    private String categoryId;
    private String categoryName;
    private Long totalDuration;


    public CategoryHistoryDTO(String categoryId, String categoryName, Long totalDuration) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalDuration = totalDuration;
    }


    public String getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public String getCategoryName() {
        return categoryName;
    }


    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public Long getTotalDuration() {
        return totalDuration;
    }


    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    
}
