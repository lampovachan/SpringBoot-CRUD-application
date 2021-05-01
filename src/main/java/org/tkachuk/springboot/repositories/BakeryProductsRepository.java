package org.tkachuk.springboot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.tkachuk.springboot.entities.BakeryProduct;

@Repository
public interface BakeryProductsRepository extends CrudRepository<BakeryProduct, Long> {
}