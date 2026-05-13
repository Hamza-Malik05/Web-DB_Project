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
    -- Insert Transaction
    INSERT INTO transactions (
        amount,
        type,
        date_of_transaction,
        accountant_id,
        payment_method
    ) VALUES (
                 p_amount,
                 'withdrawal',
                 CURRENT_DATE,
                 p_accountant_id,
                 LOWER(p_payment_method)::payment_method_type -- FIX: Convert to lowercase then cast
             )
    RETURNING transaction_id INTO new_transaction_id;

    -- Insert Bill
    INSERT INTO bills (
        transaction_id,
        bill_type,
        issue_date,
        due_date
    ) VALUES (
                 new_transaction_id,
                 LOWER(p_bill_type)::bill_type,               -- FIX: Convert to lowercase then cast
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

-- 3. Finding drivers by their ids.
CREATE OR REPLACE FUNCTION get_driver_info(
    p_driver_id INT DEFAULT NULL
)
    RETURNS TABLE (
                      driver_id INT,
                      employee_id INT,
                      first_name VARCHAR(50),
                      last_name VARCHAR(50),
                      license_no VARCHAR(50),
                      cnic VARCHAR(20)
                  )
    LANGUAGE plpgsql
AS $$
BEGIN
    -- 1. Safety check to ensure at least one parameter is provided
    IF p_driver_id IS NULL THEN
        RAISE EXCEPTION 'You must provide a driver_id';
    END IF;

    -- 2. Return the matching driver and their employee details
    RETURN QUERY
        SELECT
            d.driver_id,
            d.employee_id,
            e.first_name,
            e.last_name,
            d.license_no,
            e.cnic
        FROM driver d
                 JOIN employee e ON d.employee_id = e.employee_id
        WHERE (p_driver_id IS NOT NULL AND d.driver_id = p_driver_id);
END;
$$;

-- Function 1: Get Salary by Employee ID and Date
CREATE OR REPLACE FUNCTION get_salary_by_employee_and_date(
    p_employee_id INT,
    p_date DATE
)
    RETURNS TABLE (
                      salary_id INT,
                      transaction_id INT,
                      employee_id INT,
                      bonus NUMERIC,
                      fine NUMERIC
                  )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT s.salary_id, s.transaction_id, s.employee_id, s.bonus, s.fine
        FROM salaries s
                 JOIN transactions t ON s.transaction_id = t.transaction_id
        WHERE s.employee_id = p_employee_id AND t.date_of_transaction = p_date;
END;
$$;


-- Function 2: Get All Salaries by Date
CREATE OR REPLACE FUNCTION get_salaries_by_date(
    p_date DATE
)
    RETURNS TABLE (
                      salary_id INT,
                      transaction_id INT,
                      employee_id INT,
                      bonus NUMERIC,
                      fine NUMERIC
                  )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT s.salary_id, s.transaction_id, s.employee_id, s.bonus, s.fine
        FROM salaries s
                 JOIN transactions t ON s.transaction_id = t.transaction_id
        WHERE t.date_of_transaction = p_date;
END;
$$;

-- Function 3: Get All Bill Details
CREATE OR REPLACE FUNCTION get_all_bill_details()
    RETURNS TABLE (
                      bill_id INT,
                      amount NUMERIC,
                      issue_date DATE,   -- Changed from TIMESTAMP to DATE
                      due_date DATE,     -- Changed from TIMESTAMP to DATE
                      bill_type VARCHAR,
                      payment_method VARCHAR
                  )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT b.bill_id, t.amount, b.issue_date, b.due_date, b.bill_type, t.payment_method
        FROM bills b
                 JOIN transactions t ON b.transaction_id = t.transaction_id;
END;
$$;

-- View to get full details of bills, including transaction and accountant information
CREATE OR REPLACE VIEW v_bill_full_details AS
SELECT
    b.bill_id, b.issue_date, b.due_date, b.bill_type, b.transaction_id,
    t.amount, t.type AS transaction_type, t.date_of_transaction, t.payment_method,
    a.accountant_id, a.domain, a.employee_id
FROM bills b
         LEFT JOIN transactions t ON b.transaction_id = t.transaction_id
         LEFT JOIN accountant a ON t.accountant_id = a.accountant_id;

--View to get full details of deliveries, including order, vehicle and driver information
CREATE OR REPLACE VIEW v_delivery_details AS
SELECT
    d.delivery_id, d.departure_time, d.delivery_time, d.order_id, d.vehicle_id, d.driver_id,
    o.customer_id, o.employee_id AS order_employee_id, o.order_date, o.status AS order_status, o.address AS order_address,
    v.type AS vehicle_type, v.license_plate, v.model, v.capacity, v.status AS vehicle_status,
    dr.employee_id AS driver_employee_id, dr.license_no
FROM deliveries d
         LEFT JOIN orders o ON d.order_id = o.order_id
         LEFT JOIN vehicles v ON d.vehicle_id = v.vehicle_id
         LEFT JOIN driver dr ON d.driver_id = dr.driver_id;
-- View to get full details of product inventory storage, including product information
CREATE OR REPLACE VIEW v_product_inventory_storage AS
SELECT
    s.p_storage_unit_id,
    s.capacity,
    s.quantity_stored,
    s.product_id,
    p.name AS product_name,
    p.unit_of_measurement,
    p.price_per_unit
FROM product_inventory_storage s
         LEFT JOIN products p ON s.product_id = p.product_id;
-- View to get all drivers with their employee ids and license numbers
-- 1. Remove the old version
DROP VIEW IF EXISTS v_all_drivers;

-- 2. Create the new version with the correct Join
CREATE VIEW v_all_drivers AS
SELECT
    d.driver_id,
    d.license_no,
    e.employee_id,
    e.first_name,
    e.last_name,
    e.cnic,
    e.email
FROM driver d
         JOIN employee e ON d.employee_id = e.employee_id;

-- View to get full details of sales, including transaction and accountant information
CREATE OR REPLACE VIEW view_sales_details AS
SELECT
    s.sale_id,
    s.order_id,
    s.transaction_id,
    s.units_sold,
    s.status AS sale_status,
    t.amount,
    t.date_of_transaction,
    t.payment_method,
    e.first_name || ' ' || e.last_name AS accountant_name
FROM sales s
         JOIN transactions t ON s.transaction_id = t.transaction_id
         JOIN accountant a ON t.accountant_id = a.accountant_id
         JOIN employee e ON a.employee_id = e.employee_id;


-- View to get full details of salaries, including transaction and employee information
CREATE OR REPLACE VIEW view_salary_details AS
SELECT
    s.salary_id,
    s.bonus,
    s.fine,
    s.employee_id,
    e.first_name,
    e.last_name,
    e.designation,
    t.transaction_id,
    t.amount,
    t.payment_method,
    t.date_of_transaction,
    t.type AS transaction_type
FROM salaries s
         JOIN employee e ON s.employee_id = e.employee_id
         LEFT JOIN transactions t ON s.transaction_id = t.transaction_id;

-- Procedure to create a salary record for an employee. This procedure will first create a transaction record and then use the generated transaction_id to create a salary record linked to that transaction.
CREATE OR REPLACE PROCEDURE create_salary_record(
    p_employee_id INT,
    p_accountant_id INT,
    p_amount DECIMAL,
    p_bonus DECIMAL,
    p_fine DECIMAL,
    p_date DATE,
    p_payment_method payment_method_type
)
    LANGUAGE plpgsql
AS $$
DECLARE
    v_transaction_id INT;
BEGIN
    -- 1. Insert into transactions table first
    -- We set the type to 'EXPENSE' (or your equivalent enum value)
    INSERT INTO transactions (amount, type, date_of_transaction, accountant_id, payment_method)
    VALUES (p_amount + p_bonus - p_fine, 'salary', p_date, p_accountant_id, p_payment_method)
    RETURNING transaction_id INTO v_transaction_id;

    -- 2. Insert into salaries table using the new transaction_id
    INSERT INTO salaries (transaction_id, employee_id, bonus, fine)
    VALUES (v_transaction_id, p_employee_id, p_bonus, p_fine);

    -- Log success (optional)
    RAISE NOTICE 'Salary created for employee % with transaction %', p_employee_id, v_transaction_id;
END;
$$;