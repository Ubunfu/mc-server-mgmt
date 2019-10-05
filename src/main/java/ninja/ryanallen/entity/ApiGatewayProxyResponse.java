package ninja.ryanallen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ApiGatewayProxyResponse {
    private boolean isBase64Encoded;
    private Map<String, String> headers;
    private int statusCode;
    private String body;
}
