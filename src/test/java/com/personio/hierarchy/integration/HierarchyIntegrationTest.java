package com.personio.hierarchy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personio.hierarchy.dto.HierarchyDTO;
import com.personio.hierarchy.fixtures.HierarchyFixtures;
import com.personio.hierarchy.fixtures.JwtFixture;
import com.personio.hierarchy.repository.HierarchyRepository;
import lombok.SneakyThrows;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {HierarchyIntegrationTest.Initializer.class})
class HierarchyIntegrationTest {

  @ClassRule
  public static PostgreSQLContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:14.0")
          .withDatabaseName("integration-tests-db")
          .withUsername("user")
          .withPassword("password");

  @Autowired private ObjectMapper objectMapper;
  @Autowired private HierarchyRepository repository;
  @Autowired private MockMvc mockMvc;

  @BeforeAll
  static void setUp() {
    postgreSQLContainer.start();
  }

  @AfterEach
  void afterEach() {
    repository.deleteAll();
  }

  @SneakyThrows
  private RequestBuilder createRequestBuilderPostHierarchy(Map<String, String> input) {
    return MockMvcRequestBuilders.post("/api/v1/hierarchy")
        .with(jwt().jwt(JwtFixture.jwtFixture()))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(input));
  }

  private RequestBuilder createRequestBuilderGetSupervisorsByName(String name) {
    return MockMvcRequestBuilders.get("/api/v1/hierarchy/{name}/supervisors", name)
        .with(jwt().jwt(JwtFixture.jwtFixture()));
  }

  private RequestBuilder createRequestBuilderGetHierarchy() {
    return MockMvcRequestBuilders.get("/api/v1/hierarchy")
        .with(jwt().jwt(JwtFixture.jwtFixture()))
        .contentType(MediaType.APPLICATION_JSON);
  }

  @Test
  @SneakyThrows
  void postHierarchy() {
    // Given
    Map<String, String> input = HierarchyFixtures.getHierarchyInputDTO();
    // When
    RequestBuilder requestBuilder = createRequestBuilderPostHierarchy(input);
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    var body = result.getResponse().getContentAsString();
    // Then
    assertEquals(200, result.getResponse().getStatus(), "Should return success 200");
    HierarchyDTO<String> expected = HierarchyFixtures.getHierarchyDTO();
    var reader = objectMapper.readerFor(Map.class);
    var actual = reader.readValue(body);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void getHierarchy() {
    // Given
    Map<String, String> input = HierarchyFixtures.getHierarchyInputDTO();
    // When
    RequestBuilder requestBuilderCreate = createRequestBuilderPostHierarchy(input);
    mockMvc.perform(requestBuilderCreate).andReturn();

    RequestBuilder requestBuilderFind = createRequestBuilderGetHierarchy();
    MvcResult result = mockMvc.perform(requestBuilderFind).andReturn();
    var body = result.getResponse().getContentAsString();
    // Then
    assertEquals(200, result.getResponse().getStatus(), "Should return success 200");
    HierarchyDTO<String> expected = HierarchyFixtures.getHierarchyDTO();
    var reader = objectMapper.readerFor(Map.class);
    Map<String, Object> actual = reader.readValue(body);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void getSupervisors() {
    // Given
    Map<String, String> input = HierarchyFixtures.getHierarchyInputDTO();
    // When
    RequestBuilder requestBuilderCreate = createRequestBuilderPostHierarchy(input);
    mockMvc.perform(requestBuilderCreate).andReturn();

    var name = "Barbara";
    RequestBuilder requestBuilderFind = createRequestBuilderGetSupervisorsByName(name);
    MvcResult result = mockMvc.perform(requestBuilderFind).andReturn();
    var body = result.getResponse().getContentAsString();
    // Then
    assertEquals(200, result.getResponse().getStatus(), "Should return success 200");
    var reader = objectMapper.readerFor(List.class);
    List<String> actual = reader.readValue(body);
    assertThat(actual).isEqualTo(Arrays.asList("Nick", "Sophie"));
  }

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword())
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
