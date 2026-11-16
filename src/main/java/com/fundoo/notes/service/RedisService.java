
package com.fundoo.notes.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/*
 * Class Name  : RedisService
 * Description : Defines Redis storage operations.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public interface RedisService {

    void save(String key, Object value, Duration duration);

    Object get(String key);

    void delete(String key);

    boolean exists(String key);

    void setExpiry(String key, long timeout, TimeUnit unit);
}
