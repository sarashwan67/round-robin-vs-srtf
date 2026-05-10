import java.util.*;

class Execution {
    String pid;
    int start;
    int end;

    Execution(String pid, int start, int end) {
        this.pid = pid;
        this.start = start;
        this.end = end;
    }
}

class ProcessResult {
    String pid;
    int wt;   
    int tat;  
    int rt;  
    ProcessResult(String pid, int wt, int tat, int rt) {
        this.pid = pid;
        this.wt = wt;
        this.tat = tat;
        this.rt = rt;
    }
}

public class OutputComparison {

    static void printGanttChart(String title, List<Execution> executionList) {
        System.out.println("\n" + title + " Gantt Chart:");

        for (Execution e : executionList) {
            System.out.print("| " + e.pid + " ");
        }
        System.out.println("|");

        System.out.print(executionList.get(0).start);
        for (Execution e : executionList) {
            System.out.print("    " + e.end);
        }
        System.out.println();
    }

    static void printResultsTable(String title, List<ProcessResult> results) {
        System.out.println("\n" + title + " Results Table:");
        System.out.println("Process\tWT\tTAT\tRT");

        for (ProcessResult p : results) {
            System.out.println(p.pid + "\t" + p.wt + "\t" + p.tat + "\t" + p.rt);
        }
    }

    static double averageWT(List<ProcessResult> results) {
        int sum = 0;
        for (ProcessResult p : results) {
            sum += p.wt;
        }
        return (double) sum / results.size();
    }

    static double averageTAT(List<ProcessResult> results) {
        int sum = 0;
        for (ProcessResult p : results) {
            sum += p.tat;
        }
        return (double) sum / results.size();
    }

    static double averageRT(List<ProcessResult> results) {
        int sum = 0;
        for (ProcessResult p : results) {
            sum += p.rt;
        }
        return (double) sum / results.size();
    }

    static void printAverages(String title, List<ProcessResult> results) {
        System.out.println("\n" + title + " Averages:");
        System.out.printf("Average WT  = %.2f\n", averageWT(results));
        System.out.printf("Average TAT = %.2f\n", averageTAT(results));
        System.out.printf("Average RT  = %.2f\n", averageRT(results));
    }

    static void compareAlgorithms(List<ProcessResult> rrResults,
                                  List<ProcessResult> srtfResults,
                                  int quantum) {

        double rrAvgWT = averageWT(rrResults);
        double rrAvgTAT = averageTAT(rrResults);
        double rrAvgRT = averageRT(rrResults);

        double srtfAvgWT = averageWT(srtfResults);
        double srtfAvgTAT = averageTAT(srtfResults);
        double srtfAvgRT = averageRT(srtfResults);

        System.out.println("\nComparison Panel:");

        if (rrAvgTAT < srtfAvgTAT) {
            System.out.println("Faster Algorithm: RR");
        } else if (srtfAvgTAT < rrAvgTAT) {
            System.out.println("Faster Algorithm: SRTF");
        } else {
            System.out.println("Faster Algorithm: Both are equal");
        }

        if (rrAvgRT < srtfAvgRT) {
            System.out.println("Fairer Algorithm: RR");
        } else if (srtfAvgRT < rrAvgRT) {
            System.out.println("Fairer Algorithm: SRTF");
        } else {
            System.out.println("Fairer Algorithm: Both are equal");
        }

        System.out.println("Quantum Effect:");
        System.out.println("Quantum = " + quantum);
        System.out.println("Small quantum makes RR more fair but increases context switching.");
        System.out.println("Large quantum makes RR closer to FCFS.");

        System.out.println("\nFinal Summary:");

        if (srtfAvgWT < rrAvgWT) {
            System.out.println("SRTF is better at reducing waiting time.");
        } else if (rrAvgWT < srtfAvgWT) {
            System.out.println("RR is better at reducing waiting time in this case.");
        } else {
            System.out.println("Both algorithms have the same waiting time.");
        }

        System.out.println("RR is usually fair because each process gets CPU time using the quantum.");
    }

}
