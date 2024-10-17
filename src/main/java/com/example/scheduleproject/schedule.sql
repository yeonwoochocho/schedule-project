CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      role ENUM('USER', 'ADMIN') NOT NULL,
                      created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);
CREATE TABLE schedule
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    content TEXT,
    created_date DATETIME NOT NULL,
    modified_date DATETIME NOT NULL,
    user_id     BIGINT       NOT NULL,
    author VARCHAR(50) UNIQUE NOT NULL,
);

CREATE TABLE comment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         content TEXT NOT NULL,
                         user_id BIGINT NOT NULL,
                         schedule_id BIGINT NOT NULL,
                         created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
                         FOREIGN KEY (schedule_id) REFERENCES schedule (id) ON DELETE CASCADE
);
