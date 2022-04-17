# Hierarchy API POC

### Running
Use `docker-compose up` to start up the dependencies and the Hierarchy project.

### Documentation
Simple Swagger UI available on http://localhost:8080/swagger-ui/
<img width="1460" alt="image" src="https://user-images.githubusercontent.com/29311988/163731824-d8b78008-75b3-4bda-995e-abc3dcd9aa64.png">

### Authentication
Uses `Keycloak` open id connect

### Improvements
- Error Handling (BACKEND)
  - Use error filter 
  - Use custom errors instead of Generic Java erros
- Add and Improve tests 
  - only few happy path available since this is a POC
  - should test corner cases such as loop hierarchy (validation is implemented but no automated tests are available)
- Securty
  - Restrict docker image user permissions
