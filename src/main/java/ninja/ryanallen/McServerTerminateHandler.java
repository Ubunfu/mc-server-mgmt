package ninja.ryanallen;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import ninja.ryanallen.entity.ApiGatewayProxyRequest;
import ninja.ryanallen.entity.ApiGatewayProxyResponse;
import ninja.ryanallen.entity.ServerTerminateRequest;
import ninja.ryanallen.entity.ServerTerminateResponse;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the handler for the function that will stop minecraft servers
 */
public class McServerTerminateHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {
    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest proxyRequest, Context context) {
        LambdaLogger logger = context.getLogger();

        // Convert the Lambda proxy integration request body into a ServerTerminateRequest
        Gson gson = new Gson();
        ServerTerminateRequest stopRequest = gson.fromJson(proxyRequest.getBody(), ServerTerminateRequest.class);

        logger.log(String.format("Terminating server %s ...",
                stopRequest.getInstanceId()));

        // Create a new EC2 Client
        Ec2Client client = Ec2Client.create();

        // Create the termination request
        TerminateInstancesRequest termRequest = TerminateInstancesRequest.builder()
                .instanceIds(stopRequest.getInstanceId())
                .build();

        // Shut 'em down!!!
        TerminateInstancesResponse response = client.terminateInstances(termRequest);

        // Build a new response entity
        ServerTerminateResponse resp = new ServerTerminateResponse(response.terminatingInstances().get(0).instanceId());

        // Set up the response headers (for CORS really...)
        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Access-Control-Allow-Origin", "https://eager-jang-9f2469.netlify.com");

        // Add that into the "body" of a proper Lambda Proxy Integration response object
        return new ApiGatewayProxyResponse(false, respHeaders, HttpStatusCode.OK, gson.toJson(resp));
    }
}
