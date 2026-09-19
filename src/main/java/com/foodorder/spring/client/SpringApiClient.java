package com.foodorder.spring.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodorder.model.*;
import com.foodorder.spring.dto.LoginRequest;
import com.foodorder.spring.dto.OrderRequest;
import com.foodorder.spring.dto.RegisterRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Swing-to-Spring Boot REST client. The Swing application uses this class
 * instead of accessing the database directly.
 */
public class SpringApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final ObjectMapper JSON = new ObjectMapper();

    public static Customer login(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return send("POST", "/auth/login", request, Customer.class);
    }

    public static Admin adminLogin(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return send("POST", "/auth/admin/login", request, Admin.class);
    }

    public static Customer register(String name, String email, String password,
                                    String phone, String address) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setPhone(phone);
        request.setAddress(address);
        return send("POST", "/auth/register", request, Customer.class);
    }

    public static List<FoodItem> getFood(String search) throws Exception {
        String path = "/food";
        if (search != null && !search.isBlank()) {
            path += "?search=" + java.net.URLEncoder.encode(search, StandardCharsets.UTF_8);
        }
        return sendList("GET", path, null, new TypeReference<List<FoodItem>>() {});
    }

    public static FoodItem addFood(FoodItem food) throws Exception {
        return send("POST", "/food", food, FoodItem.class);
    }

    public static FoodItem updateFood(FoodItem food) throws Exception {
        return send("PUT", "/food/" + food.getId(), food, FoodItem.class);
    }

    public static void deleteFood(int foodId) throws Exception {
        send("DELETE", "/food/" + foodId, null, Void.class);
    }

    public static Order placeOrder(int customerId, String paymentMethod, List<OrderItem> items) throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(customerId);
        request.setPaymentMethod(paymentMethod);
        request.setOrderItems(items);
        return send("POST", "/orders", request, Order.class);
    }

    public static List<Order> getCustomerOrders(int customerId) throws Exception {
        return sendList("GET", "/orders/customer/" + customerId, null,
                new TypeReference<List<Order>>() {});
    }

    public static List<Order> getAllOrders() throws Exception {
        return sendList("GET", "/orders", null, new TypeReference<List<Order>>() {});
    }

    public static void updateOrderStatus(int orderId, String status) throws Exception {
        send("PATCH", "/orders/" + orderId + "/status?status=" +
                java.net.URLEncoder.encode(status, StandardCharsets.UTF_8), null, Map.class);
    }

    private static <T> T send(String method, String path, Object body, Class<T> type) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Accept", "application/json");
        if (body != null) {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(extractMessage(response.body(), "HTTP " + response.statusCode()));
        }
        if (type == Void.class || response.body() == null || response.body().isBlank()) return null;
        return JSON.readValue(response.body(), type);
    }

    private static <T> T sendList(String method, String path, Object body, TypeReference<T> type) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Accept", "application/json");
        if (body != null) {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(extractMessage(response.body(), "HTTP " + response.statusCode()));
        }
        return JSON.readValue(response.body(), type);
    }

    private static String extractMessage(String body, String fallback) {
        try {
            Map<?, ?> map = JSON.readValue(body, Map.class);
            Object message = map.get("message");
            return message == null ? fallback : message.toString();
        } catch (Exception ignored) {
            return body == null || body.isBlank() ? fallback : body;
        }
    }
}
