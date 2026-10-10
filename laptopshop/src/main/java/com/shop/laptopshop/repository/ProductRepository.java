package com.shop.laptopshop.repository;

import com.shop.laptopshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop5ByOrderByIdDesc();
    List<Product> findByCategory(String category);
    List<Product> findByCategoryIn(List<String> categories);
    List<Product> findByCategoryAndBrandIn(String category, List<String> brands);
    List<Product> findByCategoryInAndBrandIn(List<String> categories, List<String> brands);
    long countByCategory(String category);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
