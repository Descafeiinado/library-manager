
### 🛠️ Dependencies

* **Java 24**
* **Maven 3.9+**
* **Docker & Docker Compose**
* **MariaDB (via Docker)**

---

### 🔧 Build

```bash
mvn install
```

---

### 🚀 Run Application (make sure you have the MariaDB container running)

```bash
mvn exec:java -pl app
```

---

### 🗄️ Start MariaDB (Docker)

```bash
cd docker
docker-compose up -d
```
