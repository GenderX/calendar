package com.example.calender.controller;

import com.example.calender.service.DutyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class CalendarController {

    @Autowired
    private DutyScheduleService dutyScheduleService;

    @GetMapping("/")
    public String index(Model model) {
        LocalDate now = LocalDate.now();
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("currentMonth", now.getMonthValue());
        return "calendar";
    }

    @GetMapping("/api/schedule")
    @ResponseBody
    public Map<String, Object> getSchedule(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        
        // 如果没有提供年月参数，使用当前年月
        if (year == 0 || month == 0) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }
        
        return dutyScheduleService.generateMonthlySchedule(year, month);
    }
}