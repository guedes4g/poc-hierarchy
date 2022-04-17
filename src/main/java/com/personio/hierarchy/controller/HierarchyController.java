package com.personio.hierarchy.controller;

import com.personio.hierarchy.dto.HierarchyDTO;
import com.personio.hierarchy.mapper.HierarchyMapper;
import com.personio.hierarchy.service.HierarchyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hierarchy")
@SecurityRequirement(name = "openApi")
@RequiredArgsConstructor
public class HierarchyController {

  private final HierarchyService service;
  private final HierarchyMapper mapper;

  @PostMapping
  public HierarchyDTO<String> createHierarchy(
      @AuthenticationPrincipal Jwt principal, @RequestBody HashMap<String, String> flatHierarchy) {
    String sub = principal.getSubject();
    return mapper.toDTO(service.create(sub, flatHierarchy));
  }

  @GetMapping
  public HierarchyDTO<String> getHierarchy(@AuthenticationPrincipal Jwt principal) {
    String sub = principal.getSubject();
    return mapper.toDTO(service.getByUserId(sub));
  }

  @GetMapping("{name}/supervisors")
  public List<String> getSupervisors(
      @AuthenticationPrincipal Jwt principal, @PathVariable(value = "name") String name) {
    String sub = principal.getSubject();
    return service.findSupervisors(sub, name, 2);
  }
}
