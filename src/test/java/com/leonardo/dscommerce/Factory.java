package com.leonardo.dscommerce;

import com.leonardo.dscommerce.DTO.CategoryDTO;
import com.leonardo.dscommerce.DTO.ProductDTO;
import com.leonardo.dscommerce.DTO.ProductMinDTO;
import com.leonardo.dscommerce.entities.Category;
import com.leonardo.dscommerce.entities.Product;

public class Factory {

    // Category
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

    //Product
    public static Product createProduct(){
        Product product = new Product(1L, "PlayStation 5", "A videogame console", 4500.00, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
        product.getCategories().add(createCategory(2L, "Games"));
        return product;
    }

    public static Product createProduct(String name){
        Product product = new Product();
        product.setName(name);
        return product;
    }

    public static ProductDTO createProductDto(){
        return new ProductDTO(createProduct());
    }

    public static ProductDTO createProductDto(Long id, String name, String description, Double price, String imgUrl, CategoryDTO category){
        ProductDTO dto  = new ProductDTO(id, name, description, price, imgUrl);
        dto.getCategories().add(category);
        return dto;
    }
    

}
