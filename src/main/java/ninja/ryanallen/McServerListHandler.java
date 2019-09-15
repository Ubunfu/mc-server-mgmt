package ninja.ryanallen;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import ninja.ryanallen.entity.ServerInstance;
import ninja.ryanallen.entity.ServerListRequest;
import ninja.ryanallen.entity.ServerListResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;

import java.util.ArrayList;
import java.util.List;

public class McServerListHandler implements RequestHandler<ServerListRequest, ServerListResponse> {
    @Override
    public ServerListResponse handleRequest(ServerListRequest serverListRequest, Context context) {

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

        return new ServerListResponse(serverList);
    }
}
