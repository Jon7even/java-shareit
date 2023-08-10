CREATE TABLE IF NOT EXISTS users (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    first_name        VARCHAR(128) NOT NULL,
    email             VARCHAR(64)  NOT NULL,
    CONSTRAINT        PK_USER PRIMARY KEY(id),
    CONSTRAINT        UNQ_USER_EMAIL UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS requests (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    description       VARCHAR(512) NOT NULL,
    requestor_id      BIGINT       NOT NULL,
    created           TIMESTAMP    WITHOUT TIME ZONE     NOT NULL,
    CONSTRAINT        PK_REQUEST PRIMARY KEY(id),
    CONSTRAINT        FK_REQUEST_TO_USER FOREIGN KEY(requestor_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(128) NOT NULL,
    description       VARCHAR(512) NOT NULL,
    is_available      BOOLEAN      NOT NULL,
    user_id           BIGINT       NOT NULL,
    request_id        BIGINT,
    CONSTRAINT        PK_ITEM PRIMARY KEY(id),
    CONSTRAINT        FK_ITEM_TO_USER FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT        FK_ITEM_TO_REQUEST FOREIGN KEY(request_id) REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_date        TIMESTAMP    WITHOUT TIME ZONE     NOT NULL,
    end_date          TIMESTAMP    WITHOUT TIME ZONE     NOT NULL,
    item_id           BIGINT       NOT NULL,
    booker_id         BIGINT       NOT NULL,
    status            VARCHAR(24)  NOT NULL,
    CONSTRAINT        PK_BOOKING PRIMARY KEY(id),
    CONSTRAINT        FK_BOOKING_TO_ITEM FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT        FK_BOOKING_TO_USER FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id                BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    text              VARCHAR(1024) NOT NULL,
    item_id           BIGINT        NOT NULL,
    author_id         BIGINT        NOT NULL,
    created           TIMESTAMP     WITHOUT TIME ZONE     NOT NULL,
    CONSTRAINT        PK_COMMENTS PRIMARY KEY(id),
    CONSTRAINT        FK_COMMENTS_TO_ITEM FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT        FK_COMMENTS_TO_USER FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE CASCADE
);
