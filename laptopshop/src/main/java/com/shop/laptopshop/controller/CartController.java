package com.shop.laptopshop.controller;

import com.shop.laptopshop.model.Cart;
import com.shop.laptopshop.model.CartItem;
import com.shop.laptopshop.model.Order;
import com.shop.laptopshop.model.OrderItem;
import com.shop.laptopshop.model.Product;
import com.shop.laptopshop.repository.ProductRepository;
import com.shop.laptopshop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("cart", getCart(session));
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, 
                            @RequestParam(defaultValue = "1") int quantity, 
                            HttpSession session) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            Cart cart = getCart(session);
            cart.addItem(product, quantity);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long productId, 
                             @RequestParam int quantity, 
                             HttpSession session) {
        Cart cart = getCart(session);
        cart.updateItem(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, 
                                 HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String customerName,
                           @RequestParam String customerEmail,
                           @RequestParam String customerPhone,
                           @RequestParam String shippingAddress,
                           @RequestParam(defaultValue = "COD") String paymentMethod,
                           HttpSession session,
                           Model model) {
        Cart cart = getCart(session);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            order.addItem(orderItem);
        }

        Order savedOrder = orderService.createOrder(order);

        cart.clear();

        return "redirect:/checkout/success?orderId=" + savedOrder.getId();
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("orderId", orderId);
        model.addAttribute("order", order);
        return "checkout-success";
    }
}
