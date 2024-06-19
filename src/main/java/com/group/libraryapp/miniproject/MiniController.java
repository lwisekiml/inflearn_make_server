package com.group.libraryapp.miniproject;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MiniController {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/team")
    public List<GetTeamDTO> team() {
        return teamRepository.findAll().stream().map(GetTeamDTO::toTeamDTO).collect(Collectors.toList());
    }

    @PostMapping("/team")
    public void addTeam(@RequestBody GetTeamDTO.RegTeamDTO regTeamDTO) {
        teamRepository.save(new Team(regTeamDTO));
    }

    @GetMapping("/employee")
    public List<GetEmployeeDTO> employee() {
        return employeeRepository.findAll().stream().map(GetEmployeeDTO::toEmployeeDTO).collect(Collectors.toList());
    }

    @PostMapping("/employee")
    public void addEmployee(@RequestBody GetEmployeeDTO.RegEmployeeDTO regEmployeeDTO) {
        Team findTeam = teamRepository.findByName(regEmployeeDTO.getTeamName());
        employeeRepository.save(new Employee(regEmployeeDTO, findTeam));
    }
}

