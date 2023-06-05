package org.atipera.model;

import lombok.Data;

import java.util.List;

@Data
public class Repository {
    private String name;
    private String login;
    private List<Branch> branchList;
}
