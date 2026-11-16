
package com.fundoo.notes.service;

/*
 * Class Name  : OtpService
 * Description : Defines OTP generation and verification operations.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public interface OtpService {

    String generateOtp(String email);

    boolean verifyOtp(String email, String otp);

}
