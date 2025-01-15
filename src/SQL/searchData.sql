SELECT *
FROM patient_record_card
WHERE employee_no = 351
  AND clinic_date BETWEEN '2025-01-07' AND '2025-01-13';

SELECT *
FROM reservation
WHERE employee_no = 1
  AND reservation_date = '2025-01-15'
  AND time_slot = 0;