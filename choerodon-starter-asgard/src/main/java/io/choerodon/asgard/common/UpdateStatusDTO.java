package io.choerodon.asgard.common;


public class UpdateStatusDTO {

    private Long id;

    private String status;

    private String output;

    private String exceptionMessage;

    private Long objectVersionNumber;

    public UpdateStatusDTO(Long id, String status, String output, String exceptionMessage, Long objectVersionNumber) {
        this.id = id;
        this.status = status;
        this.output = output;
        this.exceptionMessage = exceptionMessage;
        this.objectVersionNumber = objectVersionNumber;
    }

    public UpdateStatusDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public static final class UpdateStatusDTOBuilder {
        private Long id;
        private String status;
        private String output;
        private String exceptionMessage;
        private Long objectVersionNumber;

        private UpdateStatusDTOBuilder() {
        }

        public static UpdateStatusDTOBuilder newInstance() {
            return new UpdateStatusDTOBuilder();
        }

        public UpdateStatusDTOBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UpdateStatusDTOBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public UpdateStatusDTOBuilder withOutput(String output) {
            this.output = output;
            return this;
        }

        public UpdateStatusDTOBuilder withExceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
            return this;
        }

        public UpdateStatusDTOBuilder withObjectVersionNumber(Long objectVersionNumber) {
            this.objectVersionNumber = objectVersionNumber;
            return this;
        }

        public UpdateStatusDTO build() {
            UpdateStatusDTO updateStatusDTO = new UpdateStatusDTO();
            updateStatusDTO.setId(id);
            updateStatusDTO.setStatus(status);
            updateStatusDTO.setOutput(output);
            updateStatusDTO.setExceptionMessage(exceptionMessage);
            updateStatusDTO.setObjectVersionNumber(objectVersionNumber);
            return updateStatusDTO;
        }

    }
}
