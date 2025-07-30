CREATE DATABASE IF NOT EXISTS bookstore;
USE bookstore;

-- Tabelas
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    user_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100)        NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL,
    registered_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    deactivated_at DATETIME DEFAULT NULL
);

CREATE TABLE books
(
    book_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200)       NOT NULL,
    author           VARCHAR(100)       NOT NULL,
    isbn             VARCHAR(20) UNIQUE NOT NULL,
    published_year   INT,
    copies_available INT      DEFAULT 0,
    deactivated_at   DATETIME DEFAULT NULL
);

CREATE TABLE loans
(
    loan_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    book_id     BIGINT NOT NULL,
    loan_date   DATE   NOT NULL,
    return_date DATE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books (book_id) ON DELETE CASCADE
);

-- 20 usuários
INSERT INTO users (name, email)
VALUES ('Alice Johnson', 'alice@example.com'),
       ('Bob Smith', 'bob@example.com'),
       ('Carol White', 'carol@example.com'),
       ('David Brown', 'david@example.com'),
       ('Emma Wilson', 'emma@example.com'),
       ('Frank Taylor', 'frank@example.com'),
       ('Grace Davis', 'grace@example.com'),
       ('Henry Moore', 'henry@example.com'),
       ('Isabella Clark', 'isabella@example.com'),
       ('Jack Lewis', 'jack@example.com'),
       ('Karen Walker', 'karen@example.com'),
       ('Liam Hall', 'liam@example.com'),
       ('Mia Allen', 'mia@example.com'),
       ('Noah Young', 'noah@example.com'),
       ('Olivia Hernandez', 'olivia@example.com'),
       ('Peter King', 'peter@example.com'),
       ('Quinn Wright', 'quinn@example.com'),
       ('Ruby Scott', 'ruby@example.com'),
       ('Samuel Adams', 'samuel@example.com'),
       ('Tina Baker', 'tina@example.com');

-- 20 livros (com o livro 1 com 15 cópias para permitir >10 empréstimos simultâneos)
INSERT INTO books (title, author, isbn, published_year, copies_available)
VALUES ('The Book of Many Loans', 'Popular Author', '0000000000001', 2020, 15),
       ('1984', 'George Orwell', '9780451524935', 1949, 5),
       ('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 1960, 2),
       ('Pride and Prejudice', 'Jane Austen', '9781503290563', 1813, 4),
       ('Moby Dick', 'Herman Melville', '9781503280786', 1851, 1),
       ('The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 1951, 6),
       ('Brave New World', 'Aldous Huxley', '9780060850524', 1932, 5),
       ('Jane Eyre', 'Charlotte Brontë', '9780142437209', 1847, 3),
       ('Crime and Punishment', 'Fyodor Dostoevsky', '9780140449136', 1866, 4),
       ('The Hobbit', 'J.R.R. Tolkien', '9780547928227', 1937, 7),
       ('The Odyssey', 'Homer', '9780140268867', -800, 2),
       ('Fahrenheit 451', 'Ray Bradbury', '9781451673319', 1953, 5),
       ('The Iliad', 'Homer', '9780140275360', -750, 3),
       ('Animal Farm', 'George Orwell', '9780451526342', 1945, 6),
       ('Lord of the Flies', 'William Golding', '9780399501487', 1954, 2),
       ('Wuthering Heights', 'Emily Brontë', '9780141439556', 1847, 3),
       ('Les Misérables', 'Victor Hugo', '9780451419439', 1862, 2),
       ('The Divine Comedy', 'Dante Alighieri', '9780142437223', 1320, 1),
       ('Dracula', 'Bram Stoker', '9780486411095', 1897, 4),
       ('Frankenstein', 'Mary Shelley', '9780486282114', 1818, 5);

-- Empréstimos válidos (respeitando o limite de cópias disponíveis)
-- Livro 1 (book_id = 1) com 12 empréstimos NÃO devolvidos (usuários 1 a 12)
INSERT INTO loans (user_id, book_id, loan_date, return_date)
VALUES (1, 1, '2025-07-01', NULL),
       (2, 1, '2025-07-02', NULL),
       (3, 1, '2025-07-03', NULL),
       (4, 1, '2025-07-04', NULL),
       (5, 1, '2025-07-05', NULL),
       (6, 1, '2025-07-06', NULL),
       (7, 1, '2025-07-07', NULL),
       (8, 1, '2025-07-08', NULL),
       (9, 1, '2025-07-09', NULL),
       (10, 1, '2025-07-10', NULL),
       (11, 1, '2025-07-11', NULL),
       (12, 1, '2025-07-12', NULL),

-- Outros livros com empréstimos sem ultrapassar suas cópias
       (13, 2, '2025-07-05', NULL),
       (14, 2, '2025-07-06', '2025-07-10'),
       (15, 3, '2025-07-07', NULL),
       (16, 3, '2025-07-08', '2025-07-15'),
       (17, 4, '2025-07-09', NULL),
       (18, 5, '2025-07-10', NULL),
       (19, 6, '2025-07-11', '2025-07-18'),
       (20, 7, '2025-07-12', NULL);
