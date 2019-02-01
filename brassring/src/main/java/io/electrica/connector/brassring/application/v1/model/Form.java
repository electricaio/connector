package io.electrica.connector.brassring.application.v1.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@JacksonXmlRootElement(localName = "Form")
public class Form {

    @JacksonXmlProperty(isAttribute = true, localName = "formTypeId")
    private Integer formTypeId;

    @JacksonXmlProperty(isAttribute = true, localName = "formName")
    private String formName;

    @JacksonXmlProperty(isAttribute = true, localName = "action")
    private String action;

    @JacksonXmlProperty(isAttribute = true, localName = "language")
    private String language = "";


    @JacksonXmlProperty(isAttribute = true, localName = "resumeKey")
    private String resumeKey = "";

    @JacksonXmlProperty(isAttribute = true, localName = "autoreq")
    private String autoreq = "";

    @JacksonXmlProperty(isAttribute = true, localName = "FirstName")
    private String firstName = "";

    @JacksonXmlProperty(isAttribute = true, localName = "LastName")
    private String lastName = "";

    @JacksonXmlProperty(isAttribute = true, localName = "email")
    private String email = "";

    @JacksonXmlProperty(isAttribute = true, localName = "homePhone")
    private String homePhone = "";

    @JacksonXmlProperty(isAttribute = true, localName = "formId")
    private String formId = "";

    @JacksonXmlProperty(isAttribute = true, localName = "brUID")
    private String brUID = "";

    @JacksonXmlProperty(isAttribute = true, localName = "OrderID")
    private String orderID = "";


    @JacksonXmlProperty(localName = "FormInput")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Input> fields;

    public static Form of(BrassRingApplicationPayload payload) {
        Form r = new Form();
        if (payload.getFormTypeId() != null) {
            r.setFormTypeId(payload.getFormTypeId());
        }
        if (payload.getFormName() != null) {
            r.setFormName(payload.getFormName());
        }
        if (payload.getAction() != null) {
            r.setAction(payload.getAction().toString());
        }
        if (payload.getLanguage() != null) {
            r.setLanguage(payload.getLanguage());
        }
        if (payload.getResumeKey() != null) {
            r.setResumeKey(String.valueOf(payload.getResumeKey()));
        }
        if (payload.getAutoreq() != null) {
            r.setAutoreq(payload.getAutoreq());
        }
        if (payload.getFirstName() != null) {
            r.setFirstName(payload.getFirstName());
        }
        if (payload.getLastName() != null) {
            r.setLastName(payload.getLastName());
        }
        if (payload.getEmail() != null) {
            r.setEmail(payload.getEmail());
        }
        if (payload.getHomePhone() != null) {
            r.setHomePhone(payload.getHomePhone());
        }
        if (payload.getFormId() != null) {
            r.setFormId(String.valueOf(payload.getFormId()));
        }
        if (payload.getBrUID() != null) {
            r.setBrUID(String.valueOf(payload.getBrUID()));
        }
        if (payload.getOrderID() != null) {
            r.setOrderID(String.valueOf(payload.getOrderID()));
        }
        if (payload.getFields() != null) {
            r.setFields(payload.getFields().stream()
                    .map(Input::of)
                    .collect(Collectors.toList())
            );
        }
        return r;
    }

    @Getter
    @Setter
    public static class Input {

        @JacksonXmlProperty(isAttribute = true, localName = "name")
        private String name = "";

        @JacksonXmlProperty(isAttribute = true, localName = "title")
        private String title = "";

        @JacksonXmlText
        private String value = "";

        private static Input of(FormInput input) {
            Input r = new Input();
            if (input.getId() != null) {
                r.setName(String.valueOf(input.getId()));
            }
            if (input.getName() != null) {
                r.setTitle(input.getName());
            }
            if (input.getValue() != null) {
                r.setValue(input.getValue());
            }
            return r;
        }
    }
}
