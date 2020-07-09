package com.example;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.PartitionProperties;

/**
 * Demonstrates how to fetch metadata from an Event Hub's partitions using synchronous client.
 */
public class GetEventHubMetadata {
    /**
     * Demonstrates how to get metadata from an Event Hub's partitions.
     * ref:
     * https://github.com/Azure/azure-sdk-for-java/issues/12900
     * https://github.com/Azure/azure-sdk-for-java/blob/master/sdk/eventhubs/azure-messaging-eventhubs/src/samples/java/com/azure/messaging/eventhubs/GetEventHubMetadata.java
     * @param args Unused arguments to the sample.
     */
    public static void main(String[] args) {
        // The connection string value can be obtained by:
        // 1. Going to your Event Hubs namespace in Azure Portal.
        // 2. Creating an Event Hub instance.
        // 3. Creating a "Shared access policy" for your Event Hub instance.
        // 4. Copying the connection string from the policy's properties.
        String connectionString = "";

        // Instantiate a client that will be used to call the service. Using a try-resource block, so it disposes of
        // the client when we are done.
        EventHubProducerClient client = new EventHubClientBuilder()
            .connectionString(connectionString)
            .buildProducerClient();

        // Querying the partition identifiers for the Event Hub. Then calling client.getPartitionProperties with the
        // identifier to get information about each partition.
        for (String partitionId : client.getPartitionIds()) {
            PartitionProperties properties = client.getPartitionProperties(partitionId);
            System.out.printf(
                "Event Hub Name: %s; Partition Id: %s; Is partition empty? %s; First Sequence Number: %s; "
                    + "Last Enqueued Time: %s; Last Enqueued Sequence Number: %s; Last Enqueued Offset: %s%n",
                properties.getEventHubName(),
                properties.getId(),
                properties.isEmpty(),
                properties.getBeginningSequenceNumber(),
                properties.getLastEnqueuedTime(),
                properties.getLastEnqueuedSequenceNumber(),
                properties.getLastEnqueuedOffset());
        }

        // Dispose of the client.
        client.close();
    }
}