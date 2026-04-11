CREATE TABLE users (
    id UUID PRIMARY KEY,

    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,

    user_status VARCHAR(30) NOT NULL,
    user_role VARCHAR(30) NOT NULL,

    phone_number VARCHAR(20),
    cpf VARCHAR(20),

    image_url TEXT,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_users_user_name UNIQUE (user_name),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_cpf UNIQUE (cpf)
);