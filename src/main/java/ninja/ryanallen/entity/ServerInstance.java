package ninja.ryanallen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInstance {
    private String instanceId;
    private String instanceState;
    private String publicDnsName;
}
