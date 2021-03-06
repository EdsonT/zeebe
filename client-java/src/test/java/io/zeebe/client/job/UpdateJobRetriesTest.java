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
package io.zeebe.client.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import io.zeebe.client.api.events.JobEvent;
import io.zeebe.client.api.events.JobState;
import io.zeebe.client.cmd.ClientCommandRejectedException;
import io.zeebe.client.impl.event.JobEventImpl;
import io.zeebe.client.util.ClientRule;
import io.zeebe.client.util.Events;
import io.zeebe.protocol.clientapi.ValueType;
import io.zeebe.test.broker.protocol.brokerapi.ExecuteCommandRequest;
import io.zeebe.test.broker.protocol.brokerapi.StubBrokerRule;

public class UpdateJobRetriesTest
{
    public ClientRule clientRule = new ClientRule();
    public StubBrokerRule brokerRule = new StubBrokerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(brokerRule).around(clientRule);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldUpdateRetries()
    {
        // given
        final JobEventImpl baseEvent = Events.exampleJob();
        baseEvent.setPosition(2L);
        baseEvent.setSourceRecordPosition(1L);

        brokerRule.jobs().registerUpdateRetriesCommand(3L);

        // when
        final JobEvent jobEvent = clientRule.jobClient()
            .newUpdateRetriesCommand(baseEvent)
            .retries(4)
            .send()
            .join();

        // then
        final ExecuteCommandRequest request = brokerRule.getReceivedCommandRequests().get(0);
        assertThat(request.valueType()).isEqualTo(ValueType.JOB);
        assertThat(request.partitionId()).isEqualTo(StubBrokerRule.TEST_PARTITION_ID);
        assertThat(request.sourceRecordPosition()).isEqualTo(1L);

        assertThat(request.getCommand()).containsOnly(
                entry("deadline", baseEvent.getDeadline().toEpochMilli()),
                entry("worker", baseEvent.getWorker()),
                entry("retries", 4),
                entry("type", baseEvent.getType()),
                entry("headers", baseEvent.getHeaders()),
                entry("customHeaders", baseEvent.getCustomHeaders()),
                entry("payload", baseEvent.getPayloadField().getMsgPack()));

        assertThat(jobEvent.getMetadata().getKey()).isEqualTo(baseEvent.getKey());
        assertThat(jobEvent.getMetadata().getTopicName()).isEqualTo(StubBrokerRule.TEST_TOPIC_NAME);
        assertThat(jobEvent.getMetadata().getPartitionId()).isEqualTo(StubBrokerRule.TEST_PARTITION_ID);
        assertThat(jobEvent.getMetadata().getSourceRecordPosition()).isEqualTo(3L);
        assertThat(jobEvent.getSourceRecordPosition()).isEqualTo(3L);

        assertThat(jobEvent.getState()).isEqualTo(JobState.RETRIES_UPDATED);
        assertThat(jobEvent.getHeaders()).isEqualTo(baseEvent.getHeaders());
        assertThat(jobEvent.getDeadline()).isEqualTo(baseEvent.getDeadline());
        assertThat(jobEvent.getWorker()).isEqualTo(baseEvent.getWorker());
        assertThat(jobEvent.getType()).isEqualTo(baseEvent.getType());
        assertThat(jobEvent.getPayload()).isEqualTo(baseEvent.getPayload());
        assertThat(jobEvent.getRetries()).isEqualTo(4);

    }

    @Test
    public void shouldThrowExceptionOnRejection()
    {
        // given
        final JobEventImpl baseEvent = Events.exampleJob();

        brokerRule.jobs().registerUpdateRetriesCommand(b -> b.rejection());

        // then
        exception.expect(ClientCommandRejectedException.class);
        exception.expectMessage("Command (UPDATE_RETRIES) for event with key 79 was rejected");

        // when
        clientRule.jobClient()
            .newUpdateRetriesCommand(baseEvent)
            .retries(4)
            .send()
            .join();
    }

    @Test
    public void shouldThrowExceptionIfBaseEventIsNull()
    {
        // then
        exception.expect(RuntimeException.class);
        exception.expectMessage("base event must not be null");

        // when
        clientRule.jobClient()
            .newUpdateRetriesCommand(null)
            .retries(4)
            .send()
            .join();
    }

}
