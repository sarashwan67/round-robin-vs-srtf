import java.util.ArrayList;

public class ProcessManager {

    private ArrayList<Process> processes = new ArrayList<>();

    public boolean addProcess(Process p) {
        if (p == null) {
            return false;
        }
        processes.add(p);
        return true;
    }

    public boolean addProcess(String id, String arrivalTime, String burstTime) {
        if (!InputValidator.validate(id, arrivalTime, burstTime)) {
            return false;
        }

        int at = Integer.parseInt(arrivalTime);
        int bt = Integer.parseInt(burstTime);
        Process process = new Process(id.trim(), at, bt);
        return addProcess(process);
    }

    public ArrayList<Process> getProcesses() {
        return processes;
    }

    public void clearProcesses() {
        processes.clear();
    }

    public int getProcessCount() {
        return processes.size();
    }
}