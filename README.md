# Kanban Board Backend

A robust RESTful API for a Kanban-style project management application. This back-end provides full CRUD functionality for managing projects, collaborators, and individual issues (tasks), with security handled by Auth0. Please note that this project is currently under development.

## Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Spring Security** (for method-level authorization)
- **PostgreSQL** (Database)
- **JPA (Hibernate)** (Object-Relational Mapping)
- **Docker & Docker Compose** (for local development environment)
- **Maven** (Dependency Management)

## Key Features

- **Identity Management:** User authentication and authorization are handled by Auth0.
- **Role-Based Access Control (RBAC):** Fine-grained permissions for viewing and modifying projects based on user roles (ADMIN, MEMBER).
- **Project Management:** Create, retrieve, update, and delete project boards.
- **Collaborator Management:** Add, remove, and update roles for collaborators on a project.
- **Issue Tracking:** Create, read, update, and delete issues (tasks) within a specific project.
- **Automatic Database Seeding:** Initializes the database with default statuses (e.g., "Backlog," "In Progress") and priorities ("Low," "High") on first run.

## API Endpoints

Authentication is managed by Auth0. All endpoints listed below are protected and require a valid Bearer Token.

### Project Endpoints

| Method   | Endpoint                    | Description                                       |
| :------- | :-------------------------- | :------------------------------------------------ |
| `GET`    | `/api/projects`             | Retrieves a summary of all projects for the user. |
| `POST`   | `/api/projects`             | Creates a new project.                            |
| `GET`    | `/api/projects/{projectId}` | Retrieves a single project by its ID.             |
| `PUT`    | `/api/projects/{projectId}` | Updates a project's name or description.          |
| `DELETE` | `/api/projects/{projectId}` | Deletes a project.                                |

### Collaborator Endpoints

| Method   | Endpoint                                           | Description                            |
| :------- | :------------------------------------------------- | :------------------------------------- |
| `POST`   | `/api/projects/{projectId}/collaborators`          | Adds a collaborator to a project.      |
| `PUT`    | `/api/projects/{projectId}/collaborators/{userId}` | Updates a collaborator's role.         |
| `DELETE` | `/api/projects/{projectId}/collaborators/{userId}` | Removes a collaborator from a project. |

### Issue Endpoints

| Method | Endpoint                                     | Description                         |
| :----- | :------------------------------------------- | :---------------------------------- |
| `GET`  | `/api/projects/{projectId}/issues`           | Retrieves all issues for a project. |
| `POST` | `/api/projects/{projectId}/issues/create`    | Creates a new issue in a project.   |
| `GET`  | `/api/projects/{projectId}/issues/{issueId}` | Retrieves a single issue by its ID. |
| `PUT`  | `/api/projects/{projectId}/issues/{issueId}` | Updates an existing issue.          |

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- Java 21 (or later)
- Maven
- Docker and Docker Compose

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/ky-ler/kanban-backend.git
    ```
2.  **Navigate to the project directory:**
    ```sh
    cd kanban-backend
    ```
3.  **Run PostgreSQL using Docker:**

    - This project includes a `docker-compose.yml` file that will start the service.
    - Start the containers:
      ```sh
      docker-compose up -d
      ```
    - PostgreSQL will be available on port `8778`.

4.  **Configure the Spring Boot application:**

    - Open the `src/main/resources/application.yml` file.
    - Ensure the database credentials match the ones in your `docker-compose.yml` file.
    - Update the `issuer-uri` under `spring.security.oauth2.resourceserver.jwt` to match your Auth0 URL.
    - Update the `audiences` under `spring.security.oauth2.resourceserver.jwt` to match your Auth0 API audience.

5.  **Run the application:**
    ```sh
    mvn spring-boot:run
    ```
    The API will be available at `http://localhost:8080`.
