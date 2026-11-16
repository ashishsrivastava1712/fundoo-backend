
package com.fundoo.notes.service;

import com.fundoo.notes.event.ReminderEvent;

/*
 * Class Name  : ReminderEventPublisher
 * Description : Defines reminder event publishing operations.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public interface ReminderEventPublisher {

    void publish(ReminderEvent event);
}
