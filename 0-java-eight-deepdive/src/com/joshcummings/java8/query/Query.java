package com.joshcummings.java8.query;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import static test.generated.Tables.*;
import static org.jooq.impl.DSL.*;

public class Query {
	public static void main(String[] args) {
		String userName = "root";
        String password = "changeme";
        String url = "jdbc:mysql://localhost:3306/library";

        // Connection is the only JDBC resource that we need
        // PreparedStatement and ResultSet are handled by jOOQ, internally
        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
        	DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
        	create.insertInto(AUTHOR).values(1, "Josh", "Cummings").execute();
        	Result<Record> result = create.select().from(AUTHOR).fetch();
        	result.forEach(System.out::println);
        } 

        // For the sake of this tutorial, let's keep exception handling simple
        catch (Exception e) {
            e.printStackTrace();
        }
	}
}
