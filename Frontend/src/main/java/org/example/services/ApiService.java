package org.example.services;

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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.example.model.Order;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiService {
    private final String BASE_URL = "http://localhost:7000/api";
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
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }

    public int createOrder(Order order) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/orders");
            
            String json = mapper.writeValueAsString(order);
            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            request.setEntity(entity);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 201) {
                    Map<String, Object> map = mapper.readValue(response.getEntity().getContent(), new TypeReference<Map<String, Object>>(){});
                    return (int) map.get("orderId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public List<Product> getAllProducts() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + "/products");
            try (CloseableHttpResponse response = client.execute(request)) {
                return mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Product>>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}


