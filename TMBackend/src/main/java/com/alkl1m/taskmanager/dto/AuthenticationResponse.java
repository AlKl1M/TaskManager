package com.alkl1m.taskmanager.dto;

import com.alkl1m.taskmanager.enums.UserRole;
import lombok.Data;

public record AuthenticationResponse(String jwt,
                                     UserRole userRole,
                                     Long userId) {}
