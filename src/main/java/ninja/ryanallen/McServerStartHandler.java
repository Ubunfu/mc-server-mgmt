package ninja.ryanallen;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import ninja.ryanallen.entity.ApiGatewayProxyRequest;
import ninja.ryanallen.entity.ApiGatewayProxyResponse;
import ninja.ryanallen.entity.ServerStartRequest;
import ninja.ryanallen.entity.ServerStartResponse;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateSpecification;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the handler for the function that will start minecraft servers
 */
public class McServerStartHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {
    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest proxyRequest, Context context) {
        LambdaLogger logger = context.getLogger();

        // Convert the Lambda proxy integration request body into a ServerStartRequest
        Gson gson = new Gson();
        ServerStartRequest startRequest = gson.fromJson(proxyRequest.getBody(), ServerStartRequest.class);

        logger.log(String.format("Starting server from template %s, in region %s with subnet %s ...",
                startRequest.getTemplateName(),
                startRequest.getRegion(),
                startRequest.getSubnetId()));

        // Create a new EC2 Client
        Region region = Region.of(startRequest.getRegion());
        Ec2Client client = Ec2Client.builder().region(region).build();

        // Configure the launch template settings
        LaunchTemplateSpecification launchTemplateSpec = LaunchTemplateSpecification.builder()
                .launchTemplateName(startRequest.getTemplateName())
                .build();

        // Create the run request
        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .launchTemplate(launchTemplateSpec)
                .subnetId(startRequest.getSubnetId())
                .minCount(1)
                .maxCount(1)
                .build();

        // Start 'em up!!!
        RunInstancesResponse response = client.runInstances(runRequest);
        logger.log(String.format("Starting EC2 Instance %s ...", response.instances().get(0).instanceId()));

        // Build response payload
        ServerStartResponse resp = new ServerStartResponse(response.instances().get(0).instanceId());

        // Set up the response headers (for CORS really...)
        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Access-Control-Allow-Origin", "https://eager-jang-9f2469.netlify.com");

        // Add that into the "body" of a proper Lambda Proxy Integration response object
        return new ApiGatewayProxyResponse(false, respHeaders, HttpStatusCode.OK, gson.toJson(resp));
    }
}
