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
package io.zeebe.client.api.record;

import java.time.Instant;
import java.util.Map;

public interface JobRecord extends Record
{

    /**
     * @return the type of the job
     */
    String getType();

    /**
     * @return broker-defined headers associated with this job. For example, if
     *         this job is created in the context of workflow instance, the
     *         header provide context information on which activity is executed,
     *         etc.
     */
    Map<String, Object> getHeaders();

    /**
     * @return user-defined headers associated with this job
     */
    Map<String, Object> getCustomHeaders();

    /**
     * @return the assigned worker to complete the job
     */
    String getWorker();

    /**
     * @return remaining retries
     */
    Integer getRetries();

    /**
     * @return the time until when the job is exclusively assigned to this
     *         worker. If the deadline is exceeded, it can happen that the job
     *         is handed to another worker and the work is performed twice.
     */
    Instant getDeadline();

    /**
     * @return JSON-formatted payload
     */
    String getPayload();

    /**
     * @return de-serialized payload as map
     */
    Map<String, Object> getPayloadAsMap();

    /**
     * @return de-serialized payload as the given type
     */
    <T> T getPayloadAsType(Class<T> payloadType);
}
