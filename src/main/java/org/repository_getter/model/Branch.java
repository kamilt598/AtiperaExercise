package org.repository_getter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Branch {
    private String branchName;
    private String sha;
}
