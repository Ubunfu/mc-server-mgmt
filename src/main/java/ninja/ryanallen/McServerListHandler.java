package ninja.ryanallen;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import ninja.ryanallen.entity.*;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McServerListHandler implements RequestHandler<ApiGatewayProxyRequest, ApiGatewayProxyResponse> {
    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayProxyRequest proxyRequest, Context context) {

        // Convert the Lambda proxy integration request body into a ServerListRequest
        Gson gson = new Gson();
        ServerListRequest serverListRequest = gson.fromJson(proxyRequest.getBody(), ServerListRequest.class);

        Ec2Client ec2Client = Ec2Client.builder()
                .region(Region.of(serverListRequest.getRegion()))
                .build();

        // Get all instances that are tagged "mc-server"
        DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder()
                .filters(
                    Filter.builder()
                            .name("tag:name")
                            .values("mc-server")
                            .build())
                .build();

        // Make DescribeInstances API Call to EC2
        DescribeInstancesResponse response = ec2Client.describeInstances(describeRequest);

        // Build a response object with the handful of desired metadata
        List<ServerInstance> serverList = new ArrayList<>();
        response.reservations().forEach(reservation -> reservation
                .instances().forEach(instance -> {
                    serverList.add(new ServerInstance(
                            instance.instanceId(),
                            instance.state().nameAsString(),
                            instance.publicDnsName()));
        }));

        // Build a new response entity
        ServerListResponse resp = new ServerListResponse(serverList);

        // Set up the response headers (for CORS really...)
        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Access-Control-Allow-Origin", "https://eager-jang-9f2469.netlify.com");

        // Add that into the "body" of a proper Lambda Proxy Integration response object
        return new ApiGatewayProxyResponse(false, respHeaders, HttpStatusCode.OK, gson.toJson(resp));
    }
}
