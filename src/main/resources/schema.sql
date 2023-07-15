CREATE TABLE IF NOT EXISTS users (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    first_name        VARCHAR(128) NOT NULL,
    email             VARCHAR(64)  NOT NULL,
    CONSTRAINT        PK_USER PRIMARY KEY(id),
    CONSTRAINT        UNQ_USER_EMAIL UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS items (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(128) NOT NULL,
    description       VARCHAR(512) NOT NULL,
    is_available      BOOLEAN      NOT NULL,
    user_id           BIGINT       NOT NULL,
    CONSTRAINT        PK_ITEM PRIMARY KEY(id),
    CONSTRAINT        FK_ITEM_TO_USER FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);