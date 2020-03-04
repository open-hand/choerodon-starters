package org.gitlab4j.api.models;

import java.util.List;

import org.gitlab4j.api.utils.JacksonJson;

public class ProtectedTag {

    public static class CreateAccessLevel {

        private AccessLevel accessLevel;
        private String accessLevelDescription;

        public AccessLevel getAccessLevel() {
            return accessLevel;
        }

        public void setAccessLevel(AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
        }

        public String getAccessLevelDescription() {
            return accessLevelDescription;
        }

        public void setAccessLevelDescription(String accessLevelDescription) {
            this.accessLevelDescription = accessLevelDescription;
        }
    }

    private String name;
    private List<CreateAccessLevel> createAccessLevels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CreateAccessLevel> getCreateAccessLevels() {
        return createAccessLevels;
    }

    public void setCreateAccessLevels(List<CreateAccessLevel> createAccessLevels) {
        this.createAccessLevels = createAccessLevels;
    }

    @Override
    public String toString() {
        return (JacksonJson.toJsonString(this));
    }
}
