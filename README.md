# 🛒 E-commerce Platform 
A modern web-based platform designed to connect buyers and sellers, enabling seamless online transactions and content sharing.

## 🚀 About  
E-commerce Platform is a full-stack application built to facilitate online commerce. The platform allows users to:
Browse and purchase products effortlessly.
Authenticate securely using Keycloak.
Manage their profiles with user-friendly interfaces.
Developed using a microservices architecture, the project ensures scalability, maintainability, and adaptability for future enhancements.

## ✨ Features  
- User authentication with platform (Keycloak)
- Create, edit, and delete posts with multimedia uploads (images/videos)  
- Privacy settings and content moderation to manage posts visibility  
- Payment gateway integration with VNPay for transactions  
- Cloud storage for media files via Cloudinary  

## 🛠️ Technologies  
- **Backend**: Spring boot 
- **Frontend**: React
- **Database**: MySQL, MongoDB  
- **Authentication**: OAuth 2.0 (Keycloak as Authorization Server), JWT for token-based authentication
- **Payment Integration**: VNPay
- **Cloud Storage**: Cloudinary

## 📦 Installation
- **Clone the repository**:
git clone https://github.com/0Hoag/BookStore.git
cd hotbook
- **Backend Setup**:
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```
- **Frontend Setup**:
```bash
cd frontend
npm install
npm start
```
