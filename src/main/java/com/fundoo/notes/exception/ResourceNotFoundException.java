package com.fundoo.notes.exception;

/*
 * Class Name  : ResourceNotFoundException
 * Description : Thrown when a label or reminder is not found for the user.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
