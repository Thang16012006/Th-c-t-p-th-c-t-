package com.shop.laptopshop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ExportDbTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void exportDatabase() {
        try {
            jdbcTemplate.execute("SCRIPT TO './data/laptopshopdb_backup.sql'");
            System.out.println("=================================================");
            System.out.println("DATABASE EXPORTED SUCCESSFULY TO ./data/laptopshopdb_backup.sql");
            System.out.println("=================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exportForMysql() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("./data/laptopshop_mysql_import.sql"))) {
            writer.println("-- SQL Dump for MySQL / phpMyAdmin");
            writer.println("SET FOREIGN_KEY_CHECKS = 0;");
            writer.println("TRUNCATE TABLE order_items;");
            writer.println("TRUNCATE TABLE review;");
            writer.println("TRUNCATE TABLE product;");
            writer.println("TRUNCATE TABLE orders;");
            writer.println("TRUNCATE TABLE users;");
            writer.println("SET FOREIGN_KEY_CHECKS = 1;\n");

            // 1. Export USERS
            exportTable(writer, "users", new String[]{"id", "email", "name", "password", "phone", "role"});

            // 2. Export PRODUCT
            exportTable(writer, "product", new String[]{"id", "brand", "category", "description", "image_url", "is_hot", "name", "price"});

            // 3. Export ORDERS
            exportTable(writer, "orders", new String[]{"id", "customer_email", "customer_name", "customer_phone", "order_date", "shipping_address", "status", "total_amount", "payment_method"});

            // 4. Export ORDER_ITEMS
            exportTable(writer, "order_items", new String[]{"id", "price", "quantity", "order_id", "product_id"});

            // 5. Export REVIEW
            exportTable(writer, "review", new String[]{"id", "author", "content", "created_at", "rating", "product_id"});

            System.out.println("=================================================");
            System.out.println("MYSQL IMPORT SQL GENERATED AT ./data/laptopshop_mysql_import.sql");
            System.out.println("=================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportTable(PrintWriter writer, String tableName, String[] columns) {
        String cols = String.join(", ", columns);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT " + cols + " FROM " + tableName);
        if (rows.isEmpty()) return;

        writer.println("-- Data for table " + tableName);
        for (Map<String, Object> row : rows) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableName).append(" (").append(cols).append(") VALUES (");
            for (int i = 0; i < columns.length; i++) {
                Object val = row.get(columns[i].toUpperCase());
                if (val == null) {
                    val = row.get(columns[i]); // try lowercase just in case
                }
                if (val == null) {
                    sb.append("NULL");
                } else if (val instanceof String) {
                    String escapeStr = ((String) val).replace("'", "''");
                    sb.append("'").append(escapeStr).append("'");
                } else if (val instanceof Boolean) {
                    sb.append((Boolean) val ? 1 : 0);
                } else {
                    sb.append(val.toString());
                }
                if (i < columns.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(");");
            writer.println(sb.toString());
        }
        writer.println();
    }
}
