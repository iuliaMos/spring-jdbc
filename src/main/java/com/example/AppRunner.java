package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void run(String... args) throws Exception {

        log.info("creating tables");

        jdbcTemplate.execute("DROP TABLE customer IF EXISTS ");
        jdbcTemplate.execute("CREATE TABLE customer (" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

        List<Object[]> newCustomers = Arrays.asList("Ana Maria", "Jeff Woo", "Ion Popescu",
                "Andrei Ionescu").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        newCustomers.forEach(customer -> log.info(String.format("Inserting Record: %s %s", customer[0], customer[1])));

        jdbcTemplate.batchUpdate("INSERT INTO customer(first_name, last_name) VALUES (?, ?)", newCustomers);

        log.info("Quering for customer where first_name = Jeff");
        jdbcTemplate.query("SELECT * from customer where first_name = ?", new Object[] { "Jeff" },
                (rs, roNumber) -> new Customer(rs.getLong("id"), rs.getString("first_name"),
                        rs.getString("last_name")))
                .forEach(customer -> log.info(customer.toString()));
    }
}
