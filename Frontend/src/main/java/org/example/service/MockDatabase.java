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
        } else if (catId == 3L) {
            list.add(new Product(31L, bundle.getString("prod.31.name"), 5.00, bundle.getString("prod.31.desc"), catId));
            list.add(new Product(32L, bundle.getString("prod.32.name"), 6.50, bundle.getString("prod.32.desc"), catId));
            list.add(new Product(33L, bundle.getString("prod.33.name"), 4.00, bundle.getString("prod.33.desc"), catId));
        } else if (catId == 4L) {
            list.add(new Product(40L, bundle.getString("prod.40.name"), 4.50, bundle.getString("prod.40.desc"), catId));
            list.add(new Product(41L, bundle.getString("prod.41.name"), 4.50, bundle.getString("prod.41.desc"), catId));
            list.add(new Product(42L, bundle.getString("prod.42.name"), 3.50, bundle.getString("prod.42.desc"), catId));
        }
    }
}