package com.koerber.assignment.inventory.repository;

import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByProduct(Product product);
}
