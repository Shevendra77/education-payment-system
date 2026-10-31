package com.cfs.PaymentService.service;

import com.cfs.PaymentService.model.Course;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseCatalogService
{

    private final List<Course> courses= List.of(

            new Course(
                    "java-spring",
                    "Java Spring Boot",
                    "Build production-ready REST APIs with Spring Boot", 499
            ),

            new Course(
                    "kafka-basics",
                    "kafka basics",
                    "Learn producer, consumer, topics",999
            ),

            new Course(
                    "fullstack-web",
                    "Full stack web",
                    "HTML, CSS, JS, APIs and deployment",5999
            ),

            new Course(
                    "core-java",
                    "Core java",
                    "Learn basic foundation of java",599
            ),

            new Course(
                    "microservices",
                    "Microservices with Spring Boot",
                    "Learn microservices architecture, Eureka Server, API Gateway, Feign Client, Load Balancing and distributed systems",
                    4999
            ),

            new Course(
                    "docker",
                    "Docker & Containerization",
                    "Learn Docker images, containers, Docker Compose, networking, volumes and deploying applications using Docker",
                    2999
            )

    );

    public List<Course> findAll()
    {
        return courses;
    }

    public Course findById(String id)
    {
        return courses.stream()
                .filter(c->c.id().equals(id))
                .findFirst()
                .orElseThrow(()->new IllegalArgumentException("Invalid course id: "+ id));
    }
}
