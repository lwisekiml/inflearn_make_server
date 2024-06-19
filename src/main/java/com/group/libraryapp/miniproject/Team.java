package com.group.libraryapp.miniproject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    private String name;
    private String manager; // null 가능

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<Employee> employees = new ArrayList<>();

    public Team(GetTeamDTO.RegTeamDTO regTeamDTO) {
        this.name = regTeamDTO.getName();
        this.manager = regTeamDTO.getManager();
    }
}
