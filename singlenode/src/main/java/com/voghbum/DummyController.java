package com.voghbum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DummyController {
    Logger logger = LoggerFactory.getLogger(DummyController.class);

    public DummyController() {
    }

    @GetMapping("/process")
    public ResponseEntity<Void> signup(@RequestParam String id) {
        logger.info("request with id: {}", id);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
} 