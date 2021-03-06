/*
 * Zeebe Broker Core
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.zeebe.broker.system.workflow.repository.processor;

import io.zeebe.broker.logstreams.processor.*;
import io.zeebe.broker.system.workflow.repository.data.DeployedWorkflow;
import io.zeebe.broker.system.workflow.repository.data.DeploymentRecord;
import io.zeebe.broker.system.workflow.repository.processor.state.WorkflowRepositoryIndex;
import io.zeebe.broker.system.workflow.repository.processor.state.WorkflowRepositoryIndex.WorkflowMetadata;
import io.zeebe.msgpack.value.ValueArray;
import io.zeebe.util.buffer.BufferUtil;

public class DeploymentCreatedEventProcessor implements TypedRecordProcessor<DeploymentRecord>
{
    private WorkflowRepositoryIndex repositoryIndex;

    public DeploymentCreatedEventProcessor(WorkflowRepositoryIndex repositoryIndex)
    {
        this.repositoryIndex = repositoryIndex;
    }

    @Override
    public boolean executeSideEffects(TypedRecord<DeploymentRecord> event, TypedResponseWriter responseWriter)
    {
        return responseWriter.writeEventUnchanged(event);
    }

    @Override
    public void updateState(TypedRecord<DeploymentRecord> event)
    {
        final DeploymentRecord deploymentEvent = event.getValue();

        final ValueArray<DeployedWorkflow> deployedWorkflows = deploymentEvent.deployedWorkflows();

        final String topicName = BufferUtil.bufferAsString(deploymentEvent.getTopicName());

        for (final DeployedWorkflow deployedWorkflow : deployedWorkflows)
        {
            final WorkflowMetadata workflowMetadata = new WorkflowMetadata().setKey(deployedWorkflow.getKey())
                    .setBpmnProcessId(BufferUtil.bufferAsString(deployedWorkflow.getBpmnProcessId()))
                    .setEventPosition(event.getPosition())
                    .setVersion(deployedWorkflow.getVersion())
                    .setResourceName(BufferUtil.bufferAsString(deployedWorkflow.getResourceName()))
                    .setTopicName(topicName);

            repositoryIndex.add(workflowMetadata);
        }
    }
}
