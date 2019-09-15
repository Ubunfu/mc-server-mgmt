package ninja.ryanallen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerStartRequest {
    private String templateName;
    private String subnetId;
    private String region;
}
