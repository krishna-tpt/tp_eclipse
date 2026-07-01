--truncate table 
SELECT 'truncate table '||table_name ||' CASCADE;'
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

SELECT 'SELECT setval(''' ||
    pg_get_serial_sequence(table_name, column_name) ||
    ''', COALESCE(MAX(' || column_name || '), 1)) FROM ' ||
    table_name || ';'
FROM information_schema.columns
WHERE table_schema = 'public'
  AND (
        column_default LIKE 'nextval%'
        OR is_identity = 'YES'
);

--insert script
CREATE UNIQUE INDEX uq_category_book_name
ON categories (book_id, name, type)
WHERE book_id IS NOT NULL;

CREATE UNIQUE INDEX uq_category_name_null_book
ON categories (name, type)
WHERE book_id IS NULL;


     