package co.com.loan.r2dbc.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PostgresqlConnectionPropertiesTest {

  @Test
  void testRecordProperties() {
    PostgresqlConnectionProperties props = new PostgresqlConnectionProperties("localhost",
        5432,
        "mydb",
        "public",
        "user",
        "pass");

    assertEquals("localhost", props.host());
    assertEquals(5432, props.port());
    assertEquals("mydb", props.database());
    assertEquals("public", props.schema());
    assertEquals("user", props.username());
    assertEquals("pass", props.password());
  }
}
