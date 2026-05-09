package com.tibbelin.tajmtrackr.controllers;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tibbelin.tajmtrackr.Services.CategoryService;
import com.tibbelin.tajmtrackr.Services.UserService;
import com.tibbelin.tajmtrackr.dto.UpdateCategoryDTO;
import com.tibbelin.tajmtrackr.models.Category;
import com.tibbelin.tajmtrackr.models.User;

import org.springframework.web.bind.annotation.RequestMapping;
/*
Info om Authentication
https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html
*/

@CrossOrigin
@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }
    
    @PostMapping("/{id}/category")
    public ResponseEntity<Category> newCategory(@RequestBody Category category, @PathVariable String id) {
        User user = userService.getUserById(id);;
        return ResponseEntity.ok(categoryService.newCategory(category, user));
    }

    @GetMapping("/{id}/my-categories")
    public ResponseEntity<List<Category>> getPersonalCategories(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(categoryService.getMyCategories(user.getId()));
    }

    //Extra function, may be unused in V1
    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        return null;
    }

    @PatchMapping("/category/{id}")
    public ResponseEntity<Category> updateCategoryName(@PathVariable String id, @RequestBody UpdateCategoryDTO newCategoryName) {
        System.out.println("--------------------------------------> newCategoryName: " + newCategoryName);
        categoryService.updateCategoryName(newCategoryName, id);
        return ResponseEntity.noContent().build();
    }
}
