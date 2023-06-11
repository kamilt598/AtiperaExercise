package org.repository_getter.model;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Repository {
    private String name;
    private String login;
    private List<Branch> branchList;
}
