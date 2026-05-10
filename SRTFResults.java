public class SRTFResults {
    
    public String processID;
    public int arrivalTime;
    public int burstTime;
    public int completionTime;
    public int TAT;
    public int WT;
    public int resopnseTime;

    public SRTFResults(String processID, int arrivalTime, int burstTime, 
        int completionTime, int TAT, int WT, int resopnseTime){

            this.processID = processID;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.completionTime = completionTime;
            this.TAT = TAT;
            this.WT = WT;
            this.resopnseTime = resopnseTime;
        }
}
