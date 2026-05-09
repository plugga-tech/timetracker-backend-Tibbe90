package com.tibbelin.tajmtrackr.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {
    
    @Id
    private String id;
    private String userId;
    private String categoryName;

    public Category() {}

    public Category(String id, String categoryName, String userId) {
        this.id = id;
        this.userId = userId;
        this.categoryName = categoryName;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    
}
