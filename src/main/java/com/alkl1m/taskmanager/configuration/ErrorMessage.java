package com.alkl1m.taskmanager.configuration;

import java.util.Date;

public record ErrorMessage(int statusCode,
                           Date timestamp,
                           String message,
                           String description) {
}
