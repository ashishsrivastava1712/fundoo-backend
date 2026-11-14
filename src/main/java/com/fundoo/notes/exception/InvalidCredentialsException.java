/*
 * Class Name  : InvalidCredentialsException
 * Description : Custom exception thrown when login credentials
 *               are incorrect.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

package com.fundoo.notes.exception;

public class InvalidCredentialsException extends RuntimeException {

    // Creates an exception with a descriptive message.
    public InvalidCredentialsException(String message) {
        super(message);
    }
}