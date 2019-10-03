package ninja.ryanallen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import software.amazon.awssdk.http.HttpStatusCode;

@Data
@AllArgsConstructor
public class ApiGatewayProxyResponse {
    private boolean isBase64Encoded;
    private int statusCode;
    private String body;
}
