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


-----
--Jul3

CREATE TABLE IF NOT EXISTS deleted_records (
    id          BIGSERIAL PRIMARY KEY,
    table_name  VARCHAR(64)  NOT NULL,
    record_id   INTEGER      NOT NULL,
    deleted_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (table_name, record_id)
);
 
CREATE INDEX IF NOT EXISTS idx_deleted_records_lookup
    ON deleted_records (table_name, deleted_at);