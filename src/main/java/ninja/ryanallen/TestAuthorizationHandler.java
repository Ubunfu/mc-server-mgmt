package ninja.ryanallen;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import ninja.ryanallen.entity.ApiGatewayProxyRequest;
import ninja.ryanallen.entity.ApiGatewayProxyResponse;
import software.amazon.awssdk.http.HttpStatusCode;

public class TestAuthorizationHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest proxyRequest, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log(String.format("Request Data: %s", proxyRequest));

        String authHeader = proxyRequest.getHeaders().get("Authorization");
        return new ApiGatewayProxyResponse(false, HttpStatusCode.OK, String.format("{\"token\":\"%s\"}", authHeader));
    }
}
