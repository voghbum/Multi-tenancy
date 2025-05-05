package com.voghbum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class EmployeePerformanceController {
    Logger logger = LoggerFactory.getLogger(EmployeePerformanceController.class);
    private final Random random = new Random();

    public EmployeePerformanceController() {
    }

    @GetMapping("/performance/rating")
    public ResponseEntity<Integer> signup(@RequestParam String id) {
        logger.info("request with id: {}", id);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(random.nextInt(100));
    }
} 