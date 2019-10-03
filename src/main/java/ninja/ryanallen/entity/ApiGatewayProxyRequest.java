package ninja.ryanallen.entity;

import lombok.Data;

import java.util.Map;

@Data
public class ApiGatewayProxyRequest {
    private String httpMethod;
    private Map<String, String> headers;
    private Map<String, String> queryStringParameters;
    private Map<String, String> pathParameters;
//    private Context context;
    private String body;

}
