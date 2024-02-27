package com.alkl1m.taskmanager.dto.auth;

import java.util.Date;
public record ErrorMessage(int statusCode,
                           Date timestamp,
                           String message,
                           String description) {
}
