SELECT *
FROM patient_record_card
WHERE employee_no = 351
  AND clinic_date BETWEEN '2025-01-07' AND '2025-01-13';

SELECT *
FROM reservation
WHERE employee_no = 1
  AND reservation_date = '2025-01-15'
  AND time_slot = 0;


select * from employee where employee_id = 'admin1234';
select * from department where department_id = 1;

SELECT constraint_name, table_name, constraint_type, search_condition
FROM user_constraints
WHERE constraint_name = 'SYS_C008454';

SELECT MAX(employee_no) FROM EMPLOYEE;

SELECT employee_seq.CURRVAL FROM dual;


SELECT constraint_name, table_name, r_constraint_name
FROM user_constraints
WHERE constraint_name = 'FKBEJTWVG9BXUS2MFFSM3SWJ3U9';

SELECT column_name, constraint_name, r_constraint_name, table_name
FROM user_cons_columns
WHERE table_name = 'EMPLOYEE';

SELECT * FROM department;