package com.microsoft.azure.eventhubs.samples.eventprocessorsample;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.ExceptionReceivedEventArgs;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class EventProcessorSample
{
    public static void main(String args[]) throws InterruptedException, ExecutionException
    {
    	// doc url
		// https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-java-get-started-send
    	   String consumerGroupName = "testconsumergroup";
 	   String namespaceName = "moary-event-hubs-namespace";
 	   String eventHubName = "multi-partition-event-hub";
 	   String sasKeyName = "RootManageSharedAccessKey";
 	   String sasKey = "PQJQ2haIjsj5od0khMOpel4absyr+wMoEjTbfSjzWIw=";
 	   String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=moarystorage;AccountKey=FHBNPmWQb8UCHdTZAmM6G30/392Dvk7m1ikz54z3vlMtjA70VoAFLSgO7uYVQ43kgruPh9zDGI0s13SB64ySdw==;EndpointSuffix=core.windows.net";
 	   String storageContainerName = "moary-storage-container";
 	   String hostNamePrefix = "moary";

 	   ConnectionStringBuilder eventHubConnectionString = new ConnectionStringBuilder()
 			.setNamespaceName(namespaceName)
 			.setEventHubName(eventHubName)
 			.setSasKeyName(sasKeyName)
 			.setSasKey(sasKey);

 	   EventProcessorHost host = new EventProcessorHost(
 			EventProcessorHost.createHostName(hostNamePrefix),
 			eventHubName,
 			consumerGroupName,
 			eventHubConnectionString.toString(),
 			storageConnectionString,
 			storageContainerName);

 	   System.out.println("Registering host named " + host.getHostName());
 	   EventProcessorOptions options = new EventProcessorOptions();
 	   options.setExceptionNotification(new ErrorNotificationHandler());

 	   host.registerEventProcessor(EventProcessor.class, options)
 	   .whenComplete((unused, e) ->
 	   {
 		   if (e != null)
 		   {
 			   System.out.println("Failure while registering: " + e.toString());
 			   if (e.getCause() != null)
 			   {
 				   System.out.println("Inner exception: " + e.getCause().toString());
 			   }
 		   }
 	   })
 	   .thenAccept((unused) ->
 	   {
 		   System.out.println("Press enter to stop.");
 	  	   try 
 		   {
 			   System.in.read();
 		   }
 		   catch (Exception e)
 		   {
 			   System.out.println("Keyboard read failed: " + e.toString());
 		   }
 	   })
 	   .thenCompose((unused) ->
  	   {
 	 	   return host.unregisterEventProcessor();
 	   })
 	   .exceptionally((e) ->
 	   {
 		   System.out.println("Failure while unregistering: " + e.toString());
 		   if (e.getCause() != null)
 		   {
 			   System.out.println("Inner exception: " + e.getCause().toString());
 		   }
 		   return null;
 	   })
 	   .get(); // Wait for everything to finish before exiting main!

        System.out.println("End of sample");
    }

	public static class EventProcessor implements IEventProcessor
	{
		private int checkpointBatchingCount = 0;

		// OnOpen is called when a new event processor instance is created by the host.
		@Override
		public void onOpen(PartitionContext context) throws Exception
		{
			System.out.println("SAMPLE: Partition " + context.getPartitionId() + " is opening");
		}

		// OnClose is called when an event processor instance is being shut down.
		@Override
		public void onClose(PartitionContext context, CloseReason reason) throws Exception
		{
			System.out.println("SAMPLE: Partition " + context.getPartitionId() + " is closing for reason " + reason.toString());
		}

		// onError is called when an error occurs in EventProcessorHost code that is tied to this partition, such as a receiver failure.
		@Override
		public void onError(PartitionContext context, Throwable error)
		{
			System.out.println("SAMPLE: Partition " + context.getPartitionId() + " onError: " + error.toString());
		}

		// onEvents is called when events are received on this partition of the Event Hub.
		@Override
		public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception
		{
			System.out.println("SAMPLE: Partition " + context.getPartitionId() + " got event batch");
			int eventCount = 0;
			for (EventData data : events)
			{
				try
				{
					System.out.println("SAMPLE (" + context.getPartitionId() + "," + data.getSystemProperties().getOffset() + "," +
							data.getSystemProperties().getSequenceNumber() + "): " + new String(data.getBytes(), "UTF8"));
					eventCount++;

					// Checkpointing persists the current position in the event stream for this partition and means that the next
					// time any host opens an event processor on this event hub+consumer group+partition combination, it will start
					// receiving at the event after this one.
					this.checkpointBatchingCount++;
					if ((checkpointBatchingCount % 5) == 0)
					{
						System.out.println("SAMPLE: Partition " + context.getPartitionId() + " checkpointing at " +
								data.getSystemProperties().getOffset() + "," + data.getSystemProperties().getSequenceNumber());
						// Checkpoints are created asynchronously. It is important to wait for the result of checkpointing
						// before exiting onEvents or before creating the next checkpoint, to detect errors and to ensure proper ordering.
						context.checkpoint(data).get();
					}
				}
				catch (Exception e)
				{
					System.out.println("Processing failed for an event: " + e.toString());
				}
			}
			System.out.println("SAMPLE: Partition " + context.getPartitionId() + " batch size was " + eventCount + " for host " + context.getOwner());
		}
	}
}

