
package com.fundoo.notes.controller;

import com.fundoo.notes.service.OtpService;
import com.fundoo.notes.service.RedisService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/*
 * Class Name  : RedisController
 * Description : Exposes Redis and OTP testing endpoints.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final RedisService redisService;
    private final OtpService otpService;

    public RedisController(
            RedisService redisService,
            OtpService otpService) {

        this.redisService = redisService;
        this.otpService = otpService;
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> save(
            @RequestParam("key") String key,
            @RequestParam("value") String value) {

        redisService.save(key, value, Duration.ofMinutes(10));

        return ResponseEntity.ok(
                Map.of("message", "Data saved successfully")
        );
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, Object>> get(
            @PathVariable("key") String key) {

        Object value = redisService.get(key);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }

    @DeleteMapping("/delete/{key}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable("key") String key) {

        redisService.delete(key);

        return ResponseEntity.ok(
                Map.of("message", "Data deleted successfully")
        );
    }

    @PostMapping("/otp/generate")
    public ResponseEntity<Map<String, String>> generateOtp(
            @RequestParam("email") String email) {

        String otp = otpService.generateOtp(email);

        return ResponseEntity.ok(
                Map.of("email", email, "otp", otp)
        );
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp) {

        boolean verified = otpService.verifyOtp(email, otp);

        if (!verified) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid or expired OTP")
            );
        }

        return ResponseEntity.ok(
                Map.of("message", "OTP verified successfully")
        );
    }
}
