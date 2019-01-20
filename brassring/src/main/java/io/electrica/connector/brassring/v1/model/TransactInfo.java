package io.electrica.connector.brassring.v1.model;

import io.electrica.connector.brassring.application.v1.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactInfo {

    private String transactType;
    private String transactId;
    private String timestamp;
    private Status status;
}
