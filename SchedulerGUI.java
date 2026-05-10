import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulerGUI extends JFrame {

    // ===== Process List =====
    ArrayList<Process> processes = new ArrayList<>();

    // ===== Input Fields =====
    public JTextField txtProcessID = new JTextField(10);
    public JTextField txtArrivalTime = new JTextField(10);
    public JTextField txtBurstTime = new JTextField(10);
    public JTextField txtQuantum = new JTextField(10);

    // ===== Buttons =====
    public JButton btnAdd = new JButton("Add Process");
    public JButton btnRun = new JButton("Run Simulation");
    public JButton btnClear = new JButton("Clear");
    public JButton btnScenarioA = new JButton("Scenario A");
    public JButton btnScenarioB = new JButton("Scenario B");
    public JButton btnScenarioC = new JButton("Scenario C");
    public JButton btnScenarioD = new JButton("Scenario D");
    public JButton btnScenarioE = new JButton("Scenario E");

    // ===== Tables =====
    public DefaultTableModel processTableModel;
    public JTable processTable;
    public DefaultTableModel rrTableModel;
    public DefaultTableModel srtfTableModel;
    public JTable rrTable;
    public JTable srtfTable;

    // Old names kept so the project structure stays friendly with existing code.
    public DefaultTableModel resultTableModel;
    public JTable resultTable;

    // ===== Text Areas =====
    public JTextArea readyQueueArea = new JTextArea();
    public JTextArea comparisonArea = new JTextArea();
    public JTextArea finalArea = new JTextArea();
    public JLabel statusLabel = new JLabel(" ");

    // ===== Gantt Panels =====
    public GanttPanel ganttRRPanel = new GanttPanel();
    public GanttPanel ganttSRTFPanel = new GanttPanel();

    // ===== Simple Colors =====
    Color blue = new Color(135, 170, 210);
    Color green = new Color(135, 180, 140);
    Color red = new Color(210, 130, 130);
    Color orange = new Color(225, 180, 120);
    Color cyan = new Color(145, 190, 210);
    Color purple = new Color(165, 145, 200);
    Color teal = new Color(125, 175, 170);
    Color pageColor = new Color(248, 250, 252);

    // ===== Constructor =====
    public SchedulerGUI() {

        setTitle("Round Robin vs SRTF Scheduler");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(pageColor);

        add(createInputSection(), BorderLayout.NORTH);
        add(createMiddleSection(), BorderLayout.CENTER);
        add(createBottomSection(), BorderLayout.SOUTH);

        addButtonActions();
    }

    // ===== Connect Buttons =====
    private void addButtonActions() {
        btnAdd.addActionListener(e -> addProcess());
        btnRun.addActionListener(e -> runAlgorithms());
        btnClear.addActionListener(e -> clearAll());

        btnScenarioA.addActionListener(e -> loadScenarioA());
        btnScenarioB.addActionListener(e -> loadScenarioB());
        btnScenarioC.addActionListener(e -> loadScenarioC());
        btnScenarioD.addActionListener(e -> loadScenarioD());
        btnScenarioE.addActionListener(e -> loadScenarioE());
    }

    // ===== Add Process With Simple Validation =====
    private void addProcess() {
        try {
            String pid = txtProcessID.getText().trim();
            int arrival = Integer.parseInt(txtArrivalTime.getText().trim());
            int burst = Integer.parseInt(txtBurstTime.getText().trim());

            if (pid.equals("")) {
                JOptionPane.showMessageDialog(this, "Enter Process ID");
                return;
            }

            if (arrival < 0) {
                JOptionPane.showMessageDialog(this, "Arrival Time must be >= 0");
                return;
            }

            if (burst <= 0) {
                JOptionPane.showMessageDialog(this, "Burst Time must be > 0");
                return;
            }

            // Check duplicate process ID before adding it.
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).getProcessID().equals(pid)) {
                    JOptionPane.showMessageDialog(this, "This Process ID already exists");
                    statusLabel.setText("Process ID already exists. Use another name.");
                    statusLabel.setForeground(red);
                    return;
                }
            }

            addProcessToTable(pid, arrival, burst);
            statusLabel.setText("Process added successfully.");
            statusLabel.setForeground(green);

            txtProcessID.setText("");
            txtArrivalTime.setText("");
            txtBurstTime.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers correctly.");
        }
    }

    // ===== Run Existing Algorithms =====
    private void runAlgorithms() {
        try {
            if (processes.size() == 0) {
                JOptionPane.showMessageDialog(this, "Add processes first");
                return;
            }

            int quantum = Integer.parseInt(txtQuantum.getText().trim());

            if (quantum <= 0) {
                JOptionPane.showMessageDialog(this, "Quantum must be > 0");
                return;
            }

            RoundRobinSchedular rr = new RoundRobinSchedular();
            RoundRobinSchedular.RRFinalResult rrFinal = rr.simulate(processes, quantum);

            SRTFSchedular srtf = new SRTFSchedular();
            List<SRTFResults> srtfResults = srtf.simulate(processes);

            // SRTF class returns table results only, so this is just for GUI drawing.
            List<int[]> srtfGantt = buildSRTFGantt(processes);

            showResults(rrFinal.results, srtfResults, rrFinal.gantt, srtfGantt, quantum);

            statusLabel.setText("Simulation completed successfully.");
            statusLabel.setForeground(green);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please check quantum and process data");
        }
    }

    // ===== Show Results In Tables, Gantt Charts, And Text Areas =====
    private void showResults(List<RoundRobinResult> rrResults,
                             List<SRTFResults> srtfResults,
                             List<int[]> rrGantt,
                             List<int[]> srtfGantt,
                             int quantum) {

        rrTableModel.setRowCount(0);
        srtfTableModel.setRowCount(0);

        for (int i = 0; i < rrResults.size(); i++) {
            RoundRobinResult r = rrResults.get(i);
            rrTableModel.addRow(new Object[] {
                    r.processID, r.arrivalTime, r.burstTime,
                    r.completionTime, r.WT, r.TAT, r.responseTime
            });
        }

        for (int i = 0; i < srtfResults.size(); i++) {
            SRTFResults r = srtfResults.get(i);
            srtfTableModel.addRow(new Object[] {
                    r.processID, r.arrivalTime, r.burstTime,
                    r.completionTime, r.WT, r.TAT, r.resopnseTime
            });
        }

        ganttRRPanel.setGantt(rrGantt, processes);
        ganttSRTFPanel.setGantt(srtfGantt, processes);

        showReadyQueue(rrGantt);
        showComparison(rrResults, srtfResults, quantum);
    }

    // ===== Ready Queue View For RR =====
    private void showReadyQueue(List<int[]> rrGantt) {
        readyQueueArea.setText("");

        for (int i = 0; i < rrGantt.size(); i++) {
            int[] part = rrGantt.get(i);
            int index = part[0];
            int start = part[1];
            int end = part[2];

            String pid = "Idle";
            if (index != -1) {
                pid = processes.get(index).getProcessID();
            }

            readyQueueArea.append("t=" + start + " to " + end + ": " + pid + "\n");
        }
    }

    // ===== Simple SRTF Gantt For Drawing Only =====
    private List<int[]> buildSRTFGantt(ArrayList<Process> list) {
        List<int[]> gantt = new ArrayList<>();
        int n = list.size();
        int[] remaining = new int[n];
        boolean[] done = new boolean[n];

        for (int i = 0; i < n; i++) {
            remaining[i] = list.get(i).getBurstTime();
        }

        int time = 0;
        int finished = 0;
        int last = -2;
        int start = 0;

        while (finished < n) {
            int selected = -1;
            int min = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!done[i] && list.get(i).getArrivalTime() <= time && remaining[i] < min) {
                    min = remaining[i];
                    selected = i;
                }
            }

            if (selected != last) {
                if (last != -2) {
                    gantt.add(new int[] { last, start, time });
                }
                start = time;
                last = selected;
            }

            if (selected == -1) {
                time++;
            } else {
                remaining[selected]--;
                time++;

                if (remaining[selected] == 0) {
                    done[selected] = true;
                    finished++;
                }
            }
        }

        gantt.add(new int[] { last, start, time });
        return gantt;
    }

    // ===== Comparison Text =====
    private void showComparison(List<RoundRobinResult> rrResults,
                                List<SRTFResults> srtfResults,
                                int quantum) {

        double rrWT = 0, rrTAT = 0, rrRT = 0;
        double srtfWT = 0, srtfTAT = 0, srtfRT = 0;

        for (int i = 0; i < rrResults.size(); i++) {
            rrWT += rrResults.get(i).WT;
            rrTAT += rrResults.get(i).TAT;
            rrRT += rrResults.get(i).responseTime;
        }

        for (int i = 0; i < srtfResults.size(); i++) {
            srtfWT += srtfResults.get(i).WT;
            srtfTAT += srtfResults.get(i).TAT;
            srtfRT += srtfResults.get(i).resopnseTime;
        }

        rrWT = rrWT / rrResults.size();
        rrTAT = rrTAT / rrResults.size();
        rrRT = rrRT / rrResults.size();
        srtfWT = srtfWT / srtfResults.size();
        srtfTAT = srtfTAT / srtfResults.size();
        srtfRT = srtfRT / srtfResults.size();

        String text = "Average Waiting Time: RR = " + String.format("%.2f", rrWT)
                + " , SRTF = " + String.format("%.2f", srtfWT) + "\n";
        text += "Average Turnaround Time: RR = " + String.format("%.2f", rrTAT)
                + " , SRTF = " + String.format("%.2f", srtfTAT) + "\n";
        text += "Average Response Time: RR = " + String.format("%.2f", rrRT)
                + " , SRTF = " + String.format("%.2f", srtfRT) + "\n";
        text += "RR fairness: every ready process gets a bounded CPU turn.\n";
        text += "SRTF efficiency: it favors short jobs because the smallest remaining burst is selected first.\n";
        text += "Quantum effect: quantum " + quantum
                + " controls RR time slice. Smaller values add more switches, larger values make RR closer to FCFS.";

        comparisonArea.setText(text);

        String finalText;
        if (srtfWT < rrWT) {
            finalText = "SRTF is more efficient for this workload because it reduced average waiting time. ";
        } else if (rrWT < srtfWT) {
            finalText = "RR is better for this workload because it reduced average waiting time. ";
        } else {
            finalText = "Both algorithms have the same average waiting time. ";
        }

        finalText += "RR is still useful for fairness because the quantum rotates CPU access across ready processes.";
        finalArea.setText(finalText);
    }

    // ===== Clear All =====
    private void clearAll() {
        processTableModel.setRowCount(0);
        rrTableModel.setRowCount(0);
        srtfTableModel.setRowCount(0);
        processes.clear();

        txtProcessID.setText("");
        txtArrivalTime.setText("");
        txtBurstTime.setText("");
        txtQuantum.setText("");
        statusLabel.setText(" ");
        readyQueueArea.setText("");
        comparisonArea.setText("");
        finalArea.setText("");

        ganttRRPanel.clearGantt();
        ganttSRTFPanel.clearGantt();
    }

    // ===== Test Scenarios =====
    private void loadScenarioA() {
        clearAll();
        txtQuantum.setText("2");
        addProcessToTable("P1", 0, 5);
        addProcessToTable("P2", 1, 3);
        addProcessToTable("P3", 2, 8);
        addProcessToTable("P4", 3, 6);
    }

    private void loadScenarioB() {
        clearAll();
        txtQuantum.setText("1");
        addProcessToTable("P1", 0, 7);
        addProcessToTable("P2", 0, 4);
        addProcessToTable("P3", 0, 5);
        statusLabel.setText("Scenario B: run with quantum 1, then change it to 4 and run again.");
        statusLabel.setForeground(teal);
    }

    private void loadScenarioC() {
        clearAll();
        txtQuantum.setText("2");
        addProcessToTable("P1", 0, 2);
        addProcessToTable("P2", 1, 1);
        addProcessToTable("P3", 2, 3);
        addProcessToTable("P4", 3, 1);
    }

    private void loadScenarioD() {
        clearAll();
        txtQuantum.setText("2");
        addProcessToTable("P1", 0, 20);
        addProcessToTable("P2", 1, 2);
        addProcessToTable("P3", 2, 2);
        addProcessToTable("P4", 3, 1);
    }

    private void loadScenarioE() {
        clearAll();
        txtProcessID.setText("P1");
        txtArrivalTime.setText("-1");
        txtBurstTime.setText("0");
        txtQuantum.setText("0");
        statusLabel.setText("Invalid values loaded. Press Add Process or Run Simulation.");
        statusLabel.setForeground(red);
    }

    // ===== Add Row To Table And ArrayList =====
    private void addProcessToTable(String pid, int arrival, int burst) {
        processTableModel.addRow(new Object[] { pid, arrival, burst });
        processes.add(new Process(pid, arrival, burst));
    }

    // ===== Input Section =====
    private JPanel createInputSection() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Input Panel"));
        panel.setBackground(pageColor);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(pageColor);
        inputPanel.add(new JLabel("Process ID:"));
        inputPanel.add(txtProcessID);
        inputPanel.add(new JLabel("Arrival Time:"));
        inputPanel.add(txtArrivalTime);
        inputPanel.add(new JLabel("Burst Time:"));
        inputPanel.add(txtBurstTime);
        inputPanel.add(new JLabel("Quantum:"));
        inputPanel.add(txtQuantum);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(pageColor);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRun);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnScenarioA);
        buttonPanel.add(btnScenarioB);
        buttonPanel.add(btnScenarioC);
        buttonPanel.add(btnScenarioD);
        buttonPanel.add(btnScenarioE);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(pageColor);
        statusPanel.add(statusLabel);

        colorButton(btnAdd, blue);
        colorButton(btnRun, green);
        colorButton(btnClear, red);
        colorButton(btnScenarioA, orange);
        colorButton(btnScenarioB, cyan);
        colorButton(btnScenarioC, purple);
        colorButton(btnScenarioD, teal);
        colorButton(btnScenarioE, new Color(215, 145, 115));

        panel.add(inputPanel);
        panel.add(buttonPanel);
        panel.add(statusPanel);
        return panel;
    }

    // ===== Middle Section =====
    private JPanel createMiddleSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(pageColor);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        leftPanel.setBackground(pageColor);
        leftPanel.add(createProcessPanel());
        leftPanel.add(createReadyQueuePanel());

        JPanel ganttPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        ganttPanel.setBackground(pageColor);
        ganttPanel.add(wrapPanel("Round Robin Gantt Chart", ganttRRPanel));
        ganttPanel.add(wrapPanel("SRTF Gantt Chart", ganttSRTFPanel));

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(ganttPanel, BorderLayout.CENTER);
        return panel;
    }

    // ===== Process Table Panel =====
    private JPanel createProcessPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dynamic Process Input"));

        String[] columns = { "Process ID", "Arrival Time", "Burst Time" };
        processTableModel = new DefaultTableModel(columns, 0);
        processTable = new JTable(processTableModel);
        colorTable(processTable, new Color(125, 150, 180));

        JScrollPane scroll = new JScrollPane(processTable);
        scroll.setPreferredSize(new Dimension(380, 180));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ===== Ready Queue Panel =====
    private JPanel createReadyQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Ready Queue View for RR"));
        readyQueueArea.setEditable(false);
        panel.add(new JScrollPane(readyQueueArea), BorderLayout.CENTER);
        return panel;
    }

    // ===== Bottom Section =====
    private JPanel createBottomSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(1200, 280));
        panel.setBorder(BorderFactory.createTitledBorder("Results and Comparison"));

        String[] columns = { "Process ID", "Arrival", "Burst", "Completion", "WT", "TAT", "RT" };
        rrTableModel = new DefaultTableModel(columns, 0);
        srtfTableModel = new DefaultTableModel(columns, 0);
        rrTable = new JTable(rrTableModel);
        srtfTable = new JTable(srtfTableModel);
        colorTable(rrTable, teal);
        colorTable(srtfTable, teal);

        resultTableModel = rrTableModel;
        resultTable = rrTable;

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Results Table for RR", new JScrollPane(rrTable));
        tabs.addTab("Results Table for SRTF", new JScrollPane(srtfTable));

        JPanel textPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        comparisonArea.setEditable(false);
        comparisonArea.setLineWrap(true);
        comparisonArea.setWrapStyleWord(true);
        finalArea.setEditable(false);
        finalArea.setLineWrap(true);
        finalArea.setWrapStyleWord(true);

        textPanel.add(new JScrollPane(comparisonArea));
        textPanel.add(wrapPanel("Final Conclusion Area", new JScrollPane(finalArea)));

        panel.add(tabs, BorderLayout.CENTER);
        panel.add(textPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ===== Put Title Around A Panel =====
    private JPanel wrapPanel(String title, Component component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    // ===== Button Color =====
    private void colorButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    // ===== Table Header Color =====
    private void colorTable(JTable table, Color color) {
        table.setRowHeight(22);
        table.setGridColor(new Color(220, 220, 220));
        table.getTableHeader().setBackground(color);
        table.getTableHeader().setForeground(Color.WHITE);
    }

    // ===== Gantt Chart Panel =====
    public class GanttPanel extends JPanel {
        List<int[]> gantt = new ArrayList<>();
        ArrayList<Process> processList = new ArrayList<>();

        public GanttPanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 150));
        }

        public void setGantt(List<int[]> data, ArrayList<Process> list) {
            gantt = data;
            processList = list;
            repaint();
        }

        public void clearGantt() {
            gantt = new ArrayList<>();
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (gantt == null || gantt.size() == 0) {
                return;
            }

            int maxTime = gantt.get(gantt.size() - 1)[2];
            int xStart = 35;
            int y = 55;
            int height = 42;
            int width = getWidth() - 70;

            for (int i = 0; i < gantt.size(); i++) {
                int[] part = gantt.get(i);
                int index = part[0];
                int start = part[1];
                int end = part[2];

                int x = xStart + (start * width / maxTime);
                int w = (end - start) * width / maxTime;
                if (w < 30) {
                    w = 30;
                }

                g.setColor(getGanttColor(index));
                g.fillRect(x, y, w, height);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, w, height);

                String name = "Idle";
                if (index != -1) {
                    name = processList.get(index).getProcessID();
                }
                g.drawString(name, x + 8, y + 25);
                g.drawString(String.valueOf(start), x, y + height + 25);

                if (i == gantt.size() - 1) {
                    g.drawString(String.valueOf(end), x + w - 5, y + height + 25);
                }
            }
        }

        private Color getGanttColor(int index) {
            if (index == -1) return Color.LIGHT_GRAY;
            if (index % 4 == 0) return new Color(145, 180, 220);
            if (index % 4 == 1) return new Color(165, 195, 130);
            if (index % 4 == 2) return new Color(230, 185, 115);
            return new Color(190, 145, 195);
        }
    }

    // ===== Main Method =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SchedulerGUI gui = new SchedulerGUI();
            gui.setVisible(true);
        });
    }
}
