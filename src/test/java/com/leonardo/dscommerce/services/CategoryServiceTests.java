package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.CategoryDTO;
import com.leonardo.dscommerce.Factory;
import com.leonardo.dscommerce.entities.Category;
import com.leonardo.dscommerce.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private Category category;
    private List<Category> list;

    @BeforeEach
    void setUp() throws Exception{
        category = Factory.createCategory();
        list = Arrays.asList(Factory.createCategory(2L, "Books"), Factory.createCategory(3L, "Games"));

        Mockito.when(repository.findAll()).thenReturn(list);

    }

    @Test
    public void findAllShouldReturnListCategoryDTO(){
        List<CategoryDTO> result = service.findAll();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Books", result.getFirst().getName());
        Assertions.assertEquals("Games", result.getLast().getName());
    }

}
