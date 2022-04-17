package com.personio.hierarchy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(
    indexes = {
      @Index(name = "hierarchy_user_id_idx", columnList = "userId"),
      @Index(name = "hierarchy_user_id_root_idx", columnList = "userId, root")
    })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "hierarchy")
public class Hierarchy {
  @EqualsAndHashCode.Include @EmbeddedId private HierarchyId id;

  @Column private Boolean root;

  @Builder.Default
  @OneToMany(mappedBy = "supervises", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonBackReference
  private List<Hierarchy> supervises = new ArrayList<>();

  @ToString.Exclude
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonManagedReference
  private Hierarchy supervisor;

  @PrePersist
  private void setRoot() {
    root = supervisor == null;
  }

  public void setSupervises(List<Hierarchy> supervises) {
    this.supervises = supervises;
    this.supervises.forEach(s -> s.setSupervisor(this));
  }
}
