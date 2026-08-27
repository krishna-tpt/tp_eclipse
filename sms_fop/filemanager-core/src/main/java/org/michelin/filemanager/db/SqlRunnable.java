package org.michelin.filemanager.db;

import java.sql.Connection;
import java.sql.SQLException;

/** Functional shape for Database.inTransaction(...) callers that don't return a value. */
@FunctionalInterface
public interface SqlRunnable {
    void apply(Connection c) throws SQLException;
}
