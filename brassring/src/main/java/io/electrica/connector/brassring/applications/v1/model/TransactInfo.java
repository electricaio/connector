package io.electrica.connector.brassring.applications.v1.model;

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
