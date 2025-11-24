# 💰 Expense and Budget Tracker

A comprehensive full-stack application for managing personal finances, tracking expenses, and monitoring budgets with beautiful visualizations and intelligent insights.

## 🌟 Features

### Core Functionality
- **Expense Tracking**: Record and categorize daily expenses and income
- **Budget Management**: Set monthly budgets per category with alert thresholds
- **Dashboard Analytics**: Visual insights with charts and statistics
- **Advanced Filtering**: Filter transactions by date, category, and type
- **Pagination & Sorting**: Efficient data handling for large datasets
- **Real-time Alerts**: Budget notifications when spending limits are approached

### Technical Features
- **RESTful API**: Well-structured backend with comprehensive endpoints
- **Responsive Design**: Beautiful UI that works on all devices
- **Data Validation**: Robust input validation on both frontend and backend
- **Error Handling**: Graceful error management with user-friendly messages
- **Docker Support**: Easy deployment with containerization

## 🛠️ Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.2**
- **Spring Data JPA** for database operations
- **PostgreSQL** (production) / **H2** (development)
- **Maven** for dependency management
- **Lombok** for reducing boilerplate code

### Frontend
- **Vue.js 3** with Composition API
- **Vite** for fast development and building
- **Pinia** for state management
- **Vue Router** for navigation
- **Chart.js** for data visualization
- **Tailwind CSS** for styling
- **Axios** for API communication

## 🚀 Quick Start

### Using Docker (Recommended)

1. Clone the repository:
```bash
git clone <repository-url>
cd expense-tracker
```

2. Start the application using Docker Compose:
```bash
docker-compose up -d
```

3. Access the application:
- Frontend: http://localhost
- Backend API: http://localhost:8080/api

### Manual Setup

#### Backend Setup
```bash
cd expense-tracker-backend
mvn clean install
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd expense-tracker-frontend
npm install
npm run dev
```

## 📝 API Documentation

### Authentication
Currently using mock authentication. In production, implement JWT-based auth.

### Main Endpoints

#### Users
- `GET /api/users` - Get all users
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Deactivate user

#### Transactions
- `GET /api/users/{userId}/transactions` - Get transactions (with pagination/filtering)
- `POST /api/users/{userId}/transactions` - Create transaction
- `PUT /api/users/{userId}/transactions/{id}` - Update transaction
- `DELETE /api/users/{userId}/transactions/{id}` - Delete transaction
- `GET /api/users/{userId}/transactions/summary/monthly` - Monthly summary
- `GET /api/users/{userId}/transactions/summary/yearly` - Yearly summary

#### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

#### Budgets
- `GET /api/users/{userId}/budgets` - Get user budgets
- `POST /api/users/{userId}/budgets` - Create/update budget
- `GET /api/users/{userId}/budgets/monthly` - Get monthly budgets with spending
- `GET /api/users/{userId}/budgets/alerts` - Get budget alerts
- `DELETE /api/users/{userId}/budgets/{id}` - Delete budget

### Query Parameters

#### Transaction Filtering
- `categoryId` - Filter by category
- `startDate` - Start date (ISO format)
- `endDate` - End date (ISO format)
- `type` - Transaction type (INCOME/EXPENSE)
- `page` - Page number (0-based)
- `size` - Page size
- `sortBy` - Sort field
- `sortDirection` - ASC/DESC

## 🏗️ Architecture

### Backend Architecture
```
src/main/java/com/expensetracker/
├── entity/          # JPA entities
├── repository/      # Data access layer
├── service/         # Business logic
├── controller/      # REST endpoints
├── dto/            # Data transfer objects
├── config/         # Configuration classes
└── exception/      # Exception handling
```

### Frontend Architecture
```
src/
├── views/          # Page components
├── components/     # Reusable components
├── stores/         # Pinia state management
├── services/       # API services
├── router/         # Vue Router config
└── assets/         # Static assets
```

## 🔒 Security Considerations

1. **Authentication**: Implement JWT-based authentication
2. **Authorization**: Add role-based access control
3. **Data Validation**: Input validation on both client and server
4. **SQL Injection**: Using parameterized queries via JPA
5. **XSS Protection**: Vue.js automatic escaping
6. **CORS**: Configured for specific origins

## 📊 Database Schema

### Users Table
- id (PK)
- username (unique)
- email (unique)
- full_name
- created_at
- is_active

### Categories Table
- id (PK)
- name
- description
- icon
- color
- is_system

### Transactions Table
- id (PK)
- amount
- description
- transaction_date
- type (INCOME/EXPENSE)
- user_id (FK)
- category_id (FK)
- created_at
- updated_at

### Budgets Table
- id (PK)
- amount
- month
- year
- alert_threshold
- notes
- user_id (FK)
- category_id (FK)

## 🧪 Testing

### Backend Testing
```bash
cd expense-tracker-backend
mvn test
```

### Frontend Testing
```bash
cd expense-tracker-frontend
npm run test
```

## 📈 Performance Optimization

1. **Database Indexing**: Indexes on frequently queried columns
2. **Pagination**: Efficient data loading with pagination
3. **Caching**: Consider implementing Redis for caching
4. **Lazy Loading**: JPA lazy loading for relationships
5. **Frontend Optimization**: Code splitting and lazy loading routes

## 🚢 Deployment

### Production Considerations

1. **Environment Variables**: Use proper environment configuration
2. **Database**: Use managed PostgreSQL service
3. **SSL/TLS**: Enable HTTPS with SSL certificates
4. **Monitoring**: Add application monitoring (e.g., Prometheus)
5. **Logging**: Centralized logging (e.g., ELK stack)
6. **Backup**: Regular database backups

### Docker Deployment

```bash
# Build and run in production mode
docker-compose -f docker-compose.yml up -d --build

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Vue.js team for the progressive framework
- Chart.js for beautiful charts
- Tailwind CSS for utility-first CSS

## 📞 Support

For issues and questions, please create an issue in the GitHub repository.

---

**Built with ❤️ for better financial management**