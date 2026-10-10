package com.shop.laptopshop.controller;

import com.shop.laptopshop.model.Cart;
import com.shop.laptopshop.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addCurrentUser(Model model, HttpSession session) {
        Object currentUser = session.getAttribute("user");
        if (currentUser instanceof User) {
            model.addAttribute("currentUser", currentUser);
        }
    }

    @ModelAttribute
    public void addCartCount(Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        int cartItemsCount = (cart != null) ? cart.getTotalItemsCount() : 0;
        model.addAttribute("cartItemsCount", cartItemsCount);
    }
}
