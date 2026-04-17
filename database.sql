-- DEPARTMENTS
CREATE TABLE department (
                            dept_id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL
);

-- EMPLOYEES
CREATE TYPE gender_type AS ENUM ('male', 'female');
CREATE TABLE employee (
                          employee_id SERIAL PRIMARY KEY,
                          dept_id INT,
                          first_name VARCHAR(50),
                          last_name VARCHAR(50),
                          date_of_birth DATE,
                          cnic VARCHAR(20) UNIQUE,
                          email VARCHAR(100) UNIQUE,
                          designation VARCHAR(50),
                          address VARCHAR(255),
                          gender gender_type,
                          absences INT DEFAULT 0,
                          leaves INT DEFAULT 21,
                          CONSTRAINT fk_employee_department
                          FOREIGN KEY (dept_id) REFERENCES department(dept_id)
                              ON DELETE SET NULL
                              ON UPDATE CASCADE
);

-- USER MANAGEMENT
CREATE TYPE role_type AS ENUM (
    'hr_manager',
    'warehouse_manager',
    'production_supervisor',
    'finance_manager',
    'sales_manager',
    'admin'
);
CREATE TABLE users (
                      user_id SERIAL PRIMARY KEY,
                      employee_id INT,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      role role_type NOT NULL,
                      CONSTRAINT fk_users_employee
                      FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                          ON DELETE CASCADE
                          ON UPDATE CASCADE
);


-- DRIVERS
CREATE TABLE driver (
                        driver_id SERIAL PRIMARY KEY ,
                        employee_id INT,
                        license_no VARCHAR(50),
                        PRIMARY KEY (driver_id, employee_id),
                        CONSTRAINT fk_driver_employee
                        FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                            ON DELETE CASCADE
                            ON UPDATE CASCADE
);

-- ACCOUNTANTS
CREATE TABLE accountant (
                            accountant_id INT AUTO_INCREMENT PRIMARY KEY,
                            employee_id INT,
                            domain VARCHAR(100),
                            CONSTRAINT fk_accountant_employee
                            FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);

-- SUPERVISORS
CREATE TABLE supervisor (
                            supervisor_id SERIAL PRIMARY KEY ,
                            employee_id INT,
                            office_no VARCHAR(50),
                            PRIMARY KEY (supervisor_id, employee_id),
                            CONSTRAINT fk_supervisor_employee
                            FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);

-- PRODUCTS
CREATE TABLE products (
                          product_id SERIAL PRIMARY KEY,
                          name VARCHAR(100),
                          unit_of_measurement VARCHAR(20),
                          price_per_unit DECIMAL(12,2)
);

-- RAW MATERIAL STORAGE
CREATE TABLE raw_material_inventory_storage (
                                                r_storage_unit_id SERIAL PRIMARY KEY,
                                                capacity DECIMAL(12,2),
                                                quantity_stored DECIMAL(12,2)
);

-- PRODUCT INVENTORY STORAGE
CREATE TABLE product_inventory_storage (
                                           p_storage_unit_id SERIAL PRIMARY KEY,
                                           capacity DECIMAL(12,2),
                                           quantity_stored DECIMAL(12,2),
                                           product_id INT,
                                           CONSTRAINT fk_product_inventory_storage_product
                                           FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- SUPPLIERS
CREATE TABLE suppliers (
                           supplier_id SERIAL PRIMARY KEY,
                           name VARCHAR(100),
                           email VARCHAR(100),
                           phone VARCHAR(20),
                           address VARCHAR(255),
                           city VARCHAR(50)
);

-- PURCHASES
CREATE TABLE purchases (
                           purchase_id SERIAL PRIMARY KEY,
                           supplier_id INT,
                           date_of_purchase DATE,
                           delivery_date DATE,
                           unit_of_measurement VARCHAR(20),
                           units_bought DECIMAL(12,2),
                           price_per_unit DECIMAL(12,2) DEFAULT 30,
                           CONSTRAINT fk_purchases_supplier
                           FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
);

-- BATCHES
CREATE TABLE batches (
                         batch_id SERIAL PRIMARY KEY,
                         product_id INT,
                         start_time TIMESTAMP,
                         end_time TIMESTAMP,
                         quantity_used DECIMAL(12,2),
                         quantity_produced DECIMAL(12,2),
                         employee_id INT,
                         r_storage_unit_id INT,
                         p_storage_unit_id INT,
                         CONSTRAINT fk_batches_product
                         FOREIGN KEY (product_id) REFERENCES products(product_id)
                             ON DELETE RESTRICT
                             ON UPDATE CASCADE,
                            CONSTRAINT fk_batches_employee
                         FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                             ON DELETE SET NULL
                             ON UPDATE CASCADE,
                         CONSTRAINT fk_batches_r_storage
                         FOREIGN KEY (r_storage_unit_id) REFERENCES raw_material_inventory_storage(r_storage_unit_id)
                             ON DELETE SET NULL
                             ON UPDATE CASCADE,
                         CONSTRAINT fk_batches_p_storage
                         FOREIGN KEY (p_storage_unit_id) REFERENCES product_inventory_storage(p_storage_unit_id)
                             ON DELETE SET NULL
                             ON UPDATE CASCADE
);

-- CUSTOMERS
CREATE TABLE customers (
                           customer_id SERIAL PRIMARY KEY,
                           customer_name VARCHAR(100),
                           email VARCHAR(100),
                           phone VARCHAR(20),
                           address VARCHAR(255)
);

-- ORDERS
CREATE TYPE order_status AS ENUM ('pending', 'processing','delivered', 'cancelled','shipped');
CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        customer_id INT,
                        employee_id INT,
                        order_date DATE,
                        status order_status,
                        address VARCHAR(300),
                        CONSTRAINT fk_orders_customer
                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                            ON DELETE SET NULL
                            ON UPDATE CASCADE,
                        CONSTRAINT fk_orders_employee
                        FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                            ON DELETE SET NULL
                            ON UPDATE CASCADE
);

-- ORDER PRODUCTS
CREATE TABLE orders_products (
                                 order_id INT,
                                 product_id INT,
                                 quantity DECIMAL(12,2),
                                 PRIMARY KEY (order_id, product_id),
                                 CONSTRAINT fk_orders_products_order
                                 FOREIGN KEY (order_id) REFERENCES orders(order_id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE,
                                 CONSTRAINT fk_orders_products_product
                                 FOREIGN KEY (product_id) REFERENCES products(product_id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
);

-- VEHICLES
CREATE TYPE vehicle_status AS ENUM ('Active', 'Inactive', 'Under Maintenance');
CREATE TYPE vehicle_type as ENUM ('truck','van','loader','bike','other');
CREATE TABLE vehicles (
                          vehicle_id SERIAL PRIMARY KEY,
                          type vehicle_type,
                          license_plate VARCHAR(20),
                          model VARCHAR(50),
                          capacity DECIMAL(12,2),
                          status vehicle_status
);

-- DELIVERIES
CREATE TABLE deliveries (
                            delivery_id SERIAL PRIMARY KEY,
                            order_id INT,
                            vehicle_id INT,
                            driver_id INT,
                            departure_time TIMESTAMP,
                            delivery_time TIMESTAMP,
                            CONSTRAINT fk_deliveries_order
                            FOREIGN KEY (order_id) REFERENCES orders(order_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE,
                            CONSTRAINT fk_deliveries_vehicle
                            FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
                                ON DELETE SET NULL
                                ON UPDATE CASCADE,
                            CONSTRAINT fk_deliveries_driver
                            FOREIGN KEY (driver_id) REFERENCES driver(driver_id)
                                ON DELETE SET NULL
                                ON UPDATE CASCADE
);

-- ATTENDANCE
CREATE TYPE attendance_status AS ENUM ('present', 'absent', 'late');
CREATE TABLE attendance (
                            attendance_id SERIAL PRIMARY KEY,
                            employee_id INT,
                            date DATE,
                            clock_in TIME,
                            clock_out TIME,
                            status attendance_status
                            CONSTRAINT fk_attendance_employee
                            FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
);

-- TRANSACTIONS
CREATE TYPE transaction_type AS ENUM ('withdrawal', 'deposit');
CREATE TYPE payment_method_type AS ENUM ('Cash', 'Bank Transfer', 'Cheque', 'Mobile Payment');
CREATE TABLE transactions (
                              transaction_id SERIAL PRIMARY KEY,
                              amount DECIMAL(10,2),
                              type transaction_type,
                              date_of_transaction DATE,
                              accountant_id INT,
                              payment_method payment_method_type,
                              CONSTRAINT fk_transactions_accountant
                              FOREIGN KEY (accountant_id) REFERENCES accountant(accountant_id)
);

-- BILLS
CREATE TYPE bill_type AS ENUM ('Utility', 'Maintenance', 'Raw Material', 'Office Supplies', 'Other');
CREATE TABLE bills (
                       bill_id SERIAL,
                       transaction_id INT,
                       bill_type bill_type,
                       issue_date DATE,
                       due_date DATE,
                       PRIMARY KEY (bill_id, transaction_id),
                       CONSTRAINT fk_bills_transaction
                       FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE
);

-- SALARIES
CREATE TABLE salaries (
                          salary_id SERIAL,
                          transaction_id INT,
                          employee_id INT,
                          bonus DECIMAL(10,2),
                          fine DECIMAL(10,2),
                          PRIMARY KEY (salary_id, transaction_id),
                          CONSTRAINT fk_salaries_transaction
                          FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE,
                          CONSTRAINT fk_salaries_employee
                          FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE
);

-- SALES
CREATE TYPE sale_status as ENUM ('completed','pending','cancelled');
CREATE TABLE sales (
                       sale_id SERIAL,
                       transaction_id INT,
                       order_id INT,
                       units_sold FLOAT,
                       status VARCHAR(50),
                       PRIMARY KEY (sale_id, transaction_id),
                       CONSTRAINT fk_sales_transactions
                       FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE,
                       CONSTRAINT fk_sales_orders
                       FOREIGN KEY (order_id) REFERENCES orders(order_id)
                           ON DELETE CASCADE
                           ON UPDATE CASCADE
);



INSERT INTO department ( name) VALUES
                                           ( 'HR'),
                                           ( 'Warehouse'),
                                           ( 'Production'),
                                           ( 'Finance'),
                                           ( 'Sales'),
                                           ( 'Logistics');

INSERT INTO employee ( dept_id, first_name, last_name, date_of_birth, cnic, email, designation, address, gender, absences, leaves) VALUES
                                                                                                                                                   ( 1, 'Alice', 'Johnson', '1990-05-15', '35201-1234567-1', 'hr@flourmill.com', 'HR Manager', '123 Elm Street', 'female', 2, 19),
                                                                                                                                                   ( 2, 'Bob', 'Smith', '1985-03-22', '35201-2345678-2', 'warehouse@flourmill.com', 'Warehouse Manager', '456 Maple Avenue', 'male', 0, 21),
                                                                                                                                                   ( 3, 'Charlie', 'Brown', '1992-11-30', '35201-3456789-3', 'production@flourmill.com', 'Production Supervisor', '789 Oak Lane', 'male', 1, 20),
                                                                                                                                                   ( 4, 'Diana', 'Prince', '1988-07-08', '35201-4567890-4', 'finance@flourmill.com', 'Finance Manager', '321 Birch Road', 'female', 0, 21),
                                                                                                                                                   ( 5, 'Edward', 'Norton', '1995-12-25', '35201-5678901-5', 'sales@flourmill.com', 'Sales Representative', '654 Pine Street', 'male', 0, 21),
                                                                                                                                                   ( 1, 'Ali', 'Khan', '1985-03-15', '12345-6789012-3', 'ali.khan@company.com', 'Admin', '123 Main Street, Lahore', 'male', 0, 21),
                                                                                                                                                   ( 6, 'Zain', 'Malik', '1990-05-20', '98765-4321098-7', 'zain.malik@company.com', 'Driver', '456 Elm Avenue, Karachi', 'male', 0, 21),
                                                                                                                                                   ( 4, 'Fatima', 'Ahmed', '1988-07-10', '34567-8901234-5', 'fatima.ahmed@company.com', 'Accountant', '789 Oak Road, Islamabad', 'female', 0, 21),
                                                                                                                                                   ( 4, 'Hassan', 'Iqbal', '1992-11-05', '54321-0987654-3', 'hassan.iqbal@company.com', 'Accountant', '101 Pine Lane, Peshawar', 'male', 0, 21),
                                                                                                                                                   ( 3, 'Ayesha', 'Noor', '1987-09-25', '11223-4455667-8', 'ayesha.noor@company.com', 'Supervisor', '234 Maple Drive, Faisalabad', 'female', 0, 21),
                                                                                                                                                   ( 3, 'Rehan', 'Ali', '1995-01-30', '22133-6677889-1', 'rehan.ali@company.com', 'Supervisor', '567 Birch Boulevard, Multan', 'male', 2, 19);

INSERT INTO users ( employee_id, username, password, role) VALUES
                                                                      ( 1, 'alice_j', 'secure123', 'hr_manager'),
                                                                      ( 2, 'bob_s', 'passw0rd', 'warehouse_manager'),
                                                                      ( 3, 'charlie_b', 'flourProd', 'production_supervisor'),
                                                                      ( 4, 'diana_p', 'financeMe', 'finance_manager'),
                                                                      ( 5, 'edward_n', 'salesHub', 'sales_manager'),
                                                                      (6,'ali_k','admin','admin');



INSERT INTO driver ( employee_id, license_no) VALUES
                                                            ( 6, 'DR-12345'),
                                                            ( 7, 'DR-67890');

INSERT INTO accountant ( employee_id, domain) VALUES
                                                                ( 8, 'Finance Management'),
                                                                ( 9, 'Taxation and Auditing');

INSERT INTO supervisor ( employee_id, office_no) VALUES
                                                                   ( 10, 'S-101'),
                                                                   ( 11, 'S-102');

INSERT INTO products ( name, unit_of_measurement, price_per_unit) VALUES
                                                                                 ( 'Wheat Flour', 'kg', 50.00),
                                                                                 ( 'Semolina', 'kg', 55.00),
                                                                                 ( 'Bran', 'kg', 25.00),
                                                                                 ( 'Whole Wheat Flour', 'kg', 60.00),
                                                                                 ( 'Refined Flour', 'kg', 65.00),
                                                                                 ( 'Multigrain Flour', 'kg', 70.00);

INSERT INTO raw_material_inventory_storage ( capacity, quantity_stored) VALUES
                                                                                              ( 5000.00, 3000.00),
                                                                                              ( 6000.00, 4500.00),
                                                                                              ( 4000.00, 2000.00),
                                                                                              ( 7000.00, 3500.00),
                                                                                              ( 8000.00, 6000.00),
                                                                                              ( 9000.00, 0.00);

INSERT INTO product_inventory_storage ( capacity, quantity_stored, product_id) VALUES
                                                                                                     ( 1000.0, 750.0, 1), -- Product ID 1: Wheat Flour
                                                                                                     ( 1200.0, 500.0, 2), -- Product ID 2: Semolina
                                                                                                     ( 800.0, 200.0, 3),  -- Product ID 3: Bran
                                                                                                     ( 1500.0, 1200.0, 4), -- Product ID 4: Whole Wheat Flour
                                                                                                     ( 1000.0, 600.0, 5), -- Product ID 5: Refined Flour
                                                                                                     ( 900.0, 300.0, 6);  -- Product ID 6: Gluten-Free Flour

INSERT INTO suppliers ( name, email, phone, address, city) VALUES
                                                                           ( 'ABC Supplies', 'abc@supplies.com', '123-456-7890', '10 Supply Road', 'Islamabad'),
                                                                           ( 'XYZ Trading', 'xyz@trading.com', '987-654-3210', '20 Commerce St', 'Lahore'),
                                                                           ( 'Superior Goods', 'superior@goods.com', '456-789-0123', '30 Market Lane', 'Karachi'),
                                                                           ( 'Global Parts', 'global@parts.com', '789-012-3456', '40 Import Ave', 'Faisalabad'),
                                                                           ( 'Raw Material Co', 'raw@material.com', '321-654-9870', '50 Source Blvd', 'Multan');

INSERT INTO purchases ( supplier_id, date_of_purchase, delivery_date, unit_of_measurement, units_bought) VALUES
                                                                                                                         ( 1, '2025-04-20', '2025-04-22', 'kg', 2000.00),
                                                                                                                         ( 2, '2025-04-21', '2025-04-23', 'kg', 1500.00),
                                                                                                                         ( 3, '2025-04-22', '2025-04-24', 'kg', 1800.00),
                                                                                                                         ( 4, '2025-04-23', '2025-04-25', 'kg', 2200.00),
                                                                                                                         ( 5, '2025-04-24', '2025-04-26', 'kg', 2500.00);

INSERT INTO batches ( product_id, start_time, end_time, quantity_used, quantity_produced, employee_id,r_storage_unit_id,p_storage_unit_id) VALUES
                                                                                                                                                        ( 1, '2025-04-28 08:00:00', '2025-04-28 16:00:00', 1000.00, 800.00, 3,1,1),
                                                                                                                                                        ( 2, '2025-04-28 09:00:00', '2025-04-28 17:00:00', 1200.00, 950.00, 3,1,2),
                                                                                                                                                        ( 3, '2025-04-29 08:30:00', '2025-04-29 16:30:00', 1100.00, 850.00, 3,1,3),
                                                                                                                                                        ( 4, '2025-04-29 10:00:00', '2025-04-29 18:00:00', 1300.00, 1000.00, 3,1,4),
                                                                                                                                                        ( 5, '2025-04-30 08:00:00', '2025-04-30 16:00:00', 1150.00, 870.00, 3, 2, 5),
                                                                                                                                                        ( 6, '2025-04-30 09:00:00', '2025-04-30 17:00:00', 1250.00, 930.00, 3, 2, 6);

INSERT INTO customers ( customer_name, email, phone, address) VALUES
                                                                              ( 'John Doe', 'john.doe@email.com', '03123456789', '123 Baker Street, Lahore'),
                                                                              ( 'Jane Smith', 'jane.smith@email.com', '03211234567', '456 Main Road, Karachi'),
                                                                              ( 'David Williams', 'david.williams@email.com', '03334455667', '789 Park Avenue, Islamabad'),
                                                                              ( 'Emily Johnson', 'emily.johnson@email.com', '03445566778', '321 Maple Lane, Peshawar'),
                                                                              ( 'Michael Brown', 'michael.brown@email.com', '03556677889', '654 Oak Drive, Multan'),
                                                                              ( 'Sarah Wilson', 'sarah.wilson@email.com', '03667788990', '987 Birch Boulevard, Faisalabad');

INSERT INTO orders ( customer_id, employee_id, order_date, status, address) VALUES
                                                                                         ( 1, 5, '2025-04-15', 'delivered', '123 Baker Street, Lahore'),
                                                                                         ( 2, 5, '2025-04-16', 'pending', '456 Main Road, Karachi'),
                                                                                         ( 3, 5, '2025-04-17', 'delivered', '789 Park Avenue, Islamabad'),
                                                                                         ( 4, 5, '2025-04-18', 'cancelled', '321 Maple Lane, Peshawar'),
                                                                                         ( 5, 5, '2025-04-19', 'pending', '654 Oak Drive, Multan'),
                                                                                         ( 6, 5, '2025-04-20', 'delivered', '987 Birch Boulevard, Faisalabad');

INSERT INTO orders_products (order_id, product_id, quantity) VALUES
                                                                 (1, 1, 500.0), -- Order 1: 500 kg of Wheat Flour
                                                                 (1, 2, 200.0), -- Order 1: 200 kg of Semolina
                                                                 (2, 4, 1000.0), -- Order 2: 1000 kg of Whole Wheat Flour
                                                                 (2, 5, 300.0), -- Order 2: 300 kg of Refined Flour
                                                                 (3, 3, 150.0), -- Order 3: 150 kg of Bran
                                                                 (4, 6, 400.0), -- Order 4: 400 kg of Gluten-Free Flour
                                                                 (5, 1, 250.0), -- Order 5: 250 kg of Wheat Flour
                                                                 (5, 2, 150.0), -- Order 5: 150 kg of Semolina
                                                                 (6, 5, 700.0), -- Order 6: 700 kg of Refined Flour
                                                                 (6, 4, 500.0); -- Order 6: 500 kg of Whole Wheat Flour

INSERT INTO vehicles ( type, license_plate, model, capacity, status) VALUES
                                                                                    ( 'truck', 'ABC-123', 'Hino 500', 5000.00, 'active'),
                                                                                    ( 'van', 'XYZ-456', 'Toyota HiAce', 3000.00, 'active'),
                                                                                    ( 'loader', 'LMN-789', 'Isuzu D-Max', 2000.00, 'active'),
                                                                                    ( 'truck', 'DEF-101', 'Mercedes Actros', 6000.00, 'inactive'),
                                                                                    ( 'van', 'PQR-202', 'Ford Transit', 2500.00, 'active');

INSERT INTO deliveries ( order_id, vehicle_id, driver_id, departure_time, delivery_time) VALUES
                                                                                                         ( 1, 1, 1, '2025-04-25 08:00:00', '2025-04-25 12:00:00'),
                                                                                                         ( 2, 2, 2, '2025-04-26 09:00:00', NULL),
                                                                                                         ( 3, 3, 1, '2025-04-27 10:00:00', '2025-04-27 14:00:00'),
                                                                                                         ( 5, 5, 1, '2025-04-29 12:00:00', NULL),
                                                                                                         ( 6, 1, 1, '2025-05-2 08:00:00', '2025-05-2 16:00:00');

INSERT INTO attendance ( employee_id, date, clock_in, clock_out, status) VALUES
                                                                                           ( 1, '2025-04-01', '08:00:00', '17:00:00', 'present'),  -- Alice (HR Manager)
                                                                                           ( 2, '2025-04-01', '08:30:00', '17:30:00', 'late'),     -- Bob (Warehouse Manager)
                                                                                           ( 3, '2025-04-01', '08:00:00', '17:00:00', 'present'),  -- Charlie (Production Supervisor)
                                                                                           ( 4, '2025-04-01', '08:00:00', '17:00:00', 'present'),  -- Diana (Finance Manager)
                                                                                           ( 5, '2025-04-01', '08:15:00', '17:15:00', 'late'),     -- Edward (Sales Manager)
                                                                                           ( 6, '2025-04-01', '08:00:00', '17:00:00', 'present'),  -- Ali (Driver)
                                                                                           ( 7, '2025-04-01', '08:10:00', '17:10:00', 'present'),  -- Zain (Driver)
                                                                                           ( 8, '2025-04-01', '08:00:00', '17:00:00', 'present'),  -- Fatima (Accountant)
                                                                                           ( 9, '2025-04-01', '08:00:00', '17:00:00', 'present'),  -- Hassan (Accountant)
                                                                                           ( 10, '2025-04-01', '08:00:00', '17:00:00', 'present'), -- Ayesha (Supervisor)
                                                                                           ( 11, '2025-04-01', '08:00:00', '17:00:00', 'absent'),  -- Rehan (Supervisor)

                                                                                           ( 1, '2025-04-02', '08:00:00', '17:00:00', 'present'),  -- Alice (HR Manager)
                                                                                           ( 2, '2025-04-02', '08:45:00', '17:30:00', 'late'),     -- Bob (Warehouse Manager)
                                                                                           ( 3, '2025-04-02', '08:00:00', '17:00:00', 'present'),  -- Charlie (Production Supervisor)
                                                                                           ( 4, '2025-04-02', '08:00:00', '17:00:00', 'present'),  -- Diana (Finance Manager)
                                                                                           ( 5, '2025-04-02', '08:20:00', '17:10:00', 'late'),     -- Edward (Sales Manager)
                                                                                           ( 6, '2025-04-02', '08:00:00', '17:00:00', 'present'),  -- Ali (Driver)
                                                                                           ( 7, '2025-04-02', '08:10:00', '17:05:00', 'present'),  -- Zain (Driver)
                                                                                           ( 8, '2025-04-02', '08:00:00', '17:00:00', 'present'),  -- Fatima (Accountant)
                                                                                           ( 9, '2025-04-02', '08:00:00', '17:00:00', 'present'),  -- Hassan (Accountant)
                                                                                           ( 10, '2025-04-02', '08:00:00', '17:00:00', 'present'), -- Ayesha (Supervisor)
                                                                                           ( 11, '2025-04-02', '08:00:00', '17:00:00', 'absent');  -- Rehan (Supervisor)


INSERT INTO transactions ( amount, type, date_of_transaction, accountant_id, payment_method) VALUES
-- Corresponding to Salaries
( 80000.00, 'withdrawal', '2025-04-01', 1, 'Bank Transfer'), -- Salary for Alice
( 75000.00, 'withdrawal', '2025-04-01', 2, 'Bank Transfer'), -- Salary for Bob
( 78000.00, 'withdrawal', '2025-04-01', 1, 'Bank Transfer'), -- Salary for Charlie
( 95000.00, 'withdrawal', '2025-04-01', 2, 'Bank Transfer'), -- Salary for Diana
( 72000.00, 'withdrawal', '2025-04-01', 1, 'Bank Transfer'), -- Salary for Edward
( 50000.00, 'withdrawal', '2025-04-01', 2, 'Cash'),         -- Salary for Ali
( 52000.00, 'withdrawal', '2025-04-01', 1, 'Cash'),         -- Salary for Zain
( 85000.00, 'withdrawal', '2025-04-01', 2, 'Bank Transfer'), -- Salary for Fatima
( 86000.00, 'withdrawal', '2025-04-01', 1, 'Bank Transfer'), -- Salary for Hassan
( 78000.00, 'withdrawal', '2025-04-01', 2, 'Bank Transfer'), -- Salary for Ayesha
(80000.00, 'withdrawal', '2025-04-01', 1, 'Bank Transfer'), -- Salary for Rehan

-- Corresponding to Bills
( 12000.00, 'withdrawal', '2025-03-15', 1, 'Bank Transfer'), -- Utility Bill
( 8000.00, 'withdrawal', '2025-03-20', 2, 'Cash'),           -- Maintenance Bill
( 15000.00, 'withdrawal', '2025-03-25', 1, 'Bank Transfer'), -- Raw Material Bill
( 10000.00, 'withdrawal', '2025-03-30', 2, 'Bank Transfer'), -- Office Supplies Bill

-- Corresponding to Sales
( 250000.00, 'deposit', '2025-03-10', 1, 'Bank Transfer'),   -- Sale Payment
( 180000.00, 'deposit', '2025-03-20', 1, 'Cash'),            -- Sale Payment
( 300000.00, 'deposit', '2025-03-25', 2, 'Bank Transfer');   -- Sale Payment


INSERT INTO bills ( transaction_id, bill_type,issue_date, due_date) VALUES
                                                                                ( 12, 'Utility','2025-03-01', '2025-03-20'),
                                                                                ( 13, 'Maintenance','2025-03-01', '2025-03-25'),
                                                                                ( 14, 'Raw Material','2025-03-01', '2025-03-30'),
                                                                                ( 15, 'Office Supplies','2025-03-01', '2025-04-05');

INSERT INTO salaries ( transaction_id, employee_id, bonus, fine) VALUES
                                                                               ( 1, 1, 2000.00, 0.00), -- Alice (HR Manager)
                                                                               ( 2, 2, 1500.00, 100.00), -- Bob (Warehouse Manager)
                                                                               ( 3, 3, 1800.00, 50.00), -- Charlie (Production Supervisor)
                                                                               ( 4, 4, 2500.00, 0.00), -- Diana (Finance Manager)
                                                                               ( 5, 5, 1700.00, 200.00), -- Edward (Sales Manager)
                                                                               ( 6, 6, 1000.00, 0.00), -- Ali (Driver)
                                                                               ( 7, 7, 1100.00, 50.00), -- Zain (Driver)
                                                                               ( 8, 8, 2200.00, 0.00), -- Fatima (Accountant)
                                                                               ( 9, 9, 2100.00, 100.00), -- Hassan (Accountant)
                                                                               ( 10, 10, 1900.00, 0.00), -- Ayesha (Supervisor)
                                                                               ( 11, 11, 2000.00, 50.00); -- Rehan (Supervisor)

INSERT INTO sales ( transaction_id, order_id, units_sold, status) VALUES
                                                                              (16,  1, 700.0, 'completed'),
                                                                              (17,  3, 400.0, 'completed'),
                                                                              (18,  6, 500.0, 'completed');