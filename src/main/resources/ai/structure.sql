-- auto-generated definition
create table MEMBER --회원
(
    IS_SOCIAL           NUMBER(1)          not null
        check (is_social in (0, 1)),
    MEMBER_BIRTHDAY     DATE,
    MEMBER_GENDER       NUMBER(1)          not null
        check (member_gender in (0, 1)),
    CREATED_AT          TIMESTAMP(6)       not null,
    MEMBER_NO           NUMBER(19)         not null
        primary key,
    UPDATED_AT          TIMESTAMP(6)       not null,
    MEMBER_EMAIL        VARCHAR2(255 char) not null
        unique,
    MEMBER_ID           VARCHAR2(255 char) not null
        unique,
    MEMBER_NAME         VARCHAR2(255 char), --이름
    MEMBER_PASS         VARCHAR2(255 char),
    MEMBER_PHONE_NUMBER VARCHAR2(255 char)
);


create table EMPLOYEE --직원
(
    EMPLOYEE_BIRTHDAY     DATE,
    IS_SOCIAL             NUMBER(1)          not null
        check (is_social in (0, 1)),
    CREATED_AT            TIMESTAMP(6)       not null,
    DEPARTMENT_ID         NUMBER(19)         not null
        constraint FKBEJTWVG9BXUS2MFFSM3SWJ3U9
        references DEPARTMENT,
    EMPLOYEE_NO           NUMBER(19)         not null
        primary key,
    UPDATED_AT            TIMESTAMP(6)       not null,
    EMPLOYEE_EMAIL        VARCHAR2(255 char),
    EMPLOYEE_ID           VARCHAR2(255 char) not null
        unique,
    EMPLOYEE_NAME         VARCHAR2(255 char),
    EMPLOYEE_PASS         VARCHAR2(255 char),
    EMPLOYEE_PHONE_NUMBER VARCHAR2(255 char),
    EMPLOYEE_STATUS       VARCHAR2(255 char)
        check (employee_status in ('PENDING', 'APPROVED', 'DENIED')),
    TYPE_OF_EMPLOYEE      VARCHAR2(255 char)
        check (type_of_employee in ('STAFF', 'HUMAN_RESOURCE', 'NURSE', 'DOCTOR')),
    WORKING_STATUS        VARCHAR2(255 char)
        check (working_status in ('WORKING', 'VACATION', 'SURGERY'))
);

-- auto-generated definition
create table NOTICE -- 공지사항
(
    CREATED_AT  TIMESTAMP(6)        not null, -- 작성일
    EMPLOYEE_NO NUMBER(19)
        constraint FKPJLOHKJQEFIS0MMSULK0WNFJ0
            references EMPLOYEE,
    NOTICE_NO   NUMBER(19)          not null
        primary key,
    UPDATED_AT  TIMESTAMP(6)        not null,
    VIEW_COUNT  NUMBER(19),
    CONTENT     VARCHAR2(1000 char) not null, -- 내용
    IMAGE       VARCHAR2(255 char),
    TITLE       VARCHAR2(255 char)  not null
);


-- auto-generated definition
create table PATIENT_RECORD_CARD --환자 기록카드
(
    CLINIC_DATE             DATE,
    CLINIC_RESERVATION_DATE TIMESTAMP(6),
    CREATED_AT              TIMESTAMP(6) not null,
    DISEASE_CATEGORY_ID     NUMBER(19)   not null
        constraint FK15MK9YOQ4AYEV3KRCFG12JKP0
            references DISEASE_CATEGORY,
    DISEASE_LIST_ID         NUMBER(19)   not null
        constraint FK3OWXL2AFCTBVXOG4BVFIQK62P
            references DISEASE_LIST,
    EMPLOYEE_NO             NUMBER(19)   not null
        constraint FK2V2XFGE7JHPBGCOD26OKQ3KXY
            references EMPLOYEE,
    MEMBER_ID               NUMBER(19)   not null
        constraint FKPXYY0QQYMB2RMSB7X4Y005ED4
            references MEMBER,
    PATIENT_RECORD_NO       NUMBER(19)   not null
        primary key,
    RECEPTION_NO            NUMBER(19)   not null
        constraint FK3YEW5L1HSX32XF8HSPRWOLP7T
            references RECEPTION,
    UPDATED_AT              TIMESTAMP(6) not null,
    CLINIC_MANIFESTATION    VARCHAR2(255 char),
    CLINIC_NAME             VARCHAR2(255 char),
    CLINIC_STATUS           VARCHAR2(255 char)
        check (clinic_status in ('SCHEDULED', 'COMPLETED', 'CANCELLED'))
);

-- auto-generated definition
create table RESERVATION -- 예약
(
    RESERVATION_DATE       DATE,
    TIME_SLOT              NUMBER(3)
        check (time_slot between 0 and 1),
    CREATED_AT             TIMESTAMP(6) not null,
    EMPLOYEE_NO            NUMBER(19),
    MEMBER_NO              NUMBER(19),
    PATIENT_RECORD_CARD_NO NUMBER(19),
    RESERVATION_NO         NUMBER(19)   not null
        primary key,
    UPDATED_AT             TIMESTAMP(6) not null
);

-- auto-generated definition
create table DEPARTMENT -- 부서
(
    DEPARTMENT_ID   NUMBER(19)         not null
        primary key,
    DEPARTMENT_NAME VARCHAR2(255 char) not null
        unique
);

-- 부서 Import Data
-- HUMAN_RESOURCE (인사과)
-- INTERNAL_MEDICINE (내과)
-- PSYCHIATRY (정신과)
-- SURGERY (외과)





