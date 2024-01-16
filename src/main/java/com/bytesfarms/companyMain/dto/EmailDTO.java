package com.bytesfarms.companyMain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailDTO {

    private String from;
    private String to;
    private List<String> toList;
    private String message;
    private String subject;

}