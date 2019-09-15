package ninja.ryanallen;

import ninja.ryanallen.entity.ServerInstance;
import ninja.ryanallen.entity.ServerListResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Ec2Client ec2Client = Ec2Client.builder()
            .region(Region.US_EAST_2)
            .build();

        // Get all instances that are tagged "mc-server"
        DescribeInstancesRequest describeRequest = DescribeInstancesRequest.builder()
            .filters(
                Filter.builder()
                    .name("tag:name")
                    .values("mc-server")
                    .build())
            .build();

        DescribeInstancesResponse response = ec2Client.describeInstances(describeRequest);

        List<ServerInstance> serverList = new ArrayList<>();
        response.reservations().forEach(reservation -> reservation
                .instances().forEach(instance -> {
                    serverList.add(new ServerInstance(
                            instance.instanceId(),
                            instance.state().nameAsString(),
                            instance.publicDnsName()));
        }));

        System.out.println("response is: " + new ServerListResponse(serverList));
    }
}
