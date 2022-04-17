package com.personio.hierarchy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class HierarchyId implements Serializable {
  @Column private String name;

  @Column(updatable = false)
  private String userId;
}
