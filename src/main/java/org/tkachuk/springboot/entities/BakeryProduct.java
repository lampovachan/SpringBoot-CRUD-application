package org.tkachuk.springboot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class BakeryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Name of product is required")
    private String name;

    @NotBlank(message = "Type of product is required")
    private String typeOfProducts;

    @NotBlank(message = "Price is required")
    private String price;

    public BakeryProduct(){}

    public BakeryProduct(String name, String typeOfProducts, String price) {
        this.name = name;
        this.typeOfProducts = typeOfProducts;
        this.price = price;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
    public String getTypeOfProducts() {
        return typeOfProducts;
    }

    public void setTypeOfProducts(String typeOfProducts) {
        this.typeOfProducts = typeOfProducts;
    }
}
