package com.example.linguaspace;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Liveness for the hosting platform. Does not touch the database. */
    @GetMapping("/healthz")
    public String live() {
        return "ok";
    }

    /** Readiness: confirms the database is reachable. Use it to verify a new deployment. */
    @GetMapping("/healthz/ready")
    public ResponseEntity<String> ready() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(3)) {
                return ResponseEntity.ok("ok");
            }
        } catch (SQLException e) {
            // reported as unavailable below; details stay out of the public response
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("database unavailable");
    }
}
