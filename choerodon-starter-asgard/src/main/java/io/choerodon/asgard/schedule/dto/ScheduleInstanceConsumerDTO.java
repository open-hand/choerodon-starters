package io.choerodon.asgard.schedule.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleInstanceConsumerDTO {

    private Long id;

    private String method;

    private String executeParams;

    private String instanceLock;

    private Long objectVersionNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleInstanceConsumerDTO that = (ScheduleInstanceConsumerDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

}
