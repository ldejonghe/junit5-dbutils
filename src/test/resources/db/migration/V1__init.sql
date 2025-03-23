CREATE TABLE app_user (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    age INT
);

CREATE TABLE user_roles (
    id INT PRIMARY KEY,
    user_id INT,
    role VARCHAR(50)
);
