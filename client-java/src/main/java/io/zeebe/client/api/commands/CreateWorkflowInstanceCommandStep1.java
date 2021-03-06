/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.client.api.commands;

import java.io.InputStream;
import java.util.Map;

import io.zeebe.client.api.events.WorkflowInstanceEvent;

public interface CreateWorkflowInstanceCommandStep1
{
    /**
     * Use the latest version of the workflow (without guarantee).
     */
    int LATEST_VERSION = -1;

    /**
     * Use the latest version of the workflow (with guarantee).
     */
    int FORCE_LATEST_VERSION = -2;

    /**
     * Set the BPMN process id of the workflow to create an instance of. This is
     * the static id of the process in the BPMN XML (i.e. "&#60;bpmn:process
     * id='my-workflow'&#62;").
     *
     * @param bpmnProcessId
     *            the BPMN process id of the workflow
     * @return the builder for this command
     */
    CreateWorkflowInstanceCommandStep2 bpmnProcessId(String bpmnProcessId);

    /**
     * Set the key of the workflow to create an instance of. The key is assigned
     * by the broker while deploying the workflow. It can be picked from the
     * deployment or workflow event.
     *
     * @param workflowKey
     *            the key of the workflow
     * @return the builder for this command
     */
    CreateWorkflowInstanceCommandStep3 workflowKey(long workflowKey);

    interface CreateWorkflowInstanceCommandStep2
    {
        /**
         * Set the version of the workflow to create an instance of. The version
         * is assigned by the broker while deploying the workflow. It can be
         * picked from the deployment or workflow event.
         *
         * @param version
         *            the version of the workflow
         * @return the builder for this command
         */
        CreateWorkflowInstanceCommandStep3 version(int version);

        /**
         * Use the latest version of the workflow to create an instance of.
         * <p>
         * If the latest version was deployed few moments before then it can
         * happen that the new instance is created of the previous version. Use
         * {@link #latestVersionForce()} to force the latest version in any
         * case.
         *
         * @return the builder for this command
         */
        CreateWorkflowInstanceCommandStep3 latestVersion();

        /**
         * Use the latest version of the workflow to create an instance of.
         * <p>
         * In contrast to {@link #latestVersion()}, it's guaranteed that the new
         * instance is always created of the latest version.
         *
         * @return the builder for this command
         */
        CreateWorkflowInstanceCommandStep3 latestVersionForce();
    }

    interface CreateWorkflowInstanceCommandStep3 extends FinalCommandStep<WorkflowInstanceEvent>
    {
        /**
         * Set the initial payload of the workflow instance.
         *
         * @param payload
         *            the payload (JSON) as stream
         *
         * @return the builder for this command. Call {@link #send()} to
         *         complete the command and send it to the broker.
         */
        CreateWorkflowInstanceCommandStep3 payload(InputStream payload);

        /**
         * Set the initial payload of the workflow instance.
         *
         * @param payload
         *            the payload (JSON) as String
         *
         * @return the builder for this command. Call {@link #send()} to
         *         complete the command and send it to the broker.
         */
        CreateWorkflowInstanceCommandStep3 payload(String payload);

        /**
         * Set the initial payload of the workflow instance.
         *
         * @param payload
         *            the payload as map
         *
         * @return the builder for this command. Call {@link #send()} to
         *         complete the command and send it to the broker.
         */
        CreateWorkflowInstanceCommandStep3 payload(Map<String, Object> payload);

        /**
         * Set the initial payload of the workflow instance.
         *
         * @param payload
         *            the payload as object
         *
         * @return the builder for this command. Call {@link #send()} to
         *         complete the command and send it to the broker.
         */
        CreateWorkflowInstanceCommandStep3 payload(Object payload);
    }

}
