package com.vmware.data.services.gemfire.serialization;

import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.security.user.data.UserProfile;
import org.apache.geode.json.JsonDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test for ToJsonFromNonPdxObject
 * @author gregory green
 */
@ExtendWith(MockitoExtension.class)
class ToJsonFromNonPdxObjectTest {

    @Mock
    private JsonDocument jsonDocument;
    private ToJsonFromNonPdxObject subject;
    private UserProfile userProfile = JavaBeanGeneratorCreator.of(UserProfile.class).create();

    @BeforeEach
    void setUp() {
        subject = new ToJsonFromNonPdxObject();
    }

    @DisplayName("Given object is null When convert Then return empty")
    @Test
    void convert_when_null() {
        assertNull(subject.convert(null));
    }


    @DisplayName("Given object when convert then return expect JSON")
    @Test
    void convert() {
        var actual = subject.convert(userProfile);
        assertNotNull(actual);
        assertThat(actual).contains(userProfile.getEmail());
        assertThat(actual).contains(userProfile.getName());
        assertThat(actual).contains(userProfile.getPhone());
        assertThat(actual).contains(userProfile.getTitle());

    }

    @DisplayName("GIVEN jsonDocument WHEN convert THEN return jsonDocument.toJSON")
    @Test
    void convert_jsonDocument() {
        String expected = "{}";
        when(jsonDocument.toJson()).thenReturn(expected);

        var actual = subject.convert(jsonDocument);
        assertEquals(expected, actual);
    }
}