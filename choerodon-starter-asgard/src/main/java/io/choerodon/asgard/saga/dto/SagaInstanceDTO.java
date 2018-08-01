package io.choerodon.asgard.saga.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SagaInstanceDTO {

    private Long id;

    private String sagaCode;

    private String status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Long inputDataId;

    private Long outputDataId;

    private String refType;

    private String refId;
}
