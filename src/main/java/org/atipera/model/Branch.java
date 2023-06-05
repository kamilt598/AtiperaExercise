package org.atipera.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Branch {
    private String branchName;
    private String sha;
}
