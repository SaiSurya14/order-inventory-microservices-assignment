# Order & Inventory Microservices Assignment

## 🏗️ Architecture

The system consists of two independent microservices communicating via REST APIs:

1.  **Order Service (Port 8080):**
    * Accepts orders from users.
    * Communicates with Inventory Service to check stock and deduct quantities.
    * Persists order status (`CONFIRMED` or `FAILED`).
2.  **Inventory Service (Port 8081):**
    * Manages product batches and expiry dates.
    * Uses the **Factory Design Pattern** to select sorting strategies (e.g., `ExpiryDateStrategy` for FOOD, can implement different strategies for different ProductType) .
    * Manages stock deduction logic.

**Tech Stack:**
* **Java 17**
* **Spring Boot 3.x** (Web, Data JPA)
* **H2 Database**
* **Lombok** (Boilerplate reduction)
* **MapStruct** (Entity-DTO Mapping)
* **JUnit 5 & Mockito** (Testing)
* **Integration Test** (@SpringBootTest)

---

## 🚀 Getting Started

### Prerequisites
* Java 17 SDK
* Maven 3.8+

### Installation & Running


1. **Build the Project**
    Run the following command from the root directory to build both modules:
    ```bash
    mvn clean install
    ```

2. **Run Inventory Service**
    * Open a terminal in `/inventory-service`
    * Run: `mvn spring-boot:run`
    * Server starts on: `http://localhost:8081`

3. **Run Order Service**
    * Open a new terminal in `/order-service`
    * Run: `mvn spring-boot:run`
    * Server starts on: `http://localhost:8080`

---

## 📚 API Documentation

### 📦 Inventory Service (Port 8081)

#### 1. Load Initial Inventory
Populate the database with products and batches.
* **Endpoint:** `PUT /inventory/loadInventory`
* **Content-Type:** `application/json`

**Request Body:**
```json
[
  {
    "productId": "APL-001",
    "productName": "Apples",
    "productType": "FOOD",
    "batches": [
      {
        "quantity": 30,
        "expiryDate": "2025-12-05" 
      },
      {
        "quantity": 30,
        "expiryDate": "2025-12-25" 
      }
    ]
  },
  {
    "productId": "NDL-010",
    "productName": "Noodles",
    "productType": "FOOD",
    "batches": [
      {
        "quantity": 80,
        "expiryDate": "2026-12-01" 
      }
    ]
}
]
   ```
#### 2. Update Inventory (Internal)
Called by Order Service to deduct stock.
* **Endpoint:** `POST /inventory/update`
  **Request Body:**
```json
{
  "productId": "APL-001",
  "productType": "FOOD",
  "quantity": 10
}
   ```

#### 3. Get Inventory (Sorted)
Fetches batches for a product, sorted by the strategy defined for its type (e.g., Expiry Date for FOOD)
* **Endpoint:** `GET /inventory/{productId}`

**Response Body:**
```json
[
{
    "id": 1,
    "productId": "APL-001",
    "quantity": 120,
    "expiryDate": "2025-12-05"
},
{
    "id": 2,
    "productId": "APL-001",
    "quantity": 250,
    "expiryDate": "2025-12-25"
}
]
   ```


### 🛒 Order Service (Port 8080)
#### 1. Load Initial Inventory
Populate the database with products and batches.
* **Endpoint:** `POST /order/placeOrder`
* **Content-Type:** `application/json`

**Request Body:**
```json
{
  "productId": "APL-001",
  "productType": "FOOD",
  "quantity": 5
}
   ```
**Success Response (201 Created):**
```json
{
  "orderId": "2b3939c0-a443-466c-beed-0166f6ae76bd",
  "status": "CONFIRMED",
  "message": "Order placed successfully"
}
   ```

### 🗄️ Database Access (H2 Console)

You can view the in-memory data for each service while they are running.

* **Inventory DB::** `http://localhost:8081/h2-console`
    * JDBC URL: `jdbc:h2:mem:inventorydb`
    * User: `sa`

* **Order DB::** `http://localhost:8080/h2-console`
    * JDBC URL: `jdbc:h2:mem:orderdb`
    * User: `sa`

### 🧪 Testing
The project includes Unit Tests (Mockito) and Integration Tests (Live H2 DB).



