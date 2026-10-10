package com.shop.laptopshop.controller;

import com.shop.laptopshop.model.Order;
import com.shop.laptopshop.model.Product;
import com.shop.laptopshop.repository.OrderRepository;
import com.shop.laptopshop.repository.ProductRepository;
import com.shop.laptopshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/admin", "/admin/dashboard"})
public class AdminDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String dashboard(Model model) {
        List<Order> allOrders = orderRepository.findAllByOrderByOrderDateDesc();
        List<Product> allProducts = productRepository.findAll();

        // 1. Total Revenue from DELIVERED orders
        double totalRevenue = allOrders.stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 2. Total Orders
        long totalOrders = allOrders.size();

        // 3. Total Products
        long totalProducts = allProducts.size();

        // 4. Total Customers (Users with role USER)
        long totalCustomers = userRepository.findAll().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .count();

        // 5. Recent Orders (top 5)
        List<Order> recentOrders = allOrders.stream()
                .limit(5)
                .collect(Collectors.toList());

        // 6. Category counts for Bar Chart
        Map<String, Long> categoryCounts = allProducts.stream()
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));

        // 7. Order Status counts for Pie Chart
        Map<String, Long> statusCounts = allOrders.stream()
                .filter(o -> o.getStatus() != null)
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        // Add to model
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("statusCounts", statusCounts);

        return "admin/dashboard";
    }
}
