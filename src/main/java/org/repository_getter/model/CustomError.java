package org.repository_getter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomError {
    private int status;
    private String message;
}