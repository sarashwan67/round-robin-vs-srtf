import java.util.ArrayList;
import java.util.List;
// public class Main {

//     public static void main(String[] args) {
        // ProcessManager manager = new ProcessManager();

        // manager.addProcess("p1", "0", "4");
        // manager.addProcess("p2", "1", "5");
        // manager.addProcess("p3", "2", "8");
        // manager.addProcess("p4", "3", "7");

        // SRTFSchedular schedular = new SRTFSchedular();

        // var results = schedular.simulate(manager.getProcesses());

        // for (SRTFResults r : results) {
        //     System.out.println(
        //             r.processID +
        //             " CT=" + r.completionTime +
        //             " TAT=" + r.TAT +
        //             " WT=" + r.WT +
        //             " RT=" + r.resopnseTime
        //     );
        // }

// }
// }



public class Main {

    public static void main(String[] args) {
        

        ArrayList<Process> processes = new ArrayList<>();

        processes.add(new Process("P1", 0, 5));
        processes.add(new Process("P2", 1, 3));
        processes.add(new Process("P3", 2, 1));
        processes.add(new Process("P4", 3, 2));

        int quantum = 2;

        RoundRobinSchedular rr = new RoundRobinSchedular();

        RoundRobinSchedular.RRFinalResult result =
                rr.simulate(processes, quantum);

        System.out.println("=== ROUND ROBIN RESULTS ===");

        for (RoundRobinResult r : result.results) {
            System.out.println(
                    r.processID +
                    " | CT=" + r.completionTime +
                    " | TAT=" + r.TAT +
                    " | WT=" + r.WT +
                    " | RT=" + r.responseTime
            );
        }

        System.out.println("\n=== GANTT CHART ===");

        for (int[] g : result.gantt) {

            String name = (g[0] == -1) ? "IDLE" : "P" + (g[0] + 1);

            System.out.println(name + " : " + g[1] + " -> " + g[2]);
        }
    }
}