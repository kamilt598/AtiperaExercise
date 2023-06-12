package org.repository_getter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Repository {
    private String name;
    private String login;
    private List<Branch> branchList;
}
