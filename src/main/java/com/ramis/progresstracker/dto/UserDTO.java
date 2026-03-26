package com.ramis.progresstracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private Integer level;
    private Integer totalXP;
    private Double motivationScore;
    private Double progressToNextLevel;
    private Integer xpToNextLevel;
}
