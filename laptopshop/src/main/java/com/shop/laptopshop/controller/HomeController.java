package com.shop.laptopshop.controller;

import com.shop.laptopshop.repository.ProductRepository;
import com.shop.laptopshop.repository.ReviewRepository;
import com.shop.laptopshop.model.Product;
import com.shop.laptopshop.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", loadProducts());
        model.addAttribute("newArrivals", productRepository.findTop5ByOrderByIdDesc());
        return "home";
    }

    @GetMapping("/linh-kien")
    public String components(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String category,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<String> brand,
            Model model) {
        
        java.util.List<com.shop.laptopshop.model.Product> products;
        
        if (category != null && !category.trim().isEmpty()) {
            if (brand != null && !brand.isEmpty()) {
                products = productRepository.findByCategoryAndBrandIn(category, brand);
            } else {
                products = productRepository.findByCategory(category);
            }
        } else {
            java.util.List<String> defaultCategories = java.util.List.of("LAPTOP", "CPU", "GPU", "RAM", "MAINBOARD", "POWER_SUPPLY");
            if (brand != null && !brand.isEmpty()) {
                products = productRepository.findByCategoryInAndBrandIn(defaultCategories, brand);
            } else {
                products = productRepository.findByCategoryIn(defaultCategories);
            }
        }
        
        model.addAttribute("products", products);
        
        // Pass counts for components sidebar
        model.addAttribute("cpuCount", productRepository.countByCategory("CPU"));
        model.addAttribute("gpuCount", productRepository.countByCategory("GPU"));
        model.addAttribute("ramCount", productRepository.countByCategory("RAM"));
        model.addAttribute("mainboardCount", productRepository.countByCategory("MAINBOARD"));
        model.addAttribute("powerCount", productRepository.countByCategory("POWER_SUPPLY"));
        
        return "linh-kien";
    }

    @GetMapping("/may-bo")
    public String pcSystems(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String category,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<String> brand,
            Model model) {
        
        java.util.List<com.shop.laptopshop.model.Product> products;
        
        if (category != null && !category.trim().isEmpty()) {
            if (brand != null && !brand.isEmpty()) {
                products = productRepository.findByCategoryAndBrandIn(category, brand);
            } else {
                products = productRepository.findByCategory(category);
            }
        } else {
            java.util.List<String> defaultCategories = java.util.List.of("PC_GAMING", "PC_WORKSTATION", "PC_OFFICE");
            if (brand != null && !brand.isEmpty()) {
                products = productRepository.findByCategoryInAndBrandIn(defaultCategories, brand);
            } else {
                products = productRepository.findByCategoryIn(defaultCategories);
            }
        }
        
        model.addAttribute("products", products);
        
        // Pass counts for PC systems sidebar
        model.addAttribute("gamingCount", productRepository.countByCategory("PC_GAMING"));
        model.addAttribute("workstationCount", productRepository.countByCategory("PC_WORKSTATION"));
        model.addAttribute("officeCount", productRepository.countByCategory("PC_OFFICE"));
        
        return "may-bo";
    }

    @GetMapping("/phu-kien")
    public String accessories(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String category,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<String> brand,
            Model model) {
        
        java.util.List<com.shop.laptopshop.model.Product> products;
        
        if (category != null && !category.trim().isEmpty()) {
            if (brand != null && !brand.isEmpty()) {
                products = productRepository.findByCategoryAndBrandIn(category, brand);
            } else {
                products = productRepository.findByCategory(category);
            }
        } else {
            java.util.List<String> defaultCategories = java.util.List.of("KEYBOARD", "MOUSE", "MONITOR");
            if (brand != null && !brand.isEmpty()) {
                products = productRepository.findByCategoryInAndBrandIn(defaultCategories, brand);
            } else {
                products = productRepository.findByCategoryIn(defaultCategories);
            }
        }
        
        model.addAttribute("products", products);
        
        // Pass counts for Accessories sidebar
        model.addAttribute("keyboardCount", productRepository.countByCategory("KEYBOARD"));
        model.addAttribute("mouseCount", productRepository.countByCategory("MOUSE"));
        model.addAttribute("monitorCount", productRepository.countByCategory("MONITOR"));
        
        return "phu-kien";
    }

    @GetMapping("/ho-tro")
    public String support() {
        return "ho-tro";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/";
        }
        
        java.util.List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(id);
        long reviewCount = reviews.size();
        double avgRating = 0.0;
        if (reviewCount > 0) {
            double sum = 0.0;
            for (Review r : reviews) {
                sum += r.getRating();
            }
            avgRating = sum / reviewCount;
        }
        
        // Fetch related products (excluding this one)
        java.util.List<Product> relatedProducts = new java.util.ArrayList<>();
        if (product.getCategory() != null) {
            relatedProducts = productRepository.findByCategory(product.getCategory());
            final Long currentId = product.getId();
            relatedProducts.removeIf(p -> p.getId().equals(currentId));
            if (relatedProducts.size() > 4) {
                relatedProducts = relatedProducts.subList(0, 4);
            }
        }
        
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("relatedProducts", relatedProducts);
        
        return "product-detail";
    }

    @PostMapping("/product/{id}/review")
    public String addReview(
            @PathVariable Long id,
            @RequestParam(required = false) String author,
            @RequestParam int rating,
            @RequestParam String content,
            jakarta.servlet.http.HttpSession session) {
            
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/";
        }
        
        String authorName = author;
        com.shop.laptopshop.model.User currentUser = (com.shop.laptopshop.model.User) session.getAttribute("user");
        if (currentUser != null) {
            authorName = currentUser.getName();
        }
        
        if (authorName == null || authorName.trim().isEmpty()) {
            authorName = "Khách ẩn danh";
        }
        
        Review review = new Review(product, authorName, rating, content);
        reviewRepository.save(review);
        
        return "redirect:/product/" + id;
    }

    @GetMapping("/ai-consultant")
    public String aiConsultant() {
        return "ai-consultant";
    }

    @PostMapping("/api/ai/chat")
    @org.springframework.web.bind.annotation.ResponseBody
    public java.util.Map<String, Object> aiChat(@RequestParam String message) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        String msgLower = message.toLowerCase();
        String reply = "";
        java.util.List<Product> recommendations = new java.util.ArrayList<>();
        
        if (msgLower.contains("laptop") || msgLower.contains("máy tính xách tay")) {
            recommendations = productRepository.findByCategory("LAPTOP");
            reply = "Dạ, TECH-PC đang có các dòng Laptop chính hãng cấu hình cao từ văn phòng mỏng nhẹ đến gaming hầm hố. Dưới đây là các sản phẩm nổi bật bạn có thể tham khảo ạ:";
        } else if (msgLower.contains("gaming") || msgLower.contains("chơi game") || msgLower.contains("game")) {
            java.util.List<Product> pcs = productRepository.findByCategory("PC_GAMING");
            java.util.List<Product> laptops = productRepository.findByCategory("LAPTOP");
            recommendations.addAll(pcs);
            for (Product p : laptops) {
                if (p.getName().toLowerCase().contains("strix") || p.getName().toLowerCase().contains("raider")) {
                    recommendations.add(p);
                }
            }
            reply = "Dạ, để chiến mượt các tựa game hiện nay (Valorant, GTA V, AAA...), TECH-PC xin tư vấn các dòng PC Gaming lắp sẵn tối ưu hoặc Laptop Gaming hiệu năng cao dưới đây ạ:";
        } else if (msgLower.contains("đồ họa") || msgLower.contains("render") || msgLower.contains("thiết kế") || msgLower.contains("workstation") || msgLower.contains("xeon")) {
            recommendations = productRepository.findByCategory("PC_WORKSTATION");
            reply = "Chào bạn! Đối với công việc thiết kế đồ họa 3D, dựng phim, và render nặng, TECH-PC khuyên dùng dòng cấu hình Workstation chuyên nghiệp trang bị chip đa nhân và card chuyên dụng:";
        } else if (msgLower.contains("văn phòng") || msgLower.contains("học tập") || msgLower.contains("office")) {
            recommendations = productRepository.findByCategory("PC_OFFICE");
            reply = "Chào bạn! Đối với nhu cầu học tập trực tuyến, kế toán, văn phòng cơ bản, TECH-PC có các bộ máy PC Office nhỏ gọn, bền bỉ, tiết kiệm điện năng này ạ:";
        } else if (msgLower.contains("cpu") || msgLower.contains("chip") || msgLower.contains("vi xử lý")) {
            recommendations = productRepository.findByCategory("CPU");
            reply = "Dạ, TECH-PC cung cấp các dòng CPU Intel Core thế hệ 14 và AMD Ryzen 7000 mới nhất. Dưới đây là các vi xử lý đang bán chạy nhất:";
        } else if (msgLower.contains("gpu") || msgLower.contains("vga") || msgLower.contains("card màn hình") || msgLower.contains("card đồ họa")) {
            recommendations = productRepository.findByCategory("GPU");
            reply = "Dạ, đây là các dòng card đồ họa (VGA) NVIDIA RTX 40-series và AMD Radeon thế hệ mới đang có sẵn hàng tại TECH-PC để nâng cấp cấu hình ạ:";
        } else if (msgLower.contains("ram")) {
            recommendations = productRepository.findByCategory("RAM");
            reply = "Chào bạn! Để máy chạy đa tác vụ không giật lag, bạn nên nâng cấp RAM DDR5 thế hệ mới. Dưới đây là các kit RAM DDR5 RGB cao cấp:";
        } else if (msgLower.contains("main") || msgLower.contains("mainboard") || msgLower.contains("bo mạch chủ")) {
            recommendations = productRepository.findByCategory("MAINBOARD");
            reply = "Dạ, TECH-PC xin giới thiệu các mẫu bo mạch chủ chuyên gaming và đồ họa cực kỳ bền bỉ từ ASUS ROG, TUF và MSI:";
        } else if (msgLower.contains("nguồn") || msgLower.contains("psu") || msgLower.contains("power")) {
            recommendations = productRepository.findByCategory("POWER_SUPPLY");
            reply = "Dạ, đây là các dòng nguồn công suất thực chuẩn 80 Plus Gold/Platinum giúp hệ thống hoạt động ổn định và an toàn nhất:";
        } else if (msgLower.contains("phím") || msgLower.contains("bàn phím") || msgLower.contains("keyboard") || msgLower.contains("chuột") || msgLower.contains("mouse") || msgLower.contains("màn hình") || msgLower.contains("monitor")) {
            java.util.List<String> accCats = java.util.List.of("KEYBOARD", "MOUSE", "MONITOR");
            recommendations = productRepository.findByCategoryIn(accCats);
            reply = "Dạ, TECH-PC xin tư vấn các dòng phụ kiện cơ bản gồm bàn phím cơ Keychron/AKKO, chuột Logitech/Razer và màn hình Ultrasharp/OLED cao cấp dưới đây:";
        } else {
            reply = "Xin chào! Mình là Trợ lý ảo thông minh của TECH-PC. 🤖\n\nHãy cho mình biết nhu cầu của bạn (ví dụ: cần tìm laptop văn phòng, PC chiến game, CPU render đồ họa...) hoặc tầm ngân sách của bạn, mình sẽ tìm sản phẩm phù hợp nhất trong kho hàng cho bạn ạ!";
        }
        
        // Budget filtering if user specifies price (e.g. 10tr, 20tr, 30tr)
        double maxPrice = -1;
        if (msgLower.contains("10 triệu") || msgLower.contains("10tr")) {
            maxPrice = 10000000;
        } else if (msgLower.contains("20 triệu") || msgLower.contains("20tr")) {
            maxPrice = 20000000;
        } else if (msgLower.contains("30 triệu") || msgLower.contains("30tr")) {
            maxPrice = 30000000;
        } else if (msgLower.contains("50 triệu") || msgLower.contains("50tr")) {
            maxPrice = 50000000;
        }
        
        if (maxPrice > 0) {
            final double limit = maxPrice;
            if (recommendations.isEmpty()) {
                recommendations = productRepository.findAll();
                reply = "TECH-PC đã tìm thấy các sản phẩm có giá dưới " + String.format("%,.0f", limit) + "đ phù hợp với ngân sách của bạn:";
            } else {
                reply = reply + " (Đã lọc sản phẩm dưới " + String.format("%,.0f", limit) + "đ)";
            }
            recommendations.removeIf(p -> p.getPrice() > limit);
        }
        
        java.util.List<Product> finalRecommendations = new java.util.ArrayList<>(recommendations);
        if (finalRecommendations.size() > 5) {
            finalRecommendations = finalRecommendations.subList(0, 5);
        }
        
        response.put("reply", reply);
        response.put("recommendations", finalRecommendations);
        return response;
    }

    @GetMapping("/search")
    public String search(@RequestParam String query, org.springframework.ui.Model model) {
        java.util.List<Product> results = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        model.addAttribute("products", results);
        model.addAttribute("searchQuery", query);
        return "search-results";
    }

    @GetMapping("/lien-he")
    public String lienHe() {
        return "lien-he";
    }

    private java.util.List<com.shop.laptopshop.model.Product> loadProducts() {
        var products = productRepository.findAll();
        if (products == null || products.isEmpty()) {
            return java.util.List.of(
                new com.shop.laptopshop.model.Product("RTX 4090 System", 89900000, "Hệ thống máy mạnh mẽ trang bị RTX 4090", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=1200&auto=format&fit=crop", true),
                new com.shop.laptopshop.model.Product("Keychron Q1 Max", 4250000, "Bàn phím cơ Custom cao cấp", "https://images.unsplash.com/photo-1542393545-10f5cde6c810?q=80&w=800&auto=format&fit=crop", false),
                new com.shop.laptopshop.model.Product("Gaming Mouse", 950000, "Chuột chơi game chuyên nghiệp", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=800&auto=format&fit=crop", false)
            );
        }
        return products;
    }
}