package org.repository_getter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomError {
    private int status;
    private String message;
}
