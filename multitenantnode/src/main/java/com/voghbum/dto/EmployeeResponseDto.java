package com.voghbum.dto;

public class EmployeeResponseDto {
    private Long id;
    private String name;
    private int performance;

    public EmployeeResponseDto() {}

    public EmployeeResponseDto(Long id, String name, int performance) {
        this.id = id;
        this.name = name;
        this.performance = performance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }
} 