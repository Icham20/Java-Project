package org.example.service;

import org.example.model.Category;
import org.example.model.Product;
import java.util.List;
import java.util.ResourceBundle;

public class MockDatabase {

    public static void fillCategories(List<Category> list, ResourceBundle bundle) {
        list.add(new Category(1L, bundle.getString("cat.1")));
        list.add(new Category(2L, bundle.getString("cat.2")));
        list.add(new Category(3L, bundle.getString("cat.3")));
        list.add(new Category(4L, bundle.getString("cat.4")));
    }

    public static void fillProducts(List<Product> list, Long catId, ResourceBundle bundle) {
        if (catId == 1L) {
            list.add(new Product(10L, bundle.getString("prod.10.name"), 6.90, bundle.getString("prod.10.desc"), catId));
            list.add(new Product(11L, bundle.getString("prod.11.name"), 5.50, bundle.getString("prod.11.desc"), catId));
        } else if (catId == 2L) {
            list.add(new Product(20L, bundle.getString("prod.20.name"), 12.90, bundle.getString("prod.20.desc"), catId));
            list.add(new Product(21L, bundle.getString("prod.21.name"), 14.50, bundle.getString("prod.21.desc"), catId));
            list.add(new Product(22L, bundle.getString("prod.22.name"), 14.90, bundle.getString("prod.22.desc"), catId));
        } else {
            list.add(new Product(30L, bundle.getString("prod.30.name"), 4.50, bundle.getString("prod.30.desc"), catId));
            list.add(new Product(31L, bundle.getString("prod.31.name"), 5.00, bundle.getString("prod.31.desc"), catId));
        }
    }
}