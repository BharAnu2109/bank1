# Banking Microservices with GraphQL and Kafka

Complete Spring Boot Banking Microservices with GraphQL implementation, all dynamic values for mutations (no hardcoded values), and Kafka for event-driven architecture.

## 🏗️ Architecture

This project consists of three microservices:

1. **Account Service** (Port 8081) - Manages bank accounts
2. **Transaction Service** (Port 8082) - Handles financial transactions
3. **Customer Service** (Port 8083) - Manages customer information

All services communicate via **Apache Kafka** for event-driven architecture and expose **GraphQL APIs** for flexible querying and mutations.

## 🚀 Features

- ✅ **GraphQL API** - Flexible queries and mutations with fully dynamic input values
- ✅ **Kafka Integration** - Event-driven communication between microservices
- ✅ **Spring Boot 3.1.5** - Latest Spring Boot framework
- ✅ **H2 Database** - In-memory database for development
- ✅ **Docker Compose** - Easy deployment with containerization
- ✅ **GraphiQL Interface** - Interactive GraphQL playground
- ✅ **No Hardcoded Values** - All mutations accept dynamic input parameters
- ✅ **Event Sourcing** - All operations publish events to Kafka topics

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)

## 🛠️ Setup and Installation

### Local Development (Without Docker)

1. **Start Kafka and Zookeeper:**
```bash
# Download and start Kafka
# Or use Docker:
docker-compose up zookeeper kafka
```

2. **Build the project:**
```bash
mvn clean install
```

3. **Run each service:**
```bash
# Account Service
cd account-service
mvn spring-boot:run

# Transaction Service
cd transaction-service
mvn spring-boot:run

# Customer Service
cd customer-service
mvn spring-boot:run
```

### Docker Deployment

```bash
# Build and start all services
docker-compose up --build

# Stop all services
docker-compose down
```

## 🔌 API Endpoints

### Account Service (http://localhost:8081)
- GraphQL Endpoint: `http://localhost:8081/graphql`
- GraphiQL UI: `http://localhost:8081/graphiql`
- H2 Console: `http://localhost:8081/h2-console`

### Transaction Service (http://localhost:8082)
- GraphQL Endpoint: `http://localhost:8082/graphql`
- GraphiQL UI: `http://localhost:8082/graphiql`
- H2 Console: `http://localhost:8082/h2-console`

### Customer Service (http://localhost:8083)
- GraphQL Endpoint: `http://localhost:8083/graphql`
- GraphiQL UI: `http://localhost:8083/graphiql`
- H2 Console: `http://localhost:8083/h2-console`

## 📝 GraphQL Examples

### Customer Service Mutations

#### Create Customer (All Dynamic Values)
```graphql
mutation {
  createCustomer(input: {
    firstName: "John"
    lastName: "Doe"
    email: "john.doe@example.com"
    phoneNumber: "+1234567890"
    dateOfBirth: "1990-01-15"
    address: "123 Main Street"
    city: "New York"
    country: "USA"
    postalCode: "10001"
  }) {
    id
    firstName
    lastName
    email
    status
    createdAt
  }
}
```

#### Update Customer (Dynamic Values)
```graphql
mutation {
  updateCustomer(input: {
    id: 1
    phoneNumber: "+1987654321"
    address: "456 Oak Avenue"
    city: "Los Angeles"
    postalCode: "90001"
  }) {
    id
    phoneNumber
    address
    city
    updatedAt
  }
}
```

#### Query Customers
```graphql
query {
  allCustomers {
    id
    firstName
    lastName
    email
    city
    status
  }
}

query {
  customerById(id: 1) {
    id
    firstName
    lastName
    email
    phoneNumber
  }
}
```

### Account Service Mutations

#### Create Account (All Dynamic Values)
```graphql
mutation {
  createAccount(input: {
    customerId: 1
    accountType: "SAVINGS"
    initialBalance: 5000.00
    currency: "USD"
  }) {
    id
    accountNumber
    customerId
    accountType
    balance
    currency
    status
    createdAt
  }
}
```

#### Update Balance (Dynamic Operation)
```graphql
# Credit (Deposit)
mutation {
  updateBalance(input: {
    accountNumber: "ACC123456789"
    amount: 1000.00
    operationType: "CREDIT"
  }) {
    id
    accountNumber
    balance
    updatedAt
  }
}

# Debit (Withdrawal)
mutation {
  updateBalance(input: {
    accountNumber: "ACC123456789"
    amount: 500.00
    operationType: "DEBIT"
  }) {
    id
    accountNumber
    balance
    updatedAt
  }
}
```

#### Query Accounts
```graphql
query {
  allAccounts {
    id
    accountNumber
    customerId
    accountType
    balance
    currency
    status
  }
}

query {
  accountsByCustomerId(customerId: 1) {
    accountNumber
    accountType
    balance
    status
  }
}
```

### Transaction Service Mutations

#### Create Transaction (All Dynamic Values)
```graphql
mutation {
  createTransaction(input: {
    fromAccountNumber: "ACC123456789"
    toAccountNumber: "ACC987654321"
    amount: 250.00
    currency: "USD"
    transactionType: "TRANSFER"
    description: "Payment for services"
  }) {
    id
    transactionId
    fromAccountNumber
    toAccountNumber
    amount
    currency
    transactionType
    status
    description
    transactionDate
  }
}
```

#### Update Transaction Status (Dynamic)
```graphql
mutation {
  updateTransactionStatus(
    transactionId: "TXN123456789"
    status: "COMPLETED"
  ) {
    transactionId
    status
    fromAccountNumber
    toAccountNumber
    amount
  }
}
```

#### Query Transactions
```graphql
query {
  allTransactions {
    transactionId
    fromAccountNumber
    toAccountNumber
    amount
    currency
    status
    transactionDate
  }
}

query {
  transactionsByFromAccount(fromAccountNumber: "ACC123456789") {
    transactionId
    toAccountNumber
    amount
    status
    transactionDate
  }
}
```

## 📨 Kafka Topics

The microservices communicate through the following Kafka topics:

- `account-created` - Published when a new account is created
- `account-updated` - Published when account is updated
- `balance-updated` - Published when account balance changes
- `customer-created` - Published when a new customer is created
- `customer-updated` - Published when customer data is updated
- `customer-deleted` - Published when a customer is deleted
- `transaction-created` - Published when a new transaction is created
- `transaction-completed` - Published when transaction is completed
- `transaction-failed` - Published when transaction fails

## 🔍 Testing with GraphiQL

1. Open your browser and navigate to:
   - Account Service: `http://localhost:8081/graphiql`
   - Transaction Service: `http://localhost:8082/graphiql`
   - Customer Service: `http://localhost:8083/graphiql`

2. Use the interactive GraphiQL interface to:
   - Explore the GraphQL schema (Documentation Explorer)
   - Execute queries and mutations
   - View real-time responses

## 🏗️ Project Structure

```
bank1/
├── account-service/
│   ├── src/main/java/com/banking/account/
│   │   ├── controller/      # GraphQL controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # JPA repositories
│   │   ├── model/           # Entity classes
│   │   ├── dto/             # Data transfer objects
│   │   ├── kafka/           # Kafka producers/consumers
│   │   └── config/          # Configuration classes
│   └── src/main/resources/
│       ├── graphql/         # GraphQL schema
│       └── application.yml  # Configuration
├── transaction-service/     # Similar structure
├── customer-service/        # Similar structure
├── docker-compose.yml       # Docker orchestration
└── pom.xml                  # Parent POM
```

## 🎯 Key Design Principles

1. **No Hardcoded Values**: All mutations accept dynamic input through GraphQL input types
2. **Event-Driven Architecture**: Services communicate via Kafka events
3. **Microservices Pattern**: Each service is independent and scalable
4. **GraphQL First**: Flexible API with precise data fetching
5. **Domain-Driven Design**: Clear separation of concerns

## 🔒 Database Access

H2 Console is available for each service:
- URL: `jdbc:h2:mem:accountdb` (or transactiondb, customerdb)
- Username: `sa`
- Password: (leave empty)

## 🛡️ Error Handling

All services include comprehensive error handling:
- Validation errors for invalid input
- Business logic errors (e.g., insufficient balance)
- Database constraint violations
- Kafka messaging failures

## 📈 Scalability

Each microservice can be scaled independently:
```bash
docker-compose up --scale account-service=3 --scale transaction-service=2
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is open-source and available under the MIT License.

## 👥 Authors

- Banking Microservices Team

## 🙏 Acknowledgments

- Spring Boot community
- GraphQL community
- Apache Kafka team
