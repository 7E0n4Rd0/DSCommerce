package com.leonardo.dscommerce.DTO;

import com.leonardo.dscommerce.entities.Product;
import jakarta.validation.constraints.*;

public class ProductDTO {

    private Long id;
    @NotNull @NotEmpty @NotBlank(message = "Required field")
    @Size(min = 3, max = 80, message = "Name must be between three and eighty characters.")
    private String name;
    @Size(min = 10, message = "Description must have ten characters.")
    private String description;
    @Positive(message = "The price must be positve")
    private Double price;
    private String imgUrl;


    public ProductDTO(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public ProductDTO(Product product){
        id = product.getId();
        name = product.getName();
        description = product.getDescription();
        price = product.getPrice();
        imgUrl = product.getImgUrl();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
