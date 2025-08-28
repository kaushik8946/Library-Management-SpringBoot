create database if not exists librarySpring;
use librarySpring;
CREATE TABLE books (
    bookId INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) UNIQUE NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,          
    status VARCHAR(100) NOT NULL,             
    availability VARCHAR(100) NOT NULL,     
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NULL
);

CREATE TABLE books_log (
    logId INT AUTO_INCREMENT PRIMARY KEY,
    bookId INT NOT NULL,
    title VARCHAR(100),
    author VARCHAR(100),
    category VARCHAR(100),
    status VARCHAR(100),
    availability VARCHAR(100),
    original_created_at TIMESTAMP,
    original_created_by VARCHAR(100),
    original_updated_at TIMESTAMP NULL,
    original_updated_by VARCHAR(100) NULL,
    logDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE members (
    memberID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phoneNumber BIGINT UNIQUE NOT NULL,
    gender CHAR(1) NOT NULL,
    address VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NULL
);

CREATE TABLE members_log (
    logId INT AUTO_INCREMENT PRIMARY KEY,
    memberId INT NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    phoneNumber BIGINT,
    gender CHAR(1),
    address VARCHAR(500),
    logDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE issue_records (
    issueId INT AUTO_INCREMENT PRIMARY KEY,
    bookId INT NOT NULL,
    memberId INT NOT NULL,
    status VARCHAR(50) NOT NULL,         
    issueDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    issued_by VARCHAR(100) NOT NULL,
    returnDate TIMESTAMP NULL,
    returned_by VARCHAR(100) NULL,
    FOREIGN KEY (bookId) REFERENCES books(bookId) ON DELETE CASCADE,
    FOREIGN KEY (memberId) REFERENCES members(memberID) ON DELETE CASCADE,
    unique (bookId,memberId)
);


CREATE TABLE issue_records_log (
    logId INT AUTO_INCREMENT PRIMARY KEY,
    issueId INT NOT NULL,
    bookId INT NOT NULL,
    memberId INT NOT NULL,
    status VARCHAR(100),
    issueDate TIMESTAMP,
    issued_by VARCHAR(100),
    returnDate TIMESTAMP NULL,
    returned_by VARCHAR(100) NULL,
    logDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issueId) REFERENCES issue_records(issueId) ON DELETE CASCADE,
    FOREIGN KEY (bookId) REFERENCES books(bookId) ON DELETE CASCADE,
    FOREIGN KEY (memberId) REFERENCES members(memberID) ON DELETE CASCADE
);

INSERT INTO books (title, author, category, status, availability, created_by)
VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction', 'A', 'A', 'ADMIN'),
('A Brief History of Time', 'Stephen Hawking', 'Science', 'A', 'A', 'ADMIN'),
('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'History', 'A', 'A', 'ADMIN'),
('The Diary of a Young Girl', 'Anne Frank', 'Biography', 'A', 'A', 'ADMIN'),
('Clean Code', 'Robert C. Martin', 'Technology', 'A', 'A', 'ADMIN'),
('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', 'Fantasy', 'A', 'A', 'ADMIN'),
('The Da Vinci Code', 'Dan Brown', 'Mystery', 'A', 'A', 'ADMIN'),
('The Girl with the Dragon Tattoo', 'Stieg Larsson', 'Thriller', 'A', 'A', 'ADMIN'),
('Pride and Prejudice', 'Jane Austen', 'Romance', 'A', 'A', 'ADMIN'),
('The Alchemist', 'Paulo Coelho', 'Other', 'A', 'A', 'ADMIN');


INSERT INTO members (name, email, phoneNumber, gender, address, created_by)
VALUES
('Amit Sharma', 'amit.sharma@example.com', 9876543210, 'M', 'Delhi, India', 'ADMIN'),
('Priya Verma', 'priya.verma@example.com', 9123456780, 'F', 'Mumbai, Maharashtra', 'ADMIN'),
('Rahul Mehta', 'rahul.mehta@example.com', 9988776655, 'M', 'Ahmedabad, Gujarat', 'ADMIN'),
('Sneha Nair', 'sneha.nair@example.com', 9765432109, 'F', 'Kochi, Kerala', 'ADMIN'),
('Arjun Reddy', 'arjun.reddy@example.com', 9845123456, 'M', 'Hyderabad, Telangana', 'ADMIN'),
('Neha Gupta', 'neha.gupta@example.com', 9911223344, 'F', 'Lucknow, Uttar Pradesh', 'ADMIN'),
('Karan Singh', 'karan.singh@example.com', 9822334455, 'M', 'Jaipur, Rajasthan', 'ADMIN'),
('Ananya Iyer', 'ananya.iyer@example.com', 9753124680, 'F', 'Chennai, Tamil Nadu', 'ADMIN'),
('Rohan Das', 'rohan.das@example.com', 9876012345, 'M', 'Kolkata, West Bengal', 'ADMIN'),
('Meera Patil', 'meera.patil@example.com', 9867543210, 'F', 'Pune, Maharashtra', 'ADMIN');
