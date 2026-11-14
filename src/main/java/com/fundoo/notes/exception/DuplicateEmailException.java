/*
 * Class Name  : DuplicateEmailException
 * Description : Custom exception thrown when a user registers
 *               with an email address that already exists.
 * Author      : Ashish Srivastava
 * Date        : 14/11/2026
 */

package com.fundoo.notes.exception;

public class DuplicateEmailException extends RuntimeException {

    // Creates an exception with a descriptive message.
    public DuplicateEmailException(String message) {
        super(message);
    }
}