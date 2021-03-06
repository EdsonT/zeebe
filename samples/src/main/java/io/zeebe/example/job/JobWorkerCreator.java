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
package io.zeebe.example.job;

import java.time.Duration;
import java.util.Scanner;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.ZeebeClientBuilder;
import io.zeebe.client.api.clients.JobClient;
import io.zeebe.client.api.events.JobEvent;
import io.zeebe.client.api.subscription.JobHandler;
import io.zeebe.client.api.subscription.JobWorker;

public class JobWorkerCreator
{
    public static void main(String[] args)
    {
        final String broker = "127.0.0.1:51015";
        final String topic = "default-topic";

        final String jobType = "foo";

        final ZeebeClientBuilder builder = ZeebeClient.newClientBuilder()
            .brokerContactPoint(broker);

        try (ZeebeClient client = builder.build())
        {
            final JobClient jobClient = client.topicClient(topic).jobClient();

            System.out.println("Opening job worker.");

            final JobWorker workerRegistration = jobClient
                .newWorker()
                .jobType(jobType)
                .handler(new ExampleJobHandler())
                .timeout(Duration.ofSeconds(10))
                .open();

            System.out.println("Job worker opened and receiving jobs.");

            // call workerRegistration.close() to close it

            // run until System.in receives exit command
            waitUntilSystemInput("exit");
        }
    }

    private static class ExampleJobHandler implements JobHandler
    {
        @Override
        public void handle(JobClient client, JobEvent job)
        {
            // here: business logic that is executed with every job
            System.out.println(String.format(
                "[type: %s, key: %s, lockExpirationTime: %s]\n[headers: %s]\n[payload: %s]\n===",
                job.getType(),
                job.getMetadata().getKey(),
                job.getDeadline().toString(),
                job.getHeaders(),
                job.getPayload()));

            client.newCompleteCommand(job).withoutPayload().send().join();
        }
    }

    private static void waitUntilSystemInput(final String exitCode)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            while (scanner.hasNextLine())
            {
                final String nextLine = scanner.nextLine();
                if (nextLine.contains(exitCode))
                {
                    return;
                }
            }
        }
    }
}
