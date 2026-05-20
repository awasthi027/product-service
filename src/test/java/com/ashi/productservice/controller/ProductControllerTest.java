package com.ashi.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ashi.productservice.dto.PagedResponse;
import com.ashi.productservice.dto.ProductRequest;
import com.ashi.productservice.dto.ProductResponse;
import com.ashi.productservice.exception.ResourceNotFoundException;
import com.ashi.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void createProductReturnsCreated() throws Exception {
        ProductRequest request = createRequest();
        ProductResponse response = createResponse(1L);

        when(productService.create(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15"));
    }

    @Test
    void getProductReturnsOk() throws Exception {
        when(productService.getById(10L)).thenReturn(createResponse(10L));

        mockMvc.perform(get("/api/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    @Test
    void updateProductReturnsOk() throws Exception {
        ProductRequest request = createRequest();
        when(productService.update(eq(3L), any(ProductRequest.class))).thenReturn(createResponse(3L));

        mockMvc.perform(put("/api/products/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void deleteProductReturnsNoContent() throws Exception {
        doNothing().when(productService).delete(5L);

        mockMvc.perform(delete("/api/products/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchProductsReturnsPagedData() throws Exception {
        ProductResponse response = createResponse(7L);
        PagedResponse<ProductResponse> page = new PagedResponse<>(List.of(response), 0, 10, 1, 1);
        when(productService.search("phone", 0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/products/search").param("q", "phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(7))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getProductReturnsNotFoundWhenMissing() throws Exception {
        when(productService.getById(99L)).thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }

    @Test
    void deleteReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 42")).when(productService).delete(42L);

        mockMvc.perform(delete("/api/products/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    private ProductRequest createRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("iPhone 15");
        request.setDescription("Latest Apple smartphone");
        request.setPrice(new BigDecimal("999.99"));
        request.setCategory("Electronics");
        return request;
    }

    private ProductResponse createResponse(Long id) {
        ProductResponse response = new ProductResponse();
        response.setId(id);
        response.setName("iPhone 15");
        response.setDescription("Latest Apple smartphone");
        response.setPrice(new BigDecimal("999.99"));
        response.setCategory("Electronics");
        response.setCreatedAt(Instant.now());
        response.setUpdatedAt(Instant.now());
        return response;
    }
}


