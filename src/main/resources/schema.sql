CREATE TABLE refresh_token (
    id BIGINT PRIMARY KEY,
    token VARCHAR(255)
);

CREATE TABLE study_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    study_id BIGINT,
    participation_rate DOUBLE,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (study_id) REFERENCES study(id)
);

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255),
    nickname VARCHAR(255),
    auth_provider VARCHAR(50),
    login_type VARCHAR(50),
    role VARCHAR(50) DEFAULT 'USER',
    phone VARCHAR(50),
    profile_image_url VARCHAR(255),
    failed_count INT DEFAULT 0,
    created_date TIMESTAMP,
    delete_date TIMESTAMP
);

CREATE TABLE study_board_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50),
    title VARCHAR(255),
    content TEXT,
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    user_id BIGINT,
    study_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (study_id) REFERENCES study(id)
);

CREATE TABLE study_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    is_deleted BOOLEAN,
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    study_board_post_id BIGINT,
    user_id BIGINT,
    parent_id BIGINT,
    FOREIGN KEY (study_board_post_id) REFERENCES study_board_post(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (parent_id) REFERENCES study_comment(id)
);

CREATE TABLE user_vote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vote_id BIGINT,
    user_id BIGINT,
    vote_option_id BIGINT,
    FOREIGN KEY (vote_id) REFERENCES vote(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (vote_option_id) REFERENCES vote_option(id)
);

CREATE TABLE vote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    study_board_post_id BIGINT,
    end_date TIMESTAMP,
    FOREIGN KEY (study_board_post_id) REFERENCES study_board_post(id)
);

CREATE TABLE vote_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_text VARCHAR(255),
    vote_id BIGINT,
    FOREIGN KEY (vote_id) REFERENCES vote(id)
);

CREATE TABLE checklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_user_id BIGINT,
    study_id BIGINT,
    user_id BIGINT,
    done BOOLEAN DEFAULT FALSE,
    todo VARCHAR(255),
    access_type VARCHAR(50),
    created_date TIMESTAMP,
    FOREIGN KEY (study_user_id) REFERENCES study_user(id),
    FOREIGN KEY (study_id) REFERENCES study(id)
);

CREATE TABLE study (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255),
    content TEXT,
    min_user BIGINT,
    max_user BIGINT,
    public_or_not BOOLEAN,
    password VARCHAR(255),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    start_rule BOOLEAN,
    start_attend_or_not BOOLEAN,
    checklist_cycle VARCHAR(50) NOT NULL,
    fee BIGINT,
    refund_cycle VARCHAR(50) NOT NULL,
    latest_refund_date TIMESTAMP,
    status VARCHAR(50),
    create_date TIMESTAMP,
    delete_date TIMESTAMP
);

CREATE TABLE study_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    study_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (study_id) REFERENCES study(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE community (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(255),
    content TEXT,
    photo_imgurl VARCHAR(255),
    community_category VARCHAR(50),
    view BIGINT DEFAULT 0,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE community_hashtags (
    community_id BIGINT,
    hashtag VARCHAR(255),
    PRIMARY KEY (community_id, hashtag),
    FOREIGN KEY (community_id) REFERENCES community(id)
);

CREATE TABLE community_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    community_id BIGINT,
    content TEXT NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    parent_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (community_id) REFERENCES community(id),
    FOREIGN KEY (parent_id) REFERENCES community_comments(id)
);