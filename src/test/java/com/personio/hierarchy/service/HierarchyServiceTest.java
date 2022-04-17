package com.personio.hierarchy.service;

import com.personio.hierarchy.entity.Hierarchy;
import com.personio.hierarchy.fixtures.HierarchyFixtures;
import com.personio.hierarchy.mapper.HierarchyMapper;
import com.personio.hierarchy.repository.HierarchyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HierarchyServiceTest {

  @Mock private HierarchyMapper mapper;
  @Mock private HierarchyRepository repository;
  @InjectMocks private HierarchyService service;

  @Test
  void findSupervisors() {
    // Given
    var flatHierarchy = HierarchyFixtures.getHierarchyInputDTO();
    flatHierarchy.put("Jonas", null);
    // When
    Mockito.when(repository.findSupervisors("userId", "Nick", 2))
        .thenReturn(Arrays.asList("Sophie", "Jonas"));
    var actual = service.findSupervisors("userId", "Nick", 2);
    // Then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringActualNullFields()
        .isEqualTo(Arrays.asList("Sophie", "Jonas"));
  }

  @Test
  void getByUserId() {
    // Given
    var flatHierarchy = HierarchyFixtures.getHierarchyInputDTO();
    flatHierarchy.put("Jonas", null);
    // When
    Mockito.when(mapper.toEntity(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
    Mockito.when(repository.findByIdUserId("userId")).thenReturn(flatHierarchy);
    var actual = service.getByUserId("userId");
    // Then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringActualNullFields()
        .isEqualTo(HierarchyFixtures.getHierarchy());
  }

  @Test
  void create() {
    // Given
    var flatHierarchy = HierarchyFixtures.getHierarchyInputDTO();
    // When
    Mockito.when(mapper.toEntity(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
    Mockito.when(repository.save(Mockito.any())).thenAnswer(a -> a.getArgument(0, Hierarchy.class));
    var actual = service.create("userId", flatHierarchy);
    // Then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringActualNullFields()
        .isEqualTo(HierarchyFixtures.getHierarchy());
  }
}
