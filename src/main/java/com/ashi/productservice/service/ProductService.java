package com.ashi.productservice.service;

import com.ashi.productservice.dto.PagedResponse;
import com.ashi.productservice.dto.ProductRequest;
import com.ashi.productservice.dto.ProductResponse;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    PagedResponse<ProductResponse> search(String query, int page, int size);
}


