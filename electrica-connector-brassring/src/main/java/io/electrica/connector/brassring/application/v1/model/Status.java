package io.electrica.connector.brassring.application.v1.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Status {

    @JacksonXmlProperty(localName = "Code")
    private String code;

    @JacksonXmlProperty(localName = "ShortDescription")
    private String shortDescription;

    @JacksonXmlProperty(localName = "LongDescription")
    private String longDescription;
}
