// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.json;

import com.arsenal.framework.model.utility.JodaUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * @author Gordon.Gan
 */
public class CstDateTimeDeserializer extends StdDeserializer<DateTime> {


    protected CstDateTimeDeserializer(Class<DateTime> vc) {
        super(vc);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
         JsonToken currentToken = jsonParser.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return toCst(jsonParser.getText().trim());
        }
        return null;
    }

   private DateTime toCst(String dateStr)  {

        return DateTime.parse(dateStr).withZone(JodaUtils.CST_TIMEZONE);
    }
}
