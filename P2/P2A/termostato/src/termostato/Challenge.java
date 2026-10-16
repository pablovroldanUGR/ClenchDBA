import java.util.List;

public record Challenge(
    long id,
    String name,
    String environmentType,
    String agentTypeCode,
    List<String> actionCodes,
    List<String> sensorCodes
) {}