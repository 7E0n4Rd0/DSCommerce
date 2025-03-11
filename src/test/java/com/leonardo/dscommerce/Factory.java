package com.leonardo.dscommerce;

import com.leonardo.dscommerce.DTO.CategoryDTO;
import com.leonardo.dscommerce.DTO.ProductDTO;
import com.leonardo.dscommerce.entities.*;
import com.leonardo.dscommerce.projections.UserDetailsProjection;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    //User
    public static User createClient(){
        User user = new User(1L, "Alex", "alex@gmail.com", "(11) 94002-8922", LocalDate.parse("1999-12-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")), "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO");
        user.addRoles(new Role(2L , "ROLE_OPERATOR"));
        return user;
    }

    public static User createCustomClient(Long id, String name){
        User user = new User(id, name, String.join(name,"@gmail.com"), "(11) 4002-8922", LocalDate.parse("1999-12-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")), "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO");
        user.addRoles(new Role(2L , "ROLE_OPERATOR"));
        return user;
    }

    public static User createAdmin(){
        User user = new User(2L, "Maria", "maria@gmail.com", "(11) 92156-9875", LocalDate.parse("1985-03-12", DateTimeFormatter.ofPattern("yyyy-MM-dd")), "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO");
        user.addRoles(new Role(1L , "ROLE_ADMIN"));
        return user;
    }

    public static User createCustomAdmin(Long id, String name){
        User user = new User(id, name, String.join(name,"@gmail.com"), "(11) 4002-8922", LocalDate.parse("1999-12-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")), "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO");
        user.addRoles(new Role(2L , "ROLE_ADMIN"));
        return user;
    }

    //UserDetails
    public static List<UserDetailsProjection> createCustomClientUserDetails(String usename){
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl(usename, "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO", 1L, "ROLE_OPERATOR"));
        return list;
    }

    public static List<UserDetailsProjection> createCustomAdminUserDetails(String usename){
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl(usename, "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO", 2L, "ROLE_ADMIN"));
        return list;
    }

    public static List<UserDetailsProjection> createCustomAdminClientUserDetails(String usename){
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl(usename, "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO", 1L, "ROLE_OPERATOR"));
        list.add(new UserDetailsImpl(usename, "$2a$10$XZHsZ0yuw1IkMbwcTKQHiet8JQB3bAdV0KjcU9Bg6zAHbrAmOFlEO", 2L, "ROLE_ADMIN"));
        return list;
    }

    //Order

    public static Order createOrder(User client){
        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, client, new Payment());
        Product product = Factory.createProduct();
        OrderItem orderItem = new OrderItem(order, product, 1, 4500.00);
        order.getItems().add(orderItem);

        return order;
    }

}

/**
 * Inner class responsible to implements Interface {@link UserDetailsProjection}
 * and instantiate objects.
 */
class UserDetailsImpl implements UserDetailsProjection{

    private String username;
    private String password;
    private Long roleId;
    private String authority;

    public UserDetailsImpl(){}

    public UserDetailsImpl(String username, String password, Long roleId, String authority) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.authority = authority;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}

