// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.json;

import com.arsenal.framework.model.utility.JodaTimeUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * @author Gordon.Gan
 */
public class CstDateTimeSerializer extends StdSerializer<DateTime> {


    public CstDateTimeSerializer(Class<DateTime> t) {
        super(t);
    }

    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(toCstString());

    }

    private String toCstString()  {
         DateTime dateTime = new DateTime();
        return dateTime.withZone(JodaTimeUtils.CST_TIMEZONE).toString(JodaTimeUtils.DEFAULT_UTC_DATE_TIME_FORMAT_OFFSET);
    }
}
