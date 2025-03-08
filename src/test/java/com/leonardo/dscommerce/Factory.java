package com.leonardo.dscommerce;

import com.leonardo.dscommerce.DTO.CategoryDTO;
import com.leonardo.dscommerce.entities.Category;

public class Factory {

    public static Category createCategory(){
        return new Category(1L, "Electronics");
    }

    public static Category createCategory(Long id, String name){
        return new Category(id, name);
    }

    public static CategoryDTO createCategoryDTO(){
        return new CategoryDTO(createCategory());
    }

    public static CategoryDTO createCategoryDTO(Long id, String name){
        return new CategoryDTO(id, name);
    }
}
