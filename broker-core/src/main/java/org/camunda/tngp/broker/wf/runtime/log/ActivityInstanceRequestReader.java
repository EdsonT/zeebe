package org.camunda.tngp.broker.wf.runtime.log;

import org.camunda.tngp.taskqueue.data.ActivityInstanceRequestDecoder;
import org.camunda.tngp.taskqueue.data.ActivityInstanceRequestType;
import org.camunda.tngp.taskqueue.data.MessageHeaderDecoder;
import org.camunda.tngp.util.buffer.BufferReader;

import uk.co.real_logic.agrona.DirectBuffer;

public class ActivityInstanceRequestReader implements BufferReader
{

    protected MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    protected ActivityInstanceRequestDecoder bodyDecoder = new ActivityInstanceRequestDecoder();

    public long activityInstanceKey()
    {
        return bodyDecoder.key();
    }

    public ActivityInstanceRequestType type()
    {
        return bodyDecoder.type();
    }

    @Override
    public void wrap(DirectBuffer buffer, int offset, int length)
    {
        headerDecoder.wrap(buffer, offset);

        bodyDecoder.wrap(buffer, offset + headerDecoder.encodedLength(), headerDecoder.blockLength(), headerDecoder.version());
    }

}