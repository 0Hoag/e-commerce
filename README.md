# E-commerce Platform 
A modern web-based platform designed to connect buyers and sellers, enabling seamless online transactions and content sharing.

## About  
E-commerce Platform is a full-stack application built to facilitate online commerce. The platform allows users to:
Browse and purchase products effortlessly.
Authenticate securely using Keycloak.
Manage their profiles with user-friendly interfaces.
Developed using a microservices architecture, the project ensures scalability, maintainability, and adaptability for future enhancements.

## Features  
- User authentication with platform (Keycloak)
- Create, edit, and delete posts with multimedia uploads (images/videos)  
- Privacy settings and content moderation to manage posts visibility  
- Payment gateway integration with VNPay for transactions  
- Cloud storage for media files via Cloudinary  


## Technologies  
- **Backend**: Spring boot 
- **Frontend**: React
- **Database**: MySQL, MongoDB  
- **Authentication**: OAuth 2.0 (Keycloak as Authorization Server), JWT for token-based authentication
- **Payment Integration**: VNPay
- **Cloud Storage**: Cloudinary

## Installation  

### Prerequisites  
- Java Development Kit (JDK) 
- MongoDB installed locally or accessible remotely
- Docker

### Steps  
1. Clone the repository:  
git clone https://github.com/0Hoag/e-commerce/tree/main <br>
cd social-platform <br>
3. Set up the backend: <br>
cd backend

Install JDK:
Download and install the Java Development Kit (JDK) from the official Oracle website.
Set the JAVA_HOME environment variable and verify the installation:

export JAVA_HOME=/path/to/your/jdk
export PATH=$JAVA_HOME/bin:$PATH
java -version
