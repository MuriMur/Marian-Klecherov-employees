import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class EmployeeDataUI {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Employee Project Tracker");
        jFrame.setSize(1000, 800);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Employee Id", "Employee Id", "Project Id", "Days"};
        JTable jTable = new JTable(new Object[][]{}, columnNames);
        jPanel.add(new JScrollPane(jTable), BorderLayout.CENTER);

        JButton selectCsvFile = new JButton("Select CSV File");
        jPanel.add(selectCsvFile, BorderLayout.NORTH);
        selectCsvFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                int result = jFileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jFileChooser.getSelectedFile();
                    List<EmployeeData> employeeDataList = loadFileAndCreateList(selectedFile.getAbsolutePath());
                    List<Object[]> tableData = findAndFillTableData(employeeDataList);
                    fillTableWithData(jTable, tableData);
                }
            }
        });

        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }
    private static List<EmployeeData> loadFileAndCreateList(String csvFile) {
        List<EmployeeData> employeeDataList = new ArrayList<>();
        String line = "";
        String splitBy = ",";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd][MM/dd/yyyy]");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);
                EmployeeData employee = new EmployeeData();
                employee.setEmpId(Integer.parseInt(data[0].trim()));
                employee.setProjectId(Integer.parseInt(data[1].trim()));
                employee.setDateFrom(LocalDate.parse(data[2].trim(), formatter));
                if (data[3].trim().equalsIgnoreCase("null")) {
                    employee.setDateTo(LocalDate.now());
                } else {
                    employee.setDateTo(LocalDate.parse(data[3].trim(), formatter));
                }
                employeeDataList.add(employee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeDataList;
    }

    private static List<Object[]> findAndFillTableData(List<EmployeeData> employeeDataList) {
        List<Object[]> tableData = new ArrayList<>();
        for (int i = 0; i < employeeDataList.size(); i++) {
            for (int j = i + 1; j < employeeDataList.size(); j++) {
                EmployeeData employeeData1 = employeeDataList.get(i);
                EmployeeData employeeData2 = employeeDataList.get(j);
                if (employeeData1.getProjectId() == employeeData2.getProjectId()) {
                    LocalDate overlapStart;
                    if (employeeData1.getDateFrom().isAfter(employeeData2.getDateFrom())) {
                        overlapStart = employeeData1.getDateFrom();
                    } else {
                        overlapStart = employeeData2.getDateFrom();
                    }
                    LocalDate overlapEnd;
                    if (employeeData1.getDateTo().isBefore(employeeData2.getDateTo())) {
                        overlapEnd = employeeData1.getDateTo();
                    } else {
                        overlapEnd = employeeData2.getDateTo();
                    }
                    if (!overlapStart.isAfter(overlapEnd)) {
                        long daysWorkedTogether = ChronoUnit.DAYS.between(overlapStart, overlapEnd);
                        tableData.add(new Object[]{employeeData1.getEmpId(), employeeData2.getEmpId(),
                                employeeData1.getProjectId(), daysWorkedTogether});
                    }
                }
            }
        }
        return tableData;
    }

    private static void fillTableWithData(JTable table, List<Object[]> tableData) {
        String[] columnNames = {"Employee Id", "Employee Id", "Project Id", "Days"};
        Object[][] dataArray = new Object[tableData.size()][columnNames.length];
        for (int i = 0; i < tableData.size(); i++) {
            dataArray[i] = tableData.get(i);
        }
        table.setModel(new javax.swing.table.DefaultTableModel(dataArray, columnNames));
    }
}