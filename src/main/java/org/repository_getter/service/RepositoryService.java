package org.repository_getter.service;

import org.repository_getter.model.Repository;

import java.util.List;

public interface RepositoryService {
    List<Repository> getRepositories(String username);
}
