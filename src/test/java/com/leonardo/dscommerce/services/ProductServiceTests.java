package com.leonardo.dscommerce.services;

import com.leonardo.dscommerce.DTO.CategoryDTO;
import com.leonardo.dscommerce.DTO.ProductDTO;
import com.leonardo.dscommerce.DTO.ProductMinDTO;
import com.leonardo.dscommerce.Factory;
import com.leonardo.dscommerce.entities.Product;
import com.leonardo.dscommerce.repositories.ProductRepository;
import com.leonardo.dscommerce.services.exceptions.DatabaseException;
import com.leonardo.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId, nonExistingId, dependentId;
    private Product product;
    private PageImpl<Product> page;
    private String name;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        dependentId = 3L;
        nonExistingId = 999L;
        name = "PlayStation";
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.searchByName((String) ArgumentMatchers.any(), (Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save((Product) ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingId);
        Mockito.doNothing().when(repository).deleteById(existingId);

    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExists(){
        ProductDTO result = service.findById(existingId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("PlayStation 5", result.getName());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
           service.findById(nonExistingId);
        });
    }

    @Test
    public void findAllShouldReturnPageProductDto(){
        String name = "PlayStation";
        Pageable pageable = PageRequest.of(0, 12);
        Page<ProductMinDTO> result = service.findAll(name, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getSize());
        Mockito.verify(repository).searchByName(name, pageable);
    }


    @Test
    public void insertShouldReturnProductDTO(){
        ProductDTO dto = Factory.createProductDto();
        ProductDTO result = service.insert(dto);

        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
        Assertions.assertEquals(dto.getDescription(), result.getDescription());
        Assertions.assertEquals(dto.getPrice(), result.getPrice());
        Assertions.assertEquals(dto.getImgUrl(), result.getImgUrl());
        Assertions.assertEquals(dto.getCategories().getFirst().getId(), result.getCategories().getFirst().getId());
        Assertions.assertEquals(dto.getCategories().getFirst().getName(), result.getCategories().getFirst().getName());

    }


    @Test
    public void updateShouldReturnProductDTOWhenIdExists(){
        product.setName("Xbox Series X");
        ProductDTO dto = new ProductDTO(product);
        ProductDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Xbox Series X", result.getName());
    }

    @Test
    public void updateShouldThrowResourceNotFoundWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            product.setName("Xbox Series X");
            ProductDTO dto = new ProductDTO(product);
            ProductDTO result = service.update(nonExistingId, dto);
        });
    }

    @Test
    public void deleteShouldReturnNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdIsDependent(){
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
