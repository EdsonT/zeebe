package org.camunda.tngp.broker.util.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.agrona.concurrent.UnsafeBuffer;
import org.camunda.tngp.graph.bpmn.ExecutionEventType;

public class WfRuntimeEvents
{
    public static final int FLOW_ELEMENT_ID = 1235;
    public static final long KEY = 23456789L;
    public static final long PROCESS_ID = 8888L;
    public static final long PROCESS_INSTANCE_ID = 9999L;
    public static final int TASK_QUEUE_ID = 3;
    public static final int WF_RUNTIME_LOG_ID = 4;

    public static final byte[] TASK_TYPE = "foo".getBytes(StandardCharsets.UTF_8);
    public static final byte[] FLOW_ELEMENT_ID_STRING = "bar".getBytes(StandardCharsets.UTF_8);

    public static BpmnActivityEventWriter createActivityInstanceEvent(ExecutionEventType event)
    {
        final BpmnActivityEventWriter activityEventWriter = new BpmnActivityEventWriter();

        activityEventWriter.eventType(event);
        activityEventWriter.flowElementId(FLOW_ELEMENT_ID);
        activityEventWriter.key(KEY);
        activityEventWriter.wfDefinitionId(PROCESS_ID);
        activityEventWriter.wfInstanceId(PROCESS_INSTANCE_ID);

        activityEventWriter.taskQueueId(TASK_QUEUE_ID);
        activityEventWriter.taskType(new UnsafeBuffer(TASK_TYPE), 0, TASK_TYPE.length);

        activityEventWriter.flowElementIdString(new UnsafeBuffer(FLOW_ELEMENT_ID_STRING), 0, FLOW_ELEMENT_ID_STRING.length);

        return activityEventWriter;
    }

    public static BpmnFlowElementEventWriter createFlowElementEvent()
    {
        final BpmnFlowElementEventWriter writer = new BpmnFlowElementEventWriter();

        writer.eventType(ExecutionEventType.EVT_OCCURRED);
        writer.flowElementId(FLOW_ELEMENT_ID);
        writer.key(KEY);
        writer.processId(PROCESS_ID);
        writer.workflowInstanceId(PROCESS_INSTANCE_ID);
        writer.flowElementIdString(new UnsafeBuffer(FLOW_ELEMENT_ID_STRING), 0, FLOW_ELEMENT_ID_STRING.length);

        return writer;
    }

    public static BpmnProcessEventWriter createProcessEvent(ExecutionEventType event)
    {
        final BpmnProcessEventWriter writer = new BpmnProcessEventWriter();

        writer.eventWriter(event);
        writer.initialElementId(FLOW_ELEMENT_ID);
        writer.key(PROCESS_INSTANCE_ID);
        writer.processId(PROCESS_ID);
        writer.processInstanceId(PROCESS_INSTANCE_ID);

        return writer;
    }


    public static BpmnFlowElementEventReader mockFlowElementEvent()
    {
        final BpmnFlowElementEventReader reader = mock(BpmnFlowElementEventReader.class);

        when(reader.event()).thenReturn(ExecutionEventType.EVT_OCCURRED);
        when(reader.flowElementId()).thenReturn(FLOW_ELEMENT_ID);
        when(reader.key()).thenReturn(KEY);
        when(reader.wfDefinitionId()).thenReturn(PROCESS_ID);
        when(reader.wfInstanceId()).thenReturn(PROCESS_INSTANCE_ID);
        when(reader.flowElementIdString()).thenReturn(new UnsafeBuffer(FLOW_ELEMENT_ID_STRING));

        return reader;
    }

    public static BpmnProcessEventReader mockProcessEvent()
    {
        final BpmnProcessEventReader reader = new BpmnProcessEventReader();

        when(reader.event()).thenReturn(ExecutionEventType.PROC_INST_COMPLETED);
        when(reader.initialElementId()).thenReturn(FLOW_ELEMENT_ID);
        when(reader.key()).thenReturn(PROCESS_INSTANCE_ID);
        when(reader.processId()).thenReturn(PROCESS_ID);
        when(reader.processInstanceId()).thenReturn(PROCESS_INSTANCE_ID);

        return reader;
    }

    public static BpmnActivityEventReader mockActivityInstanceEvent(ExecutionEventType event)
    {
        final BpmnActivityEventReader reader = mock(BpmnActivityEventReader.class);

        when(reader.event()).thenReturn(event);
        when(reader.flowElementId()).thenReturn(FLOW_ELEMENT_ID);
        when(reader.key()).thenReturn(KEY);
        when(reader.wfDefinitionId()).thenReturn(PROCESS_ID);
        when(reader.wfInstanceId()).thenReturn(PROCESS_INSTANCE_ID);
        when(reader.resourceId()).thenReturn(WF_RUNTIME_LOG_ID);

        when(reader.taskQueueId()).thenReturn(TASK_QUEUE_ID);
        when(reader.getTaskType()).thenReturn(new UnsafeBuffer(TASK_TYPE));
        when(reader.getFlowElementIdString()).thenReturn(new UnsafeBuffer(FLOW_ELEMENT_ID_STRING));
        when(reader.getPayload()).thenReturn(new UnsafeBuffer(0, 0));

        return reader;
    }

    public static BpmnBranchEventWriter bpmnBranchEvent(String payload, long branchKey)
    {
        final BpmnBranchEventWriter writer = new BpmnBranchEventWriter();
        writer.key(branchKey);

        final UnsafeBuffer payloadBuffer = new UnsafeBuffer(payload.getBytes(StandardCharsets.UTF_8));
        writer.materializedPayload(payloadBuffer, 0, payloadBuffer.capacity());

        return writer;
    }
}