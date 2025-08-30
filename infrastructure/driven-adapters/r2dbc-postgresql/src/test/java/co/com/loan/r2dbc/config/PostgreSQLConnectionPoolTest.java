package co.com.loan.r2dbc.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostgreSQLConnectionPoolTest {

  @InjectMocks
  private PostgreSQLConnectionPool connectionPool;

  @Mock
  private PostgresqlConnectionProperties properties;


  @BeforeEach
  void setUp() {

    when(properties.host()).thenReturn("localhost");
    when(properties.port()).thenReturn(5432);
    when(properties.database()).thenReturn("dbName");
    when(properties.schema()).thenReturn("schema");
    when(properties.username()).thenReturn("username");
    when(properties.password()).thenReturn("password");
  }

  @Test
  void getConnectionConfigSuccess() {
    assertNotNull(connectionPool.getConnectionConfig(properties));
  }
}
