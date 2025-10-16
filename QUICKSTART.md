# Quick Start Guide

This guide will help you get the Banking Microservices up and running quickly.

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (optional, for containerized deployment)

## Option 1: Docker Compose (Recommended)

The easiest way to run all services with Kafka:

```bash
# Clone the repository
git clone https://github.com/BharAnu2109/bank1.git
cd bank1

# Build and start all services
docker-compose up --build

# To run in background
docker-compose up -d --build

# To stop all services
docker-compose down
```

After starting, services will be available at:
- Account Service: http://localhost:8081/graphiql
- Transaction Service: http://localhost:8082/graphiql
- Customer Service: http://localhost:8083/graphiql

## Option 2: Local Development

### Step 1: Start Kafka

```bash
# Start Kafka using Docker
docker-compose up zookeeper kafka
```

Or download and run Kafka manually from https://kafka.apache.org/downloads

### Step 2: Build the Project

```bash
mvn clean install
```

### Step 3: Run Services

Open three terminal windows and run:

**Terminal 1 - Customer Service:**
```bash
cd customer-service
mvn spring-boot:run
```

**Terminal 2 - Account Service:**
```bash
cd account-service
mvn spring-boot:run
```

**Terminal 3 - Transaction Service:**
```bash
cd transaction-service
mvn spring-boot:run
```

## Testing the APIs

### Access GraphiQL Interfaces

1. **Customer Service**: http://localhost:8083/graphiql
2. **Account Service**: http://localhost:8081/graphiql
3. **Transaction Service**: http://localhost:8082/graphiql

### Example Flow

Follow this sequence to test the complete flow:

#### 1. Create a Customer

Go to http://localhost:8083/graphiql and run:

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
  }
}
```

Note the customer `id` from the response (e.g., `1`).

#### 2. Create an Account

Go to http://localhost:8081/graphiql and run:

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
    balance
    status
  }
}
```

Note the `accountNumber` from the response (e.g., `ACC123456789ABC`).

#### 3. Deposit Money

```graphql
mutation {
  updateBalance(input: {
    accountNumber: "ACC123456789ABC"
    amount: 1000.00
    operationType: "CREDIT"
  }) {
    accountNumber
    balance
  }
}
```

#### 4. Create a Transaction

Go to http://localhost:8082/graphiql and run:

```graphql
mutation {
  createTransaction(input: {
    fromAccountNumber: "ACC123456789ABC"
    toAccountNumber: "ACC987654321XYZ"
    amount: 250.00
    currency: "USD"
    transactionType: "TRANSFER"
    description: "Payment for services"
  }) {
    transactionId
    fromAccountNumber
    toAccountNumber
    amount
    status
  }
}
```

#### 5. Query All Data

**Get all customers:**
```graphql
query {
  allCustomers {
    id
    firstName
    lastName
    email
  }
}
```

**Get all accounts:**
```graphql
query {
  allAccounts {
    accountNumber
    customerId
    balance
    status
  }
}
```

**Get all transactions:**
```graphql
query {
  allTransactions {
    transactionId
    amount
    status
    transactionDate
  }
}
```

## Access H2 Database Console

Each service has an H2 console:

- Account Service: http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:accountdb`
  
- Transaction Service: http://localhost:8082/h2-console
  - JDBC URL: `jdbc:h2:mem:transactiondb`
  
- Customer Service: http://localhost:8083/h2-console
  - JDBC URL: `jdbc:h2:mem:customerdb`

Username: `sa`  
Password: (leave empty)

## Monitoring Kafka Events

You can monitor Kafka topics to see the events being published:

```bash
# Using Docker
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:29092 --topic account-created --from-beginning

docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:29092 --topic transaction-created --from-beginning

docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:29092 --topic customer-created --from-beginning
```

## Pre-made GraphQL Query Examples

Check the `graphql-examples/` folder for ready-to-use GraphQL queries:

- `customer-queries.graphql` - Customer service examples
- `account-queries.graphql` - Account service examples
- `transaction-queries.graphql` - Transaction service examples

Simply copy and paste these queries into the GraphiQL interface!

## Troubleshooting

### Port Already in Use

If ports 8081, 8082, or 8083 are already in use, you can change them in each service's `application.yml`:

```yaml
server:
  port: 8084  # Change to any available port
```

### Kafka Connection Issues

Ensure Kafka is running on `localhost:9092`. Check with:

```bash
docker ps | grep kafka
```

### Build Errors

Clean and rebuild:

```bash
mvn clean install -U
```

## Next Steps

- Explore the complete API using GraphiQL's documentation explorer
- Check Kafka topics for real-time events
- Modify the services to add custom business logic
- Add more microservices following the same pattern

For more detailed information, see the main [README.md](README.md).
