package org.tkachuk.springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.tkachuk.springboot.entities.BakeryProduct;
import org.tkachuk.springboot.repositories.BakeryProductsRepository;
import redis.clients.jedis.Jedis;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BakeryProductsController {
    private final BakeryProductsRepository bakeryProductsRepository;
    Jedis jedis = new Jedis("redis", 6379);

    @Autowired
    public BakeryProductsController(BakeryProductsRepository bakeryProductsRepository) {
        this.bakeryProductsRepository = bakeryProductsRepository;
    }

    @GetMapping("/")
    public String showMainPage(BakeryProduct bakeryProduct, Model model) {
        model.addAttribute("bakeryProducts", bakeryProductsRepository.findAll());
        return "index";
    }

    @GetMapping("/{id}")
    public String showDetail(@PathVariable("id") long id, Model model) throws InterruptedException {
        BakeryProduct bakeryProduct;
        if (jedis.exists(String.valueOf(id))) {
            List<String> listBakeryProducts = jedis.hmget(String.valueOf(id), "name", "typeOfProducts", "price");
            bakeryProduct = new BakeryProduct(listBakeryProducts.get(0), listBakeryProducts.get(1), listBakeryProducts.get(2));
            bakeryProduct.setId(id);
        } else {
            bakeryProduct = bakeryProductsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid bakery product Id:" + id));
            Map<String, String> bakeryMap = new HashMap<>();
            bakeryMap.put("name", bakeryProduct.getName());
            bakeryMap.put("price", bakeryProduct.getPrice());
            bakeryMap.put("typeOfProducts", bakeryProduct.getTypeOfProducts());
            jedis.hmset(String.valueOf(id), bakeryMap);
            jedis.expire(String.valueOf(id), 15 * 60);
        }
        model.addAttribute("bakeryProduct", bakeryProduct);
        return "bakery-product-detail";
    }

    @GetMapping("/new")
    public String showSignUpForm(BakeryProduct bakeryProduct) {
        return "add-bakery-product";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        BakeryProduct bakeryProduct = bakeryProductsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid bakery product Id:" + id));
        model.addAttribute("bakeryProduct", bakeryProduct);
        return "update-bakery-product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id, Model model) {
        BakeryProduct bakeryProduct = bakeryProductsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid bakery product Id:" + id));
        bakeryProductsRepository.delete(bakeryProduct);
        jedis.del(String.valueOf(id));
        model.addAttribute("bakeryProducts", bakeryProductsRepository.findAll());
        return "redirect:/";
    }

    @PostMapping("/addbakery")
    public String addProduct(@Valid BakeryProduct bakeryProduct, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-bakery-product";
        }
        bakeryProductsRepository.save(bakeryProduct);
        model.addAttribute("bakeryProducts", bakeryProductsRepository.findAll());
        return "redirect:/";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") long id, @Valid BakeryProduct bakeryProduct, BindingResult result, Model model) {
        if (result.hasErrors()) {
            bakeryProduct.setId(id);
            return "update-bakery-product";
        }

        bakeryProductsRepository.save(bakeryProduct);
        jedis.del(String.valueOf(id));
        model.addAttribute("bakeryProducts", bakeryProductsRepository.findAll());
        return "redirect:/";
    }
}