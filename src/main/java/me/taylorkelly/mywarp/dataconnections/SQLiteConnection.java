/**
 * Copyright (C) 2011 - 2014, MyWarp team and contributors
 *
 * This file is part of MyWarp.
 *
 * MyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyWarp. If not, see <http://www.gnu.org/licenses/>.
 */
package me.taylorkelly.mywarp.dataconnections;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.google.common.base.Function;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * The connection to a SQlite database.
 */
public class SQLiteConnection {

    /**
     * Block initialization of this class.
     */
    private SQLiteConnection() {
    }

    /**
     * Gets a valid connection to the given SQLite database. The connection is
     * created asynchronous, the returned CheckedFuture either contains the
     * ready-to-use connection or throws a {@link DataConnectionException}.
     * 
     * @param database
     *            the database file
     * @param controlDBLayout
     *            whether the implementation should create tables and execute
     *            updates, if necessary
     * @return a valid, setup connection to the SQLite database
     */
    public static CheckedFuture<DataConnection, DataConnectionException> getConnection(final File database,
            final boolean controlDBLayout) {
        final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors
                .newSingleThreadExecutor());

        ListenableFuture<DataConnection> futureConnection = executor.submit(new Callable<DataConnection>() {

            @Override
            public DataConnection call() throws DataConnectionException {
                String dsn = "jdbc:sqlite://" + database.getAbsolutePath();
                try {
                    // Manually load SQLite driver. DriveManager is unable to
                    // identify it as the driver does not follow JDBC 4.0
                    // standards.
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    throw new DataConnectionException("Unable to find SQLite library.", e);
                }

                if (controlDBLayout) {
                    Flyway flyway = new Flyway();

                    flyway.setDataSource(dsn, null, null);
                    flyway.setClassLoader(getClass().getClassLoader());
                    flyway.setLocations("migrations/sqlite");

                    try {
                        flyway.migrate();
                    } catch (FlywayException e) {
                        throw new DataConnectionException("Failed to execute migration process.", e);
                    }
                }

                Connection conn;
                try {
                    conn = DriverManager.getConnection(dsn);
                } catch (SQLException e) {
                    throw new DataConnectionException("Failed to connect to the database.", e);
                }

                // the database scheme can be configured by users
                Settings settings = new Settings().withRenderSchema(false);
                DSLContext create = DSL.using(conn, SQLDialect.SQLITE, settings);

                return new JOOQConnection(create, conn, executor);
            }

        });
        return Futures.makeChecked(futureConnection, new Function<Exception, DataConnectionException>() {

            @Override
            public DataConnectionException apply(Exception ex) {
                if (ex instanceof DataConnectionException) {
                    return (DataConnectionException) ex;
                }
                return new DataConnectionException(ex);
            }
        });
    }
}
