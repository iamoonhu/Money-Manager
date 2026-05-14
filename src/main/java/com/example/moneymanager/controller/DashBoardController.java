package com.example.moneymanager.controller;

import com.example.moneymanager.service.DashboardSerive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashBoardController {

    @Autowired
    private final DashboardSerive dashboardSerive;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashBoardData(){
        Map<String, Object> dashboarddata= dashboardSerive.getDashBoarddata();
        return  ResponseEntity.ok(dashboarddata);
    }
}
