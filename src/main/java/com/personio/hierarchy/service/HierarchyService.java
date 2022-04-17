package com.personio.hierarchy.service;

import com.personio.hierarchy.entity.Hierarchy;
import com.personio.hierarchy.mapper.HierarchyMapper;
import com.personio.hierarchy.repository.HierarchyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class HierarchyService {

  private final HierarchyRepository repository;
  private final HierarchyMapper mapper;

  public List<String> findSupervisors(String userId, String employee, int limit) {
    return repository.findSupervisors(userId, employee, limit);
  }

  public Hierarchy getByUserId(String userId) {
    var flatHierarchy = repository.findByIdUserId(userId);
    var root =
        flatHierarchy.entrySet().stream()
            .filter(e -> e.getValue() == null)
            .findFirst()
            .map(Map.Entry::getKey)
            .orElseThrow();
    flatHierarchy.remove(root);
    return createHierarchy(root, flatHierarchy, userId);
  }

  @Transactional
  public Hierarchy create(String userId, Map<String, String> flatHierarchy) {
    String possibleRoot = findRoot(flatHierarchy);
    Hierarchy hierarchy = createHierarchy(possibleRoot, flatHierarchy, userId);
    this.repository.deleteByIdUserId(userId);
    hierarchy = this.repository.save(hierarchy);
    return hierarchy;
  }

  private String findRoot(Map<String, String> flatHierarchy) {
    var values = flatHierarchy.values();
    var node =
        values.stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Invalid Input exception: Empty input"));
    var visited = new HashSet<String>();
    while (flatHierarchy.containsKey(node)) {
      visited.add(node);
      node = flatHierarchy.get(node);
      if (visited.contains(node)) {
        throw new RuntimeException("Invalid input: contains loop");
      }
    }
    return node;
  }

  private Hierarchy createHierarchy(
      String possibleRoot, Map<String, String> flatHierarchy, String userId) {
    Map<String, List<String>> graph =
        flatHierarchy.entrySet().stream()
            .collect(
                Collectors.groupingBy(
                    Map.Entry::getValue,
                    Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    var visited = new HashSet<String>();

    Hierarchy root = this.mapper.toEntity(possibleRoot, userId);

    var q = new ArrayDeque<Hierarchy>();
    Hierarchy node;
    q.add(root);
    while (!q.isEmpty()) {
      node = q.pop();
      if (visited.contains(node.getId().getName())) {
        throw new RuntimeException("Invalid input: contains loops");
      }
      visited.add(node.getId().getName());
      List<Hierarchy> children =
          graph.getOrDefault(node.getId().getName(), Collections.emptyList()).stream()
              .map(child -> this.mapper.toEntity(child, userId))
              .collect(Collectors.toList());
      q.addAll(children);
      node.setSupervises(children);
    }
    if (visited.size() < flatHierarchy.size() + 1) {
      throw new RuntimeException("Invalid input: not all nodes are connected");
    }
    return root;
  }
}
