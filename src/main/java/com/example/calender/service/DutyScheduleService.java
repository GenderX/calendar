package com.example.calender.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DutyScheduleService {

    private List<String> staffList;
    // 设置一个全局的轮班开始日期
    private final LocalDate rotationStartDate = LocalDate.of(2025, 1, 4); // 2025年的第一个周六

    public DutyScheduleService() {
        loadStaffList();
    }

    private void loadStaffList() {
        staffList = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("duty-staff.csv");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            
            // 跳过标题行
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    staffList.add(line.trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            staffList.add("Default Staff");
        }
    }

    public Map<String, Object> generateMonthlySchedule(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        
        Map<String, Object> result = new HashMap<>();
        Map<String, String> dutySchedule = new HashMap<>();
        
        if (staffList.isEmpty()) {
            result.put("dutySchedule", dutySchedule); // 返回空排班表
            return result;
        }

        LocalDate currentDate = firstDayOfMonth;
        while (!currentDate.isAfter(lastDayOfMonth)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if ((dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) && !currentDate.isBefore(rotationStartDate)) {
                long dutyDaysSinceStart = countDutyDays(rotationStartDate, currentDate);
                int staffIndex = (int) (dutyDaysSinceStart % staffList.size());
                dutySchedule.put(currentDate.toString(), staffList.get(staffIndex));
            }
            currentDate = currentDate.plusDays(1);
        }
        
        result.put("year", year);
        result.put("month", month);
        result.put("dutySchedule", dutySchedule);
        result.put("firstDayOfWeek", firstDayOfMonth.getDayOfWeek().getValue());
        result.put("daysInMonth", yearMonth.lengthOfMonth());
        
        return result;
    }

    /**
     * 计算从开始日期到结束日期（含）之间有多少个值班日（周六和周日）
     */
    private long countDutyDays(LocalDate start, LocalDate end) {
        long count = 0;
        LocalDate date = start;
        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                count++;
            }
            date = date.plusDays(1);
        }
        return count - 1; // 减1使索引从0开始
    }
    
    public List<String> getStaffList() {
        return new ArrayList<>(staffList);
    }
}