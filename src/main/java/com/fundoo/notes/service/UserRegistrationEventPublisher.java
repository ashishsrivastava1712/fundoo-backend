
package com.fundoo.notes.service;

import com.fundoo.notes.event.UserRegistrationEvent;

/*
 * Class Name  : UserRegistrationEventPublisher
 * Description : Defines the user registration event publishing contract.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public interface UserRegistrationEventPublisher {

    void publish(UserRegistrationEvent event);
}
