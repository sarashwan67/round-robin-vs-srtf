public class RoundRobinResult{
    
    public String processID;
    public int arrivalTime;
    public int burstTime;
    public int completionTime;
    public int TAT;
    public int WT;
    public int responseTime;

    public RoundRobinResult(String processID, int arrivalTime, int burstTime, 
        int completionTime, int TAT, int WT, int responseTime){

            this.processID = processID;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.completionTime = completionTime;
            this.TAT = TAT;
            this.WT = WT;
            this.responseTime = responseTime;
        }
}