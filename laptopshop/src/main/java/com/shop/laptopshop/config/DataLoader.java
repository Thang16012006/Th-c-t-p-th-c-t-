package com.shop.laptopshop.config;

import com.shop.laptopshop.model.Order;
import com.shop.laptopshop.model.OrderItem;
import com.shop.laptopshop.model.Product;
import com.shop.laptopshop.model.User;
import com.shop.laptopshop.repository.OrderRepository;
import com.shop.laptopshop.repository.ProductRepository;
import com.shop.laptopshop.repository.UserRepository;
import com.shop.laptopshop.repository.ReviewRepository;
import com.shop.laptopshop.model.Review;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ProductRepository productRepository, OrderRepository orderRepository, UserRepository userRepository, ReviewRepository reviewRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean hasOldData = false;
        boolean hasPCData = false;
        boolean hasAccessoryData = false;
        for (Product p : productRepository.findAll()) {
            if ("RTX 4090 System".equals(p.getName()) || p.getCategory() == null || ("ASUS ROG Strix G16".equals(p.getName()) && !p.getImageUrl().contains("photo-1603302576837-37561b2e2302"))) {
                hasOldData = true;
                break;
            }
            if (p.getCategory() != null && p.getCategory().startsWith("PC_")) {
                hasPCData = true;
            }
            if ("KEYBOARD".equals(p.getCategory())) {
                hasAccessoryData = true;
            }
        }
        if (!hasPCData || !hasAccessoryData) {
            hasOldData = true;
        }

        if (hasOldData) {
            orderRepository.deleteAll();
            reviewRepository.deleteAll();
            productRepository.deleteAll();
        }

        if (productRepository.count() == 0) {
            // Laptops
            productRepository.save(create("ASUS ROG Strix G16", 34990000, "Laptop Gaming hiệu năng khủng, Core i7-13650HX, RTX 4060, màn 165Hz mượt mà", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=800&auto=format&fit=crop", true, "LAPTOP", "ASUS"));
            productRepository.save(create("MacBook Air M3 2024", 27490000, "Thiết kế siêu mỏng nhẹ, pin 18 tiếng, chip Apple M3 mạnh mẽ, RAM 16GB tiện lợi", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?q=80&w=800&auto=format&fit=crop", true, "LAPTOP", "Apple"));
            productRepository.save(create("Dell XPS 15 9530", 52990000, "Laptop doanh nhân cao cấp, màn OLED 3.5K cảm ứng, Core i9, card đồ họa RTX 4070", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=800&auto=format&fit=crop", false, "LAPTOP", "Dell"));
            productRepository.save(create("MSI Raider GE78 HX", 45990000, "Laptop gaming tối thượng, Core i9-13980HX, RTX 4080, màn 240Hz siêu mượt", "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?q=80&w=800&auto=format&fit=crop", true, "LAPTOP", "MSI"));

            // CPU
            productRepository.save(create("Intel Core i9-14900K", 14500000, "Bộ vi xử lý Intel Core thế hệ 14 Raptor Lake Refresh, 24 nhân 32 luồng, xung nhịp lên đến 6.0GHz", "https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?q=80&w=800&auto=format&fit=crop", true, "CPU", "Intel"));
            productRepository.save(create("Intel Core i7-14700K", 10200000, "Bộ vi xử lý tầm cao cho gaming và đồ họa, 20 nhân 28 luồng, hiệu năng cực mạnh", "https://images.unsplash.com/photo-1601524909162-be87252be298?q=80&w=800&auto=format&fit=crop", false, "CPU", "Intel"));
            productRepository.save(create("AMD Ryzen 7 7800X3D", 10500000, "CPU gaming tốt nhất thế giới với công nghệ 3D V-Cache độc quyền từ AMD", "https://images.unsplash.com/photo-1618945091913-c275550a2948?q=80&w=800&auto=format&fit=crop", true, "CPU", "AMD"));
            productRepository.save(create("AMD Ryzen 9 7950X", 13800000, "Quái vật đa nhân đa luồng cho render và làm việc nặng, 16 nhân 32 luồng Zen 4", "https://images.unsplash.com/photo-1581092160607-ee22621dd758?q=80&w=800&auto=format&fit=crop", false, "CPU", "AMD"));

            // GPU
            productRepository.save(create("ASUS ROG Strix RTX 4080 Super", 31990000, "Card đồ họa phân khúc Ultra-premium với hệ thống tản nhiệt ROG đỉnh cao", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=800&auto=format&fit=crop", true, "GPU", "ASUS"));
            productRepository.save(create("MSI GeForce RTX 4070 Ti Super Gaming X Slim", 24490000, "Thiết kế mỏng nhẹ sang trọng, hiệu năng chiến game 2K/4K max settings", "https://images.unsplash.com/photo-1555680202-c86f0e12f086?q=80&w=800&auto=format&fit=crop", true, "GPU", "MSI"));
            productRepository.save(create("ASUS Dual GeForce RTX 4060 Ti 8GB", 10890000, "Phiên bản 2 quạt gọn gàng phù hợp mọi cấu hình, mát mẻ bền bỉ", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=800&auto=format&fit=crop", false, "GPU", "ASUS"));
            productRepository.save(create("MSI Radeon RX 7800 XT Gaming Trio", 14990000, "Card đồ họa từ đội đỏ mạnh mẽ, VRAM 16GB tha hồ chiến game nặng", "https://images.unsplash.com/photo-1600132806370-bf17e65e942f?q=80&w=800&auto=format&fit=crop", false, "GPU", "MSI"));

            // RAM
            productRepository.save(create("Kingston Fury Beast RGB 32GB DDR5 ASUS TUF", 3250000, "Bộ nhớ RAM DDR5 hiệu năng cao kết hợp thiết kế TUF Gaming cực chất", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=800&auto=format&fit=crop", false, "RAM", "ASUS"));
            productRepository.save(create("G.Skill Trident Z5 Neo RGB 32GB DDR5 AMD", 3450000, "RAM DDR5 tối ưu hóa cho hệ thống AMD Ryzen mới nhất, bus 6000MHz", "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=800&auto=format&fit=crop", false, "RAM", "AMD"));
            productRepository.save(create("Corsair Vengeance RGB 32GB DDR5 Intel XMP", 3150000, "Tối ưu hoàn hảo cho Intel, hỗ trợ XMP 3.0 ép xung dễ dàng bằng 1 click", "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=800&auto=format&fit=crop", false, "RAM", "Intel"));
            productRepository.save(create("MSI G.Skill Trident Z5 RGB 32GB DDR5", 3500000, "RAM DDR5 cao cấp phiên bản MSI hợp tác, LED RGB lung linh", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=800&auto=format&fit=crop", false, "RAM", "MSI"));

            // Mainboard
            productRepository.save(create("ASUS ROG Maximus Z790 Hero", 16490000, "Mainboard cao cấp hỗ trợ CPU Intel thế hệ 12, 13, 14, dàn phase nguồn khủng", "https://images.unsplash.com/photo-1616440347437-b1c73416efc2?q=80&w=800&auto=format&fit=crop", true, "MAINBOARD", "ASUS"));
            productRepository.save(create("MSI MAG B760M Mortar WiFi", 4290000, "Mainboard quốc dân thiết kế chắc chắn, hỗ trợ wifi 6E, tản nhiệt dày dặn", "https://images.unsplash.com/photo-1563770660941-20978e870e26?q=80&w=800&auto=format&fit=crop", false, "MAINBOARD", "MSI"));
            productRepository.save(create("ASUS TUF Gaming B650-Plus WiFi", 5490000, "Hỗ trợ socket AM5 cho CPU AMD Ryzen 7000/8000 series, siêu bền bỉ", "https://images.unsplash.com/photo-1551645121-d1034da75057?q=80&w=800&auto=format&fit=crop", false, "MAINBOARD", "ASUS"));
            productRepository.save(create("MSI MEG X670E ACE", 18990000, "Bo mạch chủ AMD cao cấp nhất, hỗ trợ PCIe 5.0, M.2 Gen 5 cực nhanh", "https://images.unsplash.com/photo-1600541519401-4458d197aa8c?q=80&w=800&auto=format&fit=crop", false, "MAINBOARD", "MSI"));

            // Power Supply
            productRepository.save(create("ASUS ROG Thor 850W Platinum II", 6490000, "Nguồn máy tính cao cấp với màn hình OLED hiển thị công suất thực, chuẩn Platinum", "https://images.unsplash.com/photo-1616588589676-62b3bd4ff6d2?q=80&w=800&auto=format&fit=crop", false, "POWER_SUPPLY", "ASUS"));
            productRepository.save(create("MSI MAG A850GL 850W 80 Plus Gold", 2990000, "Nguồn chuẩn PCIe 5.0 và ATX 3.0 thế hệ mới, cáp rời dẹt gọn gàng", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=800&auto=format&fit=crop", false, "POWER_SUPPLY", "MSI"));
            productRepository.save(create("ASUS TUF Gaming 750W Gold", 2790000, "Nguồn chuẩn Gold bền bỉ đạt chuẩn quân đội, bảo hành 6 năm", "https://images.unsplash.com/photo-1563986768609-322da13575f3?q=80&w=800&auto=format&fit=crop", false, "POWER_SUPPLY", "ASUS"));

            // Accessories (Homepage & Accessories page, not on /linh-kien /may-bo)
            productRepository.save(create("Keychron Q1 Max Wireless", 4250000, "Bàn phím cơ Custom cao cấp vỏ nhôm CNC đầm tay, switch Gateron gõ cực êm", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=800&auto=format&fit=crop", false, "KEYBOARD", "Keychron"));
            productRepository.save(create("Bàn phím cơ AKKO 3098B Multi-modes", 2250000, "Bàn phím cơ layout 98 phím gọn gàng, kết nối 3 chế độ linh hoạt, AKKO CS switch", "https://images.unsplash.com/photo-1587829741301-dc798b83add3?q=80&w=800&auto=format&fit=crop", false, "KEYBOARD", "AKKO"));
            productRepository.save(create("Logitech G Pro X Superlight 2", 3590000, "Chuột chơi game không dây siêu nhẹ cho game thủ chuyên nghiệp, mắt đọc HERO 2", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=800&auto=format&fit=crop", false, "MOUSE", "Logitech"));
            productRepository.save(create("Chuột Razer DeathAdder V3 Pro", 3490000, "Chuột gaming công thái học siêu nhẹ 63g, kết nối không dây Razer HyperSpeed", "https://images.unsplash.com/photo-1625842268584-8f32903a671c?q=80&w=800&auto=format&fit=crop", true, "MOUSE", "Razer"));
            productRepository.save(create("ASUS ROG Swift PG27AQDM", 22990000, "Màn hình Gaming OLED 27 inch siêu đẹp, độ phân giải 2K, tần số quét 240Hz", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=800&auto=format&fit=crop", false, "MONITOR", "ASUS"));
            productRepository.save(create("Màn hình Dell UltraSharp U2723QE 4K", 11490000, "Đỉnh cao hiển thị đồ họa, kích thước 27 inch 4K IPS Black, độ bao phủ màu 100% sRGB", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=800&auto=format&fit=crop", true, "MONITOR", "Dell"));

            // PC Systems (Máy bộ)
            // PC Gaming
            productRepository.save(create("PC Gaming TECH-PC Dragon MSI Edition", 18990000, "Cấu hình chiến game mượt mà, CPU Core i5-13400F | RAM 16GB | RTX 4060 | SSD 512GB, case MSI Forge cao cấp", "https://images.unsplash.com/photo-1531297484001-80022131f5a1?q=80&w=800&auto=format&fit=crop", true, "PC_GAMING", "MSI"));
            productRepository.save(create("PC Gaming ASUS ROG Hyperion RTX 4080 Super", 59990000, "Quái vật gaming tối thượng, CPU Core i9-14900K | RAM 32GB DDR5 | RTX 4080 Super | SSD 2TB, case ROG Hyperion GR701", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=800&auto=format&fit=crop", true, "PC_GAMING", "ASUS"));

            // PC Workstation
            productRepository.save(create("PC Workstation Dual Xeon Render 3D", 32500000, "Chuyên dụng cho đồ họa nặng và render, Dual Xeon E5-2680v4 | RAM 64GB DDR4 | SSD 1TB | RTX 3060 12GB", "https://images.unsplash.com/photo-1593640408182-31c70c8268f5?q=80&w=800&auto=format&fit=crop", false, "PC_WORKSTATION", "TECH-PC"));
            productRepository.save(create("PC Workstation ASUS ProArt AMD Threadripper", 89990000, "Đỉnh cao đồ họa chuyên nghiệp, AMD Threadripper 7960X | RAM 128GB DDR5 ECC | SSD 2TB | RTX 4090 24GB", "https://images.unsplash.com/photo-1548345680-f5475ea5df84?q=80&w=800&auto=format&fit=crop", true, "PC_WORKSTATION", "ASUS"));

            // PC Office
            productRepository.save(create("PC Văn Phòng Dell Vostro 3020 Tower", 10490000, "Gọn gàng tin cậy cho công sở, Intel Core i3-13100 | RAM 8GB DDR4 | SSD 512GB | Windows 11 Home bản quyền", "https://images.unsplash.com/photo-1587831990711-23ca6441447b?q=80&w=800&auto=format&fit=crop", false, "PC_OFFICE", "Dell"));
            productRepository.save(create("PC Học tập TECH-PC Office Slim Intel", 6890000, "Cấu hình tối ưu học tập trực tuyến, Intel Core i3-12100 | RAM 8GB | SSD 256GB | Case Slim nhỏ gọn", "https://images.unsplash.com/photo-1573164713988-8665fc963095?q=80&w=800&auto=format&fit=crop", false, "PC_OFFICE", "TECH-PC"));
        }

        if (userRepository.findByEmail("admin@techpc.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin Manager");
            admin.setEmail("admin@techpc.com");
            admin.setPhone("0123456789");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("user@techpc.com").isEmpty()) {
            User user = new User();
            user.setName("Nguyễn Văn A");
            user.setEmail("user@techpc.com");
            user.setPhone("0901234567");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            userRepository.save(user);
        }

        // Tự động băm mật khẩu thô của người dùng cũ sang BCrypt
        for (User u : userRepository.findAll()) {
            if (u.getPassword() != null && !u.getPassword().startsWith("$2a$") && !u.getPassword().startsWith("$2b$") && !u.getPassword().startsWith("$2y$")) {
                u.setPassword(passwordEncoder.encode(u.getPassword()));
                userRepository.save(u);
            }
        }

        if (orderRepository.count() == 0) {
            List<Product> products = productRepository.findAll();
            if (!products.isEmpty()) {
                Product p1 = products.get(0);
                Product p2 = products.size() > 1 ? products.get(1) : p1;
                Product p3 = products.size() > 2 ? products.get(2) : p1;

                // Order 1: PENDING
                Order o1 = new Order("Nguyễn Văn A", "a.nguyen@example.com", "0901234567", "123 Đường Lê Lợi, Quận 1, TP. HCM", 0, "PENDING", LocalDateTime.now().minusHours(5));
                OrderItem item1_1 = new OrderItem(o1, p1, 1, p1.getPrice());
                OrderItem item1_2 = new OrderItem(o1, p3, 2, p3.getPrice());
                o1.addItem(item1_1);
                o1.addItem(item1_2);
                o1.setTotalAmount(item1_1.getPrice() * item1_1.getQuantity() + item1_2.getPrice() * item1_2.getQuantity());
                orderRepository.save(o1);

                // Order 2: DELIVERED
                Order o2 = new Order("Trần Thị B", "b.tran@example.com", "0918765432", "456 Đường Nguyễn Huệ, Quận 3, TP. HCM", 0, "DELIVERED", LocalDateTime.now().minusDays(2));
                OrderItem item2_1 = new OrderItem(o2, p2, 1, p2.getPrice());
                o2.addItem(item2_1);
                o2.setTotalAmount(item2_1.getPrice() * item2_1.getQuantity());
                orderRepository.save(o2);

                // Order 3: CONFIRMED
                Order o3 = new Order("Lê Hoàng C", "c.le@example.com", "0934567890", "789 Đường Trần Hưng Đạo, Quận 5, TP. HCM", 0, "CONFIRMED", LocalDateTime.now().minusDays(1));
                OrderItem item3_1 = new OrderItem(o3, p2, 1, p2.getPrice());
                OrderItem item3_2 = new OrderItem(o3, p3, 1, p3.getPrice());
                o3.addItem(item3_1);
                o3.addItem(item3_2);
                o3.setTotalAmount(item3_1.getPrice() * item3_1.getQuantity() + item3_2.getPrice() * item3_2.getQuantity());
                orderRepository.save(o3);
            }
        }

        if (reviewRepository.count() == 0) {
            List<Product> products = productRepository.findAll();
            if (!products.isEmpty()) {
                Product p1 = products.stream().filter(p -> p.getName().contains("ROG Strix G16")).findFirst().orElse(products.get(0));
                Product p2 = products.stream().filter(p -> p.getName().contains("MacBook Air")).findFirst().orElse(products.size() > 1 ? products.get(1) : products.get(0));
                Product p3 = products.stream().filter(p -> p.getName().contains("RTX 4080 Super")).findFirst().orElse(products.size() > 8 ? products.get(8) : products.get(0));
                
                reviewRepository.save(new Review(p1, "Nguyễn Minh Tuấn", 5, "Máy quá mạnh mượt, chơi game AAA max settings tản nhiệt cực tốt không bị quá nhiệt. Rất đáng đồng tiền bát gạo!"));
                reviewRepository.save(new Review(p1, "Trần Hoàng Nam", 4, "Hiệu năng thì không có gì để bàn, tuy nhiên máy hơi nặng và cục sạc to, bù lại sạc nhanh. Ship hàng cực tốc độ."));
                reviewRepository.save(new Review(p2, "Lê Thùy Dương", 5, "Màu Starlight siêu đẹp, pin dùng cả ngày làm việc văn phòng không cần mang sạc. Gọn nhẹ, màn hình hiển thị sắc nét."));
                reviewRepository.save(new Review(p2, "Phan Anh Tuấn", 4, "Máy mỏng nhẹ đẹp, dùng văn phòng rất mượt. Điểm trừ là Ram 16GB dùng nhiều tác vụ nặng hơi tràn tí, cơ mà cơ bản là quá tốt rồi."));
                reviewRepository.save(new Review(p3, "Hoàng Gia Bảo", 5, "VGA siêu chất lượng từ ASUS ROG, tản nhiệt to nạc nhe, chạy cực mát và êm ái. Chiến game 4K mượt mà!"));
            }
        }
    }

    private Product create(String name, double price, String desc, String imageUrl, boolean isHot) {
        return new Product(name, price, desc, imageUrl, isHot);
    }

    private Product create(String name, double price, String desc, String imageUrl, boolean isHot, String category, String brand) {
        return new Product(name, price, desc, imageUrl, isHot, category, brand);
    }
}
