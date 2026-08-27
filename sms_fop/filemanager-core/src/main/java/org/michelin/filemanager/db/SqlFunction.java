package org.michelin.filemanager.db;

import java.sql.Connection;
import java.sql.SQLException;

/** Functional shape for Database.inTransaction(...) callers. */
@FunctionalInterface
public interface SqlFunction<T> {
    T apply(Connection c) throws SQLException;
}
