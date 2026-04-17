# Frontend Architecture (Angular)

## 5.1 Recommended Angular Structure

```text
src/
 ┣ app/
 ┃ ┣ core/
 ┃ ┃ ┣ guards/
 ┃ ┃ ┣ interceptors/
 ┃ ┃ ┣ services/
 ┃ ┃ ┣ models/
 ┃ ┃ ┗ core.module.ts
 ┃ ┣ shared/
 ┃ ┃ ┣ components/
 ┃ ┃ ┣ directives/
 ┃ ┃ ┣ pipes/
 ┃ ┃ ┗ shared.module.ts
 ┃ ┣ features/
 ┃ ┃ ┣ auth/
 ┃ ┃ ┣ dashboard/
 ┃ ┃ ┣ customers/
 ┃ ┃ ┣ accounts/
 ┃ ┃ ┣ transactions/
 ┃ ┃ ┣ payments/
 ┃ ┃ ┣ reports/
 ┃ ┃ ┗ admin/
 ┃ ┣ layout/
 ┃ ┃ ┣ header/
 ┃ ┃ ┣ sidebar/
 ┃ ┃ ┣ footer/
 ┃ ┃ ┗ layout.module.ts
 ┃ ┣ app-routing.module.ts
 ┃ ┣ app.component.ts
 ┃ ┗ app.module.ts
 ┣ assets/
 ┣ environments/
 ┗ styles/
```

## 5.2 Frontend Module Responsibilities

### Core Module

Contains application-wide services and logic:

- Authentication service
- Token management
- Route guards
- HTTP interceptors
- User session handling

### Shared Module

Contains reusable UI elements:

- Buttons
- Form controls
- Tables
- Loading spinners
- Modals
- Pipes and validators

### Auth Module

Handles:

- Login page
- Logout
- Forgot password
- Reset password
- MFA screens

### Dashboard Module

Displays:

- Balance summaries
- Recent transactions
- Alerts
- Quick actions
- Charts and metrics

### Customers Module

Allows:

- Customer registration
- Customer search
- Customer profile view
- KYC details management

### Accounts Module

Allows:

- Account listing
- Account creation
- Account details
- Balance view
- Account status update

### Transactions Module

Allows:

- Transaction history
- Transfer initiation
- Filters and search
- Transaction details
- Download statements

### Payments Module

Allows:

- Beneficiary management
- Scheduled payments
- Transfer confirmations
- Payment tracking

### Admin Module

Allows:

- User administration
- Role assignment
- Audit review
- Compliance review
- System settings

## 5.3 Frontend Security Features

The Angular frontend should include:

- Route guards for protected pages
- HTTP interceptor to attach JWT token
- Automatic redirect on unauthorized access
- Secure session timeout handling
- Form validation
- Masking of sensitive data
- Role-based menu rendering

## 5.4 Frontend UX Features

The user interface should provide:

- Responsive layout for desktop and tablet
- Clean dashboard
- Account summary cards
- Transaction tables with search and filters
- Clear validation messages
- Confirmation modals for payments and transfers
- Audit-friendly action history pages

## 6. Database Design – MySQL

> Placeholder for backend database design details. Suggested follow-up sections:
>
> - Entity model and relationships
> - Schema per service (auth, customer, account, transaction)
> - Indexing and query strategy
> - Audit tables and retention
> - Migration/versioning approach
