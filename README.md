# 🚜 Tractor Field Work Manager

A Spring Boot REST API to manage daily/hourly tractor fieldwork, auto-calculate billing, and generate monthly client statements.

**Built for DevOps learning** — each tool you learn (Docker, K8s, CI/CD, monitoring) can be applied directly to this project.

---

## 📦 Tech Stack

| Layer | Tech |
|-------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Database | H2 (in-memory, swap for Postgres later) |
| Build | Maven |
| Containerization | Docker |

---

## 🚀 Quick Start

### Option 1 — Run locally with Maven
```bash
mvn clean spring-boot:run
```

### Option 2 — Run with Docker
```bash
# Build image
docker build -t tractor-app .

# Run container
docker run -p 8080:8080 tractor-app
```

### Option 3 — Docker Compose
```bash
docker-compose up --build
```

App runs at: **http://localhost:8080**

H2 Console (dev): **http://localhost:8080/h2-console**

---

## 📡 API Endpoints

### Clients
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/clients` | Add new client |
| GET | `/api/clients` | List all clients |
| GET | `/api/clients?search=raju` | Search by name |
| GET | `/api/clients/{id}` | Get client details |
| PUT | `/api/clients/{id}` | Update client |
| DELETE | `/api/clients/{id}` | Delete client |
| GET | `/api/clients/outstanding` | Clients with pending dues |
| POST | `/api/clients/{id}/payment` | Record payment |

### Work Entries
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/work` | Log new field work |
| GET | `/api/work/today` | Today's all work |
| GET | `/api/work/client/{id}` | Work history for client |
| GET | `/api/work/client/{id}/monthly-bill?year=2024&month=6` | Monthly bill |
| GET | `/api/work/range?from=2024-06-01&to=2024-06-30` | Date range |
| POST | `/api/work/{id}/mark-paid` | Mark work as paid |
| DELETE | `/api/work/{id}` | Delete entry |

### Dashboard
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/dashboard/stats` | Overview stats |
| GET | `/api/dashboard/stats?year=2024&month=5` | Specific month stats |

---

## 🧪 Sample API Calls

### Add a client
```bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Venkat Rao",
    "phone": "9876543210",
    "village": "Nalgonda"
  }'
```

### Log field work (auto-calculates amount + adds to client balance)
```bash
curl -X POST http://localhost:8080/api/work \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "workDate": "2024-06-15",
    "workType": "PLOUGHING",
    "fieldDescription": "North paddy field",
    "hoursWorked": 6.5,
    "ratePerHour": 500,
    "notes": "Completed 3 rounds"
  }'
```
> Amount = 6.5 × 500 = ₹3250 — auto-added to client's outstanding balance.

### Get monthly bill for a client
```bash
curl "http://localhost:8080/api/work/client/1/monthly-bill?year=2024&month=6"
```

### Record a payment
```bash
curl -X POST http://localhost:8080/api/clients/1/payment \
  -H "Content-Type: application/json" \
  -d '{"amount": 5000}'
```

---

## 🗺️ DevOps Learning Roadmap

Use this project to practice each tool as you learn it:

### ✅ Stage 1 — Docker (current)
- [x] `Dockerfile` with multi-stage build
- [x] `docker-compose.yml`
- [ ] Push image to Docker Hub: `docker push yourusername/tractor-app`
- [ ] Try different base images (alpine vs jammy), compare sizes

### 🔜 Stage 2 — CI/CD
- [ ] **GitHub Actions**: Add `.github/workflows/build.yml` to auto-build+test on push
- [ ] **Jenkins**: Set up a local Jenkins, create a pipeline to build and push the Docker image
- [ ] Add test coverage with `mvn test`

### 🔜 Stage 3 — Kubernetes
- [ ] Write `k8s/deployment.yaml` and `k8s/service.yaml`
- [ ] Deploy to minikube: `kubectl apply -f k8s/`
- [ ] Add ConfigMap for application.properties
- [ ] Add Horizontal Pod Autoscaler

### 🔜 Stage 4 — Real Database
- [ ] Add PostgreSQL service to docker-compose.yml
- [ ] Add `application-prod.properties` with Postgres config
- [ ] Use environment variables for DB credentials (secrets)
- [ ] Add Flyway for database migrations

### 🔜 Stage 5 — Monitoring
- [ ] Add Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`)
- [ ] Add Prometheus + Grafana to docker-compose
- [ ] Create a dashboard showing work entries per hour, revenue metrics

### 🔜 Stage 6 — Cloud
- [ ] Deploy to AWS EC2 manually
- [ ] Move to AWS ECS (Docker on AWS)
- [ ] Try EKS (Kubernetes on AWS)
- [ ] Terraform: write IaC to provision the infra

---

## 📁 Project Structure

```
tractor-field-manager/
├── src/
│   └── main/java/com/tractorapp/
│       ├── TractorFieldManagerApplication.java  # Entry point
│       ├── DataSeeder.java                      # Demo data
│       ├── model/
│       │   ├── Client.java                      # Client entity
│       │   └── WorkEntry.java                   # Work log entity
│       ├── repository/
│       │   ├── ClientRepository.java
│       │   └── WorkEntryRepository.java         # Billing queries
│       ├── service/
│       │   ├── ClientService.java               # Balance management
│       │   └── WorkEntryService.java            # Core billing logic
│       └── controller/
│           ├── ClientController.java
│           ├── WorkEntryController.java
│           └── DashboardController.java
├── Dockerfile                                   # Multi-stage build
├── docker-compose.yml
└── pom.xml
```

---

## 💡 Business Logic

1. **Log work** → hours × rate = amount → auto-added to client outstanding balance
2. **Monthly bill** → sum all entries for client in that month
3. **Payment** → reduces outstanding balance
4. **Dashboard** → total revenue, clients with dues, monthly overview

Work types: `PLOUGHING`, `TILLING`, `HARVESTING`, `LEVELLING`, `GENERAL`
