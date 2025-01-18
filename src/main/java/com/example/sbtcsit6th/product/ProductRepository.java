package com.example.sbtcsit6th.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Optional<Product> findByName(String name);

	Long countByName(String name);

}
