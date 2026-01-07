package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.model.Category;
import org.example.model.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiService {
    private final String BASE_URL = "http://localhost:7000";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Category> getCategories() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/categories");
            try (CloseableHttpResponse response = client.execute(request)) {
                return mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Category>>(){});
            }
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/products?category=" + categoryId);
            try (CloseableHttpResponse response = client.execute(request)) {
                return mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Product>>(){});
            }
        } catch (Exception e) { return new ArrayList<>(); }
    }
}