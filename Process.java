public class Process {

    private String processID;
    private int arrivalTime;
    private int burstTime;

    public Process(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    public String getProcessID() { return processID; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
}