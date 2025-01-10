INSERT INTO member (member_no, member_id, member_pass, member_name, member_email, member_gender, member_phone_number, member_birthday, is_social, created_at, updated_at)
VALUES (member_seq.NEXTVAL, 'member1001', 'password1', '이순신', 'member1001@example.com', 0, '010-1234-5678', TO_DATE('2010-01-01', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO member (member_no, member_id, member_pass, member_name, member_email, member_gender, member_phone_number, member_birthday, is_social, created_at, updated_at)
VALUES (member_seq.NEXTVAL, 'member1002', 'password2', '세종대왕', 'member1002@example.com', 0, '010-5678-1234', TO_DATE('1988-02-15', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO member (member_no, member_id, member_pass, member_name, member_email, member_gender, member_phone_number, member_birthday, is_social, created_at, updated_at)
VALUES (member_seq.NEXTVAL, 'member1003', 'password3', '김유신', 'member1003@example.com', 0, '010-3456-7890', TO_DATE('1992-05-10', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO member (member_no, member_id, member_pass, member_name, member_email, member_gender, member_phone_number, member_birthday, is_social, created_at, updated_at)
VALUES (member_seq.NEXTVAL, 'member1004', 'password4', '강감찬', 'member1004@example.com', 1, '010-9876-5432', TO_DATE('1985-07-20', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO member (member_no, member_id, member_pass, member_name, member_email, member_gender, member_phone_number, member_birthday, is_social, created_at, updated_at)
VALUES (member_seq.NEXTVAL, 'member1005', 'password5', '신사임당', 'member1005@example.com', 1, '010-1111-2222', TO_DATE('1994-09-05', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO member (member_no, member_id, member_pass, member_name, member_email, member_gender, member_phone_number, member_birthday, is_social, created_at, updated_at)
VALUES (member_seq.NEXTVAL, 'member1006', 'password6', '이순신', 'member1006@example.com', 1, '010-1111-2232', TO_DATE('1994-09-03', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP);

SELECT * FROM member WHERE member_name = '이순신' AND member_birthday = TO_DATE('2010-01-01', 'YYYY-MM-DD');

