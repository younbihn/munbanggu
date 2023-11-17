INSERT INTO USER(email, password,  nickname,  phone, failed_count,created_date)
values ('abc@gmail.com', 'password1234', 'nickname', '010-3333-4444', 0, '2023-10-29 23:01:10');

INSERT INTO VOTE(id, title, end_date)
values (2L, '투표 제목', '2023-11-11T00:00:00');

INSERT INTO VOTE_OPTION(option_text, vote_id)
values('옵션1', 2L),
    ('옵션2', 2L),
    ('옵션3', 2L);

INSERT INTO STUDY_BOARD_POST(type, title, content, user_id, vote_id)
values ('VOTE', '게시글 제목', '내용 작성하자', 1L, 2L);
