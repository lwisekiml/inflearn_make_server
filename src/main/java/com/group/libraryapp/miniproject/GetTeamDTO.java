package com.group.libraryapp.miniproject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GetTeamDTO {
    private String name;
    private String manager;
    private int memberCount;

    public static GetTeamDTO toTeamDTO(Team team) {
        return new GetTeamDTO(
                team.getName(),
                team.getManager(),
                team.getEmployees().size()
        );
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class RegTeamDTO {
        @NotNull
        private String name;
        private String manager;
    }
}
