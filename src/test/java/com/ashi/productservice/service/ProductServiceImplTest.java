package com.ashi.productservice.service;

import com.ashi.productservice.dto.PagedResponse;
import com.ashi.productservice.dto.ProductRequest;
import com.ashi.productservice.dto.ProductResponse;
import com.ashi.productservice.entity.Product;
import com.ashi.productservice.exception.ResourceNotFoundException;
import com.ashi.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest request;

    @BeforeEach
    void setup() {
        request = new ProductRequest();
        request.setName("MacBook Pro");
        request.setDescription("M3 laptop");
        request.setPrice(new BigDecimal("2399.00"));
        request.setCategory("Electronics");
    }

    @Test
    void getByIdThrowsWhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(1L));
    }

    @Test
    void createPersistsProduct() {
        Product saved = product(11L);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.create(request);

        assertEquals(11L, response.getId());
        assertEquals("MacBook Pro", response.getName());
    }

    @Test
    void updatePersistsChangedFields() {
        Product existing = product(22L);
        when(productRepository.findById(22L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse response = productService.update(22L, request);

        assertEquals(22L, response.getId());
        assertEquals("MacBook Pro", response.getName());
        verify(productRepository).save(existing);
    }

    @Test
    void deleteRemovesProductWhenExists() {
        when(productRepository.existsById(50L)).thenReturn(true);

        productService.delete(50L);

        verify(productRepository).deleteById(50L);
    }

    @Test
    void deleteThrowsWhenProductMissing() {
        when(productRepository.existsById(51L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.delete(51L));
        verify(productRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void searchUsesQueryWhenProvided() {
        Product entity = product(33L);
        Page<Product> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                eq("mac"), eq("mac"), any())).thenReturn(page);

        PagedResponse<ProductResponse> response = productService.search("mac", 0, 10);

        assertEquals(1, response.getTotalElements());
        assertEquals(33L, response.getItems().get(0).getId());
    }

    private Product product(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("MacBook Pro");
        product.setDescription("M3 laptop");
        product.setPrice(new BigDecimal("2399.00"));
        product.setCategory("Electronics");
        product.prePersist();
        product.preUpdate();
        return product;
    }
}


