package ninja.ryanallen;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import ninja.ryanallen.entity.ServerTerminateRequest;
import ninja.ryanallen.entity.ServerTerminateResponse;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

/**
 * This is the handler for the function that will stop minecraft servers
 */
public class McServerTerminateHandler implements RequestHandler<ServerTerminateRequest, ServerTerminateResponse> {
    @Override
    public ServerTerminateResponse handleRequest(ServerTerminateRequest stopRequest, Context context) {
        LambdaLogger logger = context.getLogger();

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

        return new ServerTerminateResponse(response.terminatingInstances().get(0).instanceId());
    }
}
