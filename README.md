# 🏢 HRMS — Human Resource Management System (Backend)

> Enterprise-grade HRMS backend with multi-role access control built with Spring Boot

🔗 **Frontend Repo:** https://github.com/HetBhimani25/hrms-frontend

---

## ✨ Features

- 👥 Multi-role access: Admin, HR, Manager, Employee
- 🔐 JWT Authentication with Role-Based Access Control (RBAC)
- 👤 Employee CRUD operations
- 🌴 Leave management with hierarchical approval workflows
- 📊 Role-based dashboards and data access
- 🏗️ Layered architecture (Controller → Service → Repository → DTO)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security + JWT |
| **Database** | PostgreSQL |
| **ORM** | Spring Data JPA (Hibernate) |
| **Build Tool** | Maven |
| **API Testing** | Postman |

---

## 🚀 Run Locally

```bash
# Clone repo
git clone https://github.com/HetBhimani25/hrms-backend.git

# Configure application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your_jwt_secret

# Run
mvn spring-boot:run
```

---

## 👨‍💻 Author

**Het Bhimani**  
[GitHub](https://github.com/HetBhimani25) · [LinkedIn](https://www.linkedin.com/in/hetbhimani)
