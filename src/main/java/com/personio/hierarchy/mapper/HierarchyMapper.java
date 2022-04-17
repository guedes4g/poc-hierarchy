package com.personio.hierarchy.mapper;

import com.personio.hierarchy.dto.HierarchyDTO;
import com.personio.hierarchy.entity.Hierarchy;
import com.personio.hierarchy.entity.HierarchyId;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;

@Component
public class HierarchyMapper {

  public HierarchyDTO<String> toDTO(Hierarchy hierarchy) {
    var root = new HierarchyDTO<String>();
    HierarchyDTO<String> dto = root;
    var q = new ArrayDeque<Pair<Hierarchy, HierarchyDTO<String>>>();
    q.push(Pair.of(hierarchy, dto));
    Hierarchy current;
    while (!q.isEmpty()) {
      var pair = q.pop();
      current = pair.getFirst();
      dto = pair.getSecond();
      var childDTO = new HierarchyDTO<String>();
      for (var child : current.getSupervises()) {
        q.push(Pair.of(child, childDTO));
        dto.put(current.getId().getName(), childDTO);
      }
      if (current.getSupervises().isEmpty()) {
        var emptyDTO = new HierarchyDTO<String>();
        dto.put(current.getId().getName(), emptyDTO);
      }
    }
    return root;
  }

  public Hierarchy toEntity(String name, String userId) {
    return Hierarchy.builder().id(HierarchyId.builder().name(name).userId(userId).build()).build();
  }
}
