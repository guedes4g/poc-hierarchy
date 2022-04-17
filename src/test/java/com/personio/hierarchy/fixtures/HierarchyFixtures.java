package com.personio.hierarchy.fixtures;

import com.personio.hierarchy.dto.HierarchyDTO;
import com.personio.hierarchy.entity.Hierarchy;
import com.personio.hierarchy.entity.HierarchyId;

import java.util.HashMap;
import java.util.Map;

/** Case 1 */
public class HierarchyFixtures {

  public static Map<String, String> getHierarchyInputDTO() {
    Map<String, String> input = new HashMap<>();
    input.put("Pete", "Nick");
    input.put("Barbara", "Nick");
    input.put("Nick", "Sophie");
    input.put("Sophie", "Jonas");
    return input;
  }

  public static HierarchyDTO<String> getHierarchyDTO() {
    HierarchyDTO<String> expected = new HierarchyDTO<>();
    expected.put("Jonas", new HierarchyDTO<>());
    expected.get("Jonas").put("Sophie", new HierarchyDTO<>());
    expected.get("Jonas").get("Sophie").put("Nick", new HierarchyDTO<>());
    expected.get("Jonas").get("Sophie").get("Nick").put("Pete", new HierarchyDTO<>());
    expected.get("Jonas").get("Sophie").get("Nick").put("Barbara", new HierarchyDTO<>());
    return expected;
  }

  public static Hierarchy getHierarchy() {
    Hierarchy jonas = Hierarchy.builder().id(new HierarchyId("Jonas", "userId")).build();
    Hierarchy sophie = Hierarchy.builder().id(new HierarchyId("Sophie", "userId")).build();
    Hierarchy nick = Hierarchy.builder().id(new HierarchyId("Nick", "userId")).build();
    Hierarchy pete = Hierarchy.builder().id(new HierarchyId("Pete", "userId")).build();
    Hierarchy barbara = Hierarchy.builder().id(new HierarchyId("Barbara", "userId")).build();
    jonas.setRoot(true);
    jonas.getSupervises().add(sophie);
    sophie.setSupervisor(jonas);
    sophie.getSupervises().add(nick);
    nick.setSupervisor(sophie);
    nick.getSupervises().add(pete);
    nick.getSupervises().add(barbara);
    pete.setSupervisor(nick);
    barbara.setSupervisor(nick);
    return jonas;
  }
}
