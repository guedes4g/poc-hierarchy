package com.personio.hierarchy.repository;

import com.personio.hierarchy.entity.Hierarchy;
import com.personio.hierarchy.entity.HierarchyId;
import com.personio.hierarchy.entity.HierarchyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface HierarchyRepository extends JpaRepository<Hierarchy, HierarchyId> {

  @Modifying(clearAutomatically=true, flushAutomatically=true)
  void deleteByIdUserId(String userId);

  @Query(
      "select new com.personio.hierarchy.entity.HierarchyProjection(node.id.name, node.supervisor.id.name) "
          + "from hierarchy node "
          + "where node.id.userId = :userId")
  List<HierarchyProjection> findNameAndSupervisorNameByIdUserId(String userId);

  default Map<String, String> findByIdUserId(String userId) {
    var tuples = findNameAndSupervisorNameByIdUserId(userId);
    HashMap<String, String> flatHierarchyMap = new HashMap<>();
    for (var t : tuples) {
      flatHierarchyMap.put(t.getName(), t.getParentName());
    }
    return flatHierarchyMap;
  }

  @Query(
      value =
          "with recursive hierarchycte as ( "
              + "select "
              + "    h.\"name\", "
              + "    h.user_id, "
              + "    h.supervisor_name, "
              + "    h.supervisor_user_id "
              + "from "
              + "    \"hierarchy\" h "
              + "where "
              + "    h.\"name\" = :employee "
              + "    and h.user_id = :userId "
              + "union all "
              + "select "
              + "    p.\"name\", "
              + "    p.user_id, "
              + "    p.supervisor_name, "
              + "    p.supervisor_user_id "
              + "from "
              + "\"hierarchy\" p "
              + "    inner join hierarchycte as pcte "
              + "    on p.name = pcte.supervisor_name "
              + "    and p.user_id = pcte.supervisor_user_id "
              + ") "
              + "select "
              + "    cte.name as name "
              + "from "
              + "    hierarchycte cte "
              + "offset 1 "
              + "limit :limit ",
      nativeQuery = true)
  List<String> findSupervisors(String userId, String employee, int limit);
}
