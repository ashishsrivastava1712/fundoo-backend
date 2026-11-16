
package com.fundoo.notes.event;

/*
 * Class Name  : UserRegistrationEvent
 * Description : Stores user registration details for JMS messaging.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public record UserRegistrationEvent(
        Long userId,
        String firstName,
        String email
) {
}
