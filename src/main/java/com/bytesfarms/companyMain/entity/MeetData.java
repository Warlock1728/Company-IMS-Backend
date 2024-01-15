package com.bytesfarms.companyMain.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class MeetData {
    private String startDateTime;
    private String topic;
    private String agenda;
    private String duration;
    private String candidate;
    
    
    
    
    private List<String> listOfAttendee;
}
