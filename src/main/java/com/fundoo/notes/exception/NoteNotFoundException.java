
package com.fundoo.notes.exception;

/*
 * Class Name  : NoteNotFoundException
 * Description : Thrown when a requested note does not exist
 *               or does not belong to the authenticated user.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String message) {
        super(message);
    }
}
