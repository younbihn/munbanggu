INSERT INTO USER (email, name, password, nickname, auth_provider, role, phone, profile_image_url, failed_count, created_date, delete_date) VALUES
('user1@example.com', 'User One', 'password1', 'nickname1', NULL, 'USER', '010-1234-5671', 'http://example.com/profile1.jpg', 0, NOW(), NULL),
('user2@example.com', 'User Two', 'password2', 'nickname2', NULL, 'USER', '010-1234-5672', 'http://example.com/profile2.jpg', 0, NOW(), NULL),
('user3@example.com', 'User Three', 'password3', 'nickname3', NULL, 'USER', '010-1234-5673', 'http://example.com/profile3.jpg', 0, NOW(), NULL),
('user4@example.com', 'User Four', 'password4', 'nickname4', NULL, 'USER', '010-1234-5674', 'http://example.com/profile4.jpg', 0, NOW(), NULL),
('user5@example.com', 'User Five', 'password5', 'nickname5', NULL, 'USER', '010-1234-5675', 'http://example.com/profile5.jpg', 0, NOW(), NULL),
('user6@example.com', 'User Six', 'password6', 'nickname6', NULL, 'USER', '010-1234-5676', 'http://example.com/profile6.jpg', 0, NOW(), NULL),
('user8@example.com', 'User Eight', 'password8', 'nickname8', NULL, 'USER', '010-1234-5678', 'http://example.com/profile8.jpg', 0, NOW(), NULL),
('user9@example.com', 'User Nine', 'password9', 'nickname9', NULL, 'USER', '010-1234-5679', 'http://example.com/profile9.jpg', 0, NOW(), NULL),
('user10@example.com', 'User Ten', 'password10', 'nickname10', NULL, 'USER', '010-1234-5680', 'http://example.com/profile10.jpg', 0, NOW(), NULL);

INSERT INTO community (user_id, title, content, photo_imgurl, community_category, view, created_date, modified_date)
VALUES
(1, '커뮤니티 제목 1', '커뮤니티 내용 1', 'https://example.com/photo1.jpg', '카테고리1', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '커뮤니티 제목 2', '커뮤니티 내용 2', 'https://example.com/photo2.jpg', '카테고리2', 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '커뮤니티 제목 3', '커뮤니티 내용 3', 'https://example.com/photo3.jpg', '카테고리3', 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '커뮤니티 제목 4', '커뮤니티 내용 4', 'https://example.com/photo4.jpg', '카테고리4', 250, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '커뮤니티 제목 5', '커뮤니티 내용 5', 'https://example.com/photo5.jpg', '카테고리5', 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
