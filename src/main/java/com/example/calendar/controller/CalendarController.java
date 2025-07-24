package com.example.calendar.controller;

import com.example.calendar.service.CommentService;
import com.example.calendar.service.DutyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class CalendarController {

    @Autowired
    private DutyScheduleService dutyScheduleService;

    @Autowired
    private CommentService commentService;

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

        if (year == 0 || month == 0) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        Map<String, Object> scheduleData = dutyScheduleService.generateMonthlySchedule(year, month);
        scheduleData.put("comments", commentService.getAllComments());
        return scheduleData;
    }

    @PostMapping("/api/comments")
    public ResponseEntity<?> saveComment(@RequestBody Map<String, String> payload) {
        String date = payload.get("date");
        String comment = payload.get("comment");
        if (date == null || date.isEmpty()) {
            return ResponseEntity.badRequest().body("Date is required.");
        }
        commentService.saveComment(date, comment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public Map<String, Integer> getStatistics(@RequestParam int year) {
        return dutyScheduleService.getDutyStatsYTD(year);
    }
}