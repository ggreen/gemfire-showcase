package com.vmware.data.services.gemfire.qa;

import com.vmware.data.services.gemfire.client.GemFireClient;

import static org.assertj.core.api.Assertions.assertThat;

public class GemFireClientQA {
    public static void main(String[] args) {
        var gemfire = GemFireClient.builder()
                .locators("localhost[10334]")
                .clientName("gfQA")
                .build();

        var region = gemfire.getRegion("test");
        region.put("test","test");

        assertThat(region.get("test")).isEqualTo("test");

        System.out.println("****** SUCCESS **************");
    }
}
