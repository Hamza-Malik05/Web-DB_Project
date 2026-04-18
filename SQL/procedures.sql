-- 1. Procedure creating a new bill and linking it to a transaction that is created on runtime of the procedure.
CREATE OR REPLACE PROCEDURE create_new_bill (
    p_amount DECIMAL(10,2),
    p_accountant_id INT,
    p_payment_method VARCHAR(50),
    p_bill_type VARCHAR(50),
    p_issue_date DATE,
    p_due_date DATE
)
    LANGUAGE plpgsql
AS $$
DECLARE
    new_transaction_id INT;
BEGIN
    -- The RETURNING clause must be inside the INSERT statement
    INSERT INTO transactions (
        amount,
        type,
        date_of_transaction,
        accountant_id,
        payment_method
    ) VALUES (
                 p_amount,
                 'withdrawal', -- Ensure this matches your transaction_type ENUM exactly
                 CURRENT_DATE,
                 p_accountant_id,
                 p_payment_method
             )
    RETURNING transaction_id INTO new_transaction_id; -- Moved inside the semicolon

    INSERT INTO bills (
        transaction_id,
        bill_type,
        issue_date,
        due_date
    ) VALUES (
                 new_transaction_id,
                 p_bill_type,
                 p_issue_date,
                 p_due_date
             );
END;
$$;

-- 2. Adding an employee to the database as a user of the system. The procedure checks if the employee already has a user, then it determines the role based on the department and, finally it inserts a new user record with the appropriate role.
CREATE OR REPLACE PROCEDURE register_user_from_employee(
    p_emp_id INT,
    p_uname VARCHAR(50),
    p_pwd VARCHAR(255)
)
    LANGUAGE plpgsql
AS $$
DECLARE
    v_emp_role VARCHAR(50);
BEGIN
    -- 1. Check if the employee already has a user
    IF EXISTS (SELECT 1 FROM users WHERE employee_id = p_emp_id) THEN
        RAISE EXCEPTION 'User already exists for this employee';
    END IF;

    -- 2. Get role based on department name
    SELECT
        CASE d.name
            WHEN 'HR' THEN 'hr_manager'
            WHEN 'Finance' THEN 'finance_manager'
            WHEN 'Sales' THEN 'sales_manager'
            WHEN 'Production' THEN 'production_supervisor'
            WHEN 'Warehouse' THEN 'warehouse_manager'
            WHEN 'Logistics' THEN 'warehouse_manager'
            END
    INTO v_emp_role
    FROM employee e
             JOIN department d ON e.dept_id = d.dept_id
    WHERE e.employee_id = p_emp_id;

    -- 3. Check if v_emp_role is null
    IF v_emp_role IS NULL THEN
        RAISE EXCEPTION 'Invalid employee ID or department not found';
    ELSE
        -- 4. Insert into users table with the required type cast
        INSERT INTO users (employee_id, username, password, role)
        VALUES (p_emp_id, p_uname, p_pwd, v_emp_role::role_type);
    END IF;
END;
$$;