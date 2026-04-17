# Database Seeding Guide - Financial Platform

This guide explains the initial database seeding setup for all microservices in the financial platform.

## Overview

Each microservice has its own database with initial seed data created through Flyway migrations. The data is seeded in dependency order to ensure referential integrity.

## Microservice Seeding Order & Dependencies

### 1. **Auth Service** (No Dependencies)
- **Migration**: `V4__initial_seed_data.sql`
- **Seeds**:
  - 4 Default Roles: ADMIN, USER, MANAGER, TELLER
  - 5 Default Users: admin, user1, user2, manager1, teller1
  - User-Role assignments
- **Default Users** (Password: password123):
  - admin@financial-platform.com (ADMIN role)
  - user1@financial-platform.com (USER role)
  - user2@financial-platform.com (USER role)
  - manager1@financial-platform.com (MANAGER role)
  - teller1@financial-platform.com (TELLER role)

### 2. **Customer Service** (No Dependencies)
- **Migration**: `V3__initial_seed_data.sql`
- **Seeds**: 10 test customers (CUST-0001 to CUST-0010)
- **Status**: All ACTIVE with VERIFIED KYC

### 3. **Account Service** (Depends on Customer Service)
- **Migrations**: 
  - `V1__init_schema.sql` - Creates schema + seeds account types
  - `V3__initial_seed_data.sql` - Seeds accounts for all customers
- **Account Types Included**:
  - CHECKING: $0 minimum balance, $10,000 daily transfer limit
  - SAVINGS: $100 minimum balance, $5,000 daily transfer limit
- **Seeds**: 2 accounts per customer (20 total accounts)
  - Checking Account: ~$2,500-$8,500 USD
  - Savings Account: ~$8,500-$25,000 USD

### 4. **Payment Service** (Depends on Customer Service)
- **Migration**: `V5__initial_seed_data.sql`
- **Seeds**: 2 beneficiaries per customer (20 total beneficiaries)
- **Beneficiary Types**: Internal (BANK-USA) and International (BANK-INTL) transfers

### 5. **Transfer Service** (No Direct Data Dependencies)
- **Migration**: `V4__initial_seed_data.sql`
- **Seeds**: Transaction types for transfer operations
  - TRANSFER, DEPOSIT, WITHDRAWAL, CHARGE, INTEREST

### 6. **Transaction Service** (No Direct Data Dependencies)
- **Migration**: `V4__initial_seed_data.sql`
- **Seeds**: Transaction types including PAYMENT and REFUND

### 7. **Notification Service** (No Dependencies)
- **Migration**: `V2__initial_seed_data.sql`
- **Seeds**: 8 notification templates
  - Transfer notifications (initiated, completed, failed)
  - Payment notifications
  - Account alerts and balance warnings
  - Beneficiary notifications

### 8. **Report Service** (No Direct Dependencies)
- **Migration**: `V5__initial_seed_data.sql`
- **Seeds**:
  - Transaction types (TRANSFER, DEPOSIT, WITHDRAWAL, CHARGE, INTEREST, PAYMENT, REFUND)
  - 6 Report types (Account Summary, Transaction Detail, Customer Activity, Payment, Transfer, Compliance)

### 9. **Dashboard Service** (Depends on Account & Customer)
- No direct seed data required; pulls data from Account and Customer services

## Database Connection Details

Each microservice connects to its own database. Update the following in your deployment environment:

### Auth Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_auth
spring.datasource.username=root
spring.datasource.password=your_password
```

### Customer Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_customer
spring.datasource.username=root
spring.datasource.password=your_password
```

### Account Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_account
spring.datasource.username=root
spring.datasource.password=your_password
```

### Payment Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_payment
spring.datasource.username=root
spring.datasource.password=your_password
```

### Transfer Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_transfer
spring.datasource.username=root
spring.datasource.password=your_password
```

### Transaction Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_transaction
spring.datasource.username=root
spring.datasource.password=your_password
```

### Notification Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_notification
spring.datasource.username=root
spring.datasource.password=your_password
```

### Report Service
```
spring.datasource.url=jdbc:mysql://localhost:3306/finance_report
spring.datasource.username=root
spring.datasource.password=your_password
```

## Deployment Steps

1. **Create Databases**:
   ```sql
   CREATE DATABASE finance_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_customer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_account CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_payment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_transfer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_transaction CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_notification CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE DATABASE finance_report CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Start Services in Order**:
   - Start Auth Service (handles authentication)
   - Start Customer Service (required by Account and Payment)
   - Start Account Service (required by Payment and Transfers)
   - Start Payment Service
   - Start Transfer Service
   - Start Transaction Service
   - Start Notification Service
   - Start Report Service
   - Start Dashboard Service

3. **Verify Seed Data**:
   - Auth Service: `SELECT COUNT(*) FROM users;` (should return 5)
   - Customer Service: `SELECT COUNT(*) FROM customers;` (should return 10)
   - Account Service: `SELECT COUNT(*) FROM accounts;` (should return 20)
   - Payment Service: `SELECT COUNT(*) FROM beneficiaries;` (should return 20)

## Test Data Reference

### Default Admin User
- **Email**: admin@financial-platform.com
- **Password**: password123
- **Role**: ADMIN

### Test Customer
- **ID**: 1
- **Name**: John Doe
- **Email**: john.doe@example.com
- **Customer Code**: CUST-0001
- **Checking Account**: CHK-0001001 ($5,000.00)
- **Savings Account**: SAV-0001002 ($10,000.00)
- **Beneficiaries**: Alice Johnson, Bob Smith

## Security Notes

⚠️ **IMPORTANT**: The default passwords and seed data are **FOR DEVELOPMENT ONLY**.

For production:
1. Change all default passwords immediately
2. Rotate credentials for service accounts
3. Enable encryption at rest
4. Configure database access controls
5. Implement audit logging
6. Run security compliance checks

## Flyway Configuration

All services are configured with Flyway for automatic database migrations. Migrations run automatically on service startup.

**Configuration**:
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baselineOnMigrate: true
    outOfOrder: false
```

## Troubleshooting

### Migration Conflicts
If migrations fail due to duplicate data:
```sql
DELETE FROM users WHERE username IN ('admin', 'user1', 'user2');
DELETE FROM customers WHERE customer_code LIKE 'CUST-%';
```

Then restart the service to re-apply migrations.

### Connection Issues
Verify database connectivity:
```bash
mysql -h localhost -u root -p -e "SHOW DATABASES;"
```

### Missing Tables
If tables are missing, check Flyway migrations:
```sql
SELECT * FROM flyway_schema_history;
```

## Next Steps

After initial deployment:
1. Test user authentication with admin account
2. Create additional customer accounts as needed
3. Test payment and transfer flows
4. Configure notification delivery (email/SMS)
5. Set up report scheduling
6. Configure monitoring and alerting
