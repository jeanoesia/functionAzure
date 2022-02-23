package org.example.functions;

import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpStatus;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Optional;
import java.sql.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerFunction {

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */

    @FunctionName("HttpExample")
    public HttpResponseMessage run(

            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
                final ExecutionContext context) {
                context.getLogger().info("Java HTTP trigger processed a request.");

                 // Parse query parameter
                final String query = request.getQueryParameters().get("name");
                final String name = request.getBody().orElse(query);

                if (name == null)
                     return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
                else {
                    insertDataSQL_Azure(name);
                    return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
                }
            }

        public void insertDataSQL_Azure(String name){
              final Logger LOGGER =
                    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
                    String connectionString = "jdbc:sqlserver://test55-sql-server.database.windows.net:1433;database=test55;user=usertest;password=admin01@;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
                    try {
                        Connection conn = DriverManager.getConnection(connectionString);
                        Statement statement = conn.createStatement();
                        ResultSet resultSet = statement.executeQuery("INSERT INTO [dbo].[Persona] (Nombre) values ('"+name+"');");
                        LOGGER.log(Level.INFO,"CORRECT",resultSet);
                    } catch (SQLException e) {
                             LOGGER.log(Level.WARNING,e.toString());
                             e.printStackTrace();
                    }
        }
}
