DROP TABLE IF EXISTS USERS, CATEGORIES, EVENTS, COMPILATIONS, COMPILATION_EVENT, REQUESTS, COMMENTS CASCADE;

CREATE TABLE IF NOT EXISTS USERS
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME VARCHAR(128),
    EMAIL VARCHAR(64) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (ID),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (EMAIL)

);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME VARCHAR(64),
    CONSTRAINT pk_categories PRIMARY KEY (ID),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (NAME)

);

CREATE TABLE IF NOT EXISTS EVENTS
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ANNOTATION VARCHAR(512),
    CATEGORY_ID BIGINT REFERENCES CATEGORIES (ID) ON DELETE CASCADE,
    CONFIRMED_REQUESTS INTEGER,
    CREATED_ON TIMESTAMP WITHOUT TIME ZONE,
    DESCRIPTION VARCHAR(1024) NOT NULL,
    EVENT_DATE TIMESTAMP WITHOUT TIME ZONE,
    INITIATOR_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE,
    LAT float,
    LON float,
    PAID BOOLEAN,
    PARTICIPANT_LIMIT INTEGER,
    PUBLISHED_ON TIMESTAMP WITHOUT TIME ZONE,
    REQUEST_MODERATION BOOLEAN,
    STATE VARCHAR(16),
    TITLE VARCHAR(128),
    VIEWS BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (ID)
);



CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    PINNED BOOLEAN,
    TITLE VARCHAR(128),
    CONSTRAINT pk_compilation PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS COMPILATION_EVENT
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    COMPILATION_ID BIGINT REFERENCES COMPILATIONS (ID) ON DELETE CASCADE,
    EVENT_ID BIGINT REFERENCES EVENTS (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    CREATED TIMESTAMP WITHOUT TIME ZONE,
    EVENT_ID BIGINT REFERENCES EVENTS (ID) ON DELETE CASCADE,
    REQUESTER_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE,
    STATUS VARCHAR(16) NOT NULL,
    PRIMARY KEY (ID)
);