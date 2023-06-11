package org.repository_getter.service;

import org.repository_getter.model.Branch;

import java.util.List;

public interface BranchService {
    List<Branch> getBranches(String username, String repositoryName);
}
