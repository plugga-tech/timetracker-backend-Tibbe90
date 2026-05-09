package com.tibbelin.tajmtrackr.Services;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.tibbelin.tajmtrackr.dto.UpdateCategoryDTO;
import com.tibbelin.tajmtrackr.models.Category;
import com.tibbelin.tajmtrackr.models.User;

@Service
public class CategoryService {
    private final MongoOperations mongoOperations;

    public CategoryService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public Category newCategory(Category category, User user) {
        category.setUserId(user.getId());
        Query query = Query.query(Criteria.where("categoryName").is(category.getCategoryName())
        .and("userId").is(category.getUserId()));
        if (mongoOperations.exists(query, Category.class)) {
            throw new IllegalArgumentException("You already have this category");
        }
        return mongoOperations.insert(category);
    }

    public List<Category> getMyCategories(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoOperations.find(query, Category.class);
    }

    public void updateCategoryName(UpdateCategoryDTO updateName, String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("categoryName", updateName.getNewName());
        mongoOperations.updateFirst(query, update, Category.class);
    }
}
