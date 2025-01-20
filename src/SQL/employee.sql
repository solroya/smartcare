-- 내과 직원
INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'internal_doc1',
             '내과나인광사원',
             'pass123',
             'doc1.internal@example.com',
          0,
             TO_DATE('1985-03-15', 'YYYY-MM-DD'),
             '010-1111-1111',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             1, -- 내과
             SYSDATE,
             SYSDATE
         );

INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'internal_doc2',
             '내과박주연사원',
             'pass456',
             'doc2.internal@example.com',
          0,
             TO_DATE('1990-06-20', 'YYYY-MM-DD'),
             '010-2222-2222',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             1, -- 내과
             SYSDATE,
             SYSDATE
         );

INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'internal_doc3',
             '내과김지현과장',
             'pass456',
             'doc3.internal@example.com',
          0,
             TO_DATE('1990-06-20', 'YYYY-MM-DD'),
             '011-2222-2222',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             1, -- 내과
             SYSDATE,
             SYSDATE
         );

-- 외과 직원
INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'surgery_doc1',
             '외과김철순이사',
             'pass789',
             'doc1.surgery@example.com',
          0,
             TO_DATE('1978-12-05', 'YYYY-MM-DD'),
             '010-3333-3333',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             2, -- 외과
             SYSDATE,
             SYSDATE
         );

INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'surgery_doc2',
             '외과최지영인턴',
             'pass012',
             'doc2.surgery@example.com',
          0,
             TO_DATE('1982-07-11', 'YYYY-MM-DD'),
             '010-4444-4444',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             2, -- 외과
             SYSDATE,
             SYSDATE
         );


INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'surgery_doc3',
             '외과김철희',
             'pass012',
             'doc3.surgery@example.com',
          0,
             TO_DATE('1983-07-11', 'YYYY-MM-DD'),
             '010-4444-4544',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             2, -- 외과
             SYSDATE,
             SYSDATE
         );

-- 정신과 직원
INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'psychiatry_doc1',
             '정신과김지현사장',
             'pass345',
             'doc1.psychiatry@example.com',
          0,
             TO_DATE('1988-01-30', 'YYYY-MM-DD'),
             '010-5555-5555',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             3, -- 정신과
             SYSDATE,
             SYSDATE
         );

INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'psychiatry_doc2',
             '정신과나인과인턴',
             'pass678',
             'doc2.psychiatry@example.com',
          1,
             TO_DATE('1999-09-23', 'YYYY-MM-DD'),
             '010-6123-6676',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             3, -- 정신과
             SYSDATE,
             SYSDATE
         );


INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,
             'psychiatry_doc3',
             '정신과마지막의사',
             'pass678',
             'doc3.psychiatry@example.com',
          1,
             TO_DATE('1994-09-23', 'YYYY-MM-DD'),
             '010-6667-6666',
             0,
             'PENDING',
             'DOCTOR',
             'WORKING',
             3, -- 정신과
             SYSDATE,
             SYSDATE
         );

INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,      -- Auto-generated by sequence
             'jane.smith',              -- employee_id
             'Jane Smith',              -- employee_name
             'password123',             -- employee_pass
             'jane.smith@example.com',  -- employee_email
    1,
             TO_DATE('1985-03-15', 'YYYY-MM-DD'), -- employee_birthday
             '010-5678-1234',           -- employee_phone_number
             1,                         -- is_social (0: false, 1: true)
             'PENDING',                  -- employee_status
             'HUMAN_RESOURCE',          -- type_of_employee
             'WORKING',                 -- working_status
             4,                         -- department_id (HUMAN_RESOURCE의 ID)
             SYSDATE,                   -- created_at
             SYSDATE                    -- updated_at
         );

INSERT INTO employee (
    employee_no,
    employee_id,
    employee_name,
    employee_pass,
    employee_email,
    employee_gender,
    employee_birthday,
    employee_phone_number,
    is_social,
    employee_status,
    type_of_employee,
    working_status,
    department_id,
    created_at,
    updated_at
) VALUES (
             employee_seq.NEXTVAL,      -- Auto-generated by sequence
             'michael.lee',             -- employee_id
             'Michael Lee',             -- employee_name
             'securepass456',           -- employee_pass
             'michael.lee@example.com', -- employee_email
    0,
             TO_DATE('1992-07-25', 'YYYY-MM-DD'), -- employee_birthday
             '010-8765-4321',           -- employee_phone_number
             0,                         -- is_social (0: false, 1: true)
             'PENDING',                  -- employee_status
             'HUMAN_RESOURCE',          -- type_of_employee
             'WORKING',                 -- working_status
             4,                         -- department_id (HUMAN_RESOURCE의 ID)
             SYSDATE,                   -- created_at
             SYSDATE                    -- updated_at
         );


SELECT constraint_name, search_condition
FROM user_constraints
WHERE table_name = 'EMPLOYEE'
  AND constraint_type = 'C';