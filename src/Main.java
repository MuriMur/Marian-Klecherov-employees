import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String csvFile = "D:\\sirmaSolutions.csv"; // Path to your CSV file
        List<EmployeeData> employeeDataList = loadFileAndCreateList(csvFile);

        // Calculate the pair of employees who worked together the longest
        Map<String, Long> employeesIdsAndDaysMap = new HashMap<>();
        for (int i = 0; i < employeeDataList.size(); i++) {
            for (int j = i + 1; j < employeeDataList.size(); j++) {
                EmployeeData employeeData1 = employeeDataList.get(i);
                EmployeeData employeeData2 = employeeDataList.get(j);

                if (employeeData1.getProjectId() == employeeData2.getProjectId()) {
                    LocalDate overlapStart;
                    if (employeeData1.getDateFrom().isAfter(employeeData2.getDateFrom())){
                        overlapStart = employeeData1.getDateFrom();
                    } else{
                        overlapStart = employeeData2.getDateFrom();
                    }
                    LocalDate overlapEnd;
                    if (employeeData1.getDateTo().isBefore(employeeData2.getDateTo())){
                        overlapEnd = employeeData1.getDateTo();
                    } else{
                        overlapEnd = employeeData2.getDateTo();
                    }
                    if (overlapStart.isBefore(overlapEnd)) {
                        long daysWorkedTogether = ChronoUnit.DAYS.between(overlapStart, overlapEnd);
                        String key = employeeData1.getEmpId() + ", " + employeeData2.getEmpId();
                        employeesIdsAndDaysMap.put(key, employeesIdsAndDaysMap.getOrDefault(key, 0L) + daysWorkedTogether);
                    }
                }
            }
        }
        long maxDays = 0;
        String idsOfMaxDaysPair = "";
        for (Map.Entry<String, Long> empIdsDays : employeesIdsAndDaysMap.entrySet()) {
            if (maxDays < empIdsDays.getValue()){
                maxDays = empIdsDays.getValue();
                idsOfMaxDaysPair = empIdsDays.getKey();
            }
        }

        System.out.println(idsOfMaxDaysPair + ", " + maxDays);
    }
    private static List<EmployeeData> loadFileAndCreateList(String csvFile) {
        String line = "";
        String splitBy = ",";
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                .toFormatter();
        List<EmployeeData> employeeDataList = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] employee = line.split(splitBy);
                EmployeeData employeeData = new EmployeeData();
                employeeData.setEmpId(Integer.parseInt(employee[0]));
                employeeData.setProjectId(Integer.parseInt(employee[1].replace(" ", "")));
                employeeData.setDateFrom(LocalDate.parse(employee[2].replace(" ", ""),formatter));
                if (employee[3].replace(" ", "").equalsIgnoreCase("null")){
                    employeeData.setDateTo(LocalDate.now());
                }else {
                    employeeData.setDateTo(LocalDate.parse(employee[3].replace(" ", ""), formatter));
                }
                employeeDataList.add(employeeData);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return employeeDataList;
    }
}