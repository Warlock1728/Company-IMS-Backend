package com.bytesfarms.companyMain.entity;

import java.util.List;

import lombok.Data;

@Data
public class MeetData {
    private String startDateTime, endDateTime, summary, description;
    private List<String> listOfAttendee;
}
