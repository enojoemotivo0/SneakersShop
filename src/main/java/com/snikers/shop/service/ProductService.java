package com.snikers.shop.service;

import com.snikers.shop.model.Product;
import com.snikers.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio para la gestión de productos (sneakers).
 * Implementa el CRUD completo exigido por el proyecto.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    // ============ READ ============

    @Transactional(readOnly = true)
    public List<Product> findAllActive() {
        return productRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Page<Product> findAllActive(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> findFeatured() {
        return productRepository.findByFeaturedTrueAndActiveTrue();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Product> findByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return productRepository.findByActiveTrue(pageable);
        }
        return productRepository.search(query.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public long count() {
        return productRepository.countByActiveTrue();
    }

    // ============ CREATE / UPDATE ============

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = findById(id);
        existing.setName(updated.getName());
        existing.setBrand(updated.getBrand());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setOriginalPrice(updated.getOriginalPrice());
        existing.setStock(updated.getStock());
        existing.setImageUrl(updated.getImageUrl());
        existing.setColor(updated.getColor());
        existing.setSizeRange(updated.getSizeRange());
        existing.setFeatured(updated.getFeatured() != null ? updated.getFeatured() : Boolean.FALSE);
        existing.setActive(updated.getActive() != null ? updated.getActive() : Boolean.TRUE);
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        return productRepository.save(existing);
    }

    // ============ DELETE (lógico) ============

    public void softDelete(Long id) {
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    public void hardDelete(Long id) {
        productRepository.deleteById(id);
    }

    // ============ STOCK ============

    public void decrementStock(Long productId, int quantity) {
        Product p = findById(productId);
        if (p.getStock() < quantity) {
            throw new IllegalStateException("Stock insuficiente para " + p.getName());
        }
        p.setStock(p.getStock() - quantity);
        productRepository.save(p);
    }
}
