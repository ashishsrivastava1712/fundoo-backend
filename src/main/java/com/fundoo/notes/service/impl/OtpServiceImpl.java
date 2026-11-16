
package com.fundoo.notes.service.impl;

import com.fundoo.notes.service.OtpService;
import com.fundoo.notes.service.RedisService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

/*
 * Class Name  : OtpServiceImpl
 * Description : Generates OTPs and stores them temporarily in Redis.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Service
public class OtpServiceImpl implements OtpService {

    private final RedisService redisService;

    @Value("${app.redis.otp-expiration}")
    private long otpExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpServiceImpl(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public String generateOtp(String email) {

        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        String key = "OTP:" + email;

        redisService.save(
                key,
                otp,
                Duration.ofSeconds(otpExpiration)
        );

        return otp;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {

        String key = "OTP:" + email;

        Object storedOtp = redisService.get(key);

        if (storedOtp == null || !storedOtp.toString().equals(otp)) {
            return false;
        }

        redisService.delete(key);

        return true;
    }
}
