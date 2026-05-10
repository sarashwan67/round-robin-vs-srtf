import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobinSchedular {

    public static class RRFinalResult {
        public List<RoundRobinResult> results;
        public List<int[]> gantt;

        public RRFinalResult(List<RoundRobinResult> results, List<int[]> gantt) {
            this.results = results;
            this.gantt = gantt;
        }
    }

    public RRFinalResult simulate(ArrayList<Process> processes, int quantum) {

        int n = processes.size();

        boolean[] started = new boolean[n];
        int[] remainingTime = new int[n];
        int[] responseTime = new int[n];
        int[] completionTime = new int[n];

        int currentTime = 0;
        int completed = 0;

        Queue<Integer> queue = new LinkedList<>();

        List<int[]> gantt = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            remainingTime[i] = processes.get(i).getBurstTime();
            started[i]=false;
        }

        while (completed < n) {

            
            for (int i = 0; i < n; i++) {
                if (processes.get(i).getArrivalTime() <= currentTime && !queue.contains(i) && remainingTime[i] > 0) {
                    queue.add(i);
                }
            }

            if (queue.isEmpty()) {
                gantt.add(new int[]{-1, currentTime, currentTime + 1});
                currentTime++;
                continue;
            }

            int index = queue.poll();
            Process p = processes.get(index);

            int start = currentTime;

            
            if (!started[index]) {
                started[index] = true;
                responseTime[index] = currentTime - p.getArrivalTime();
            }

            int exec = Math.min(quantum, remainingTime[index]);

            currentTime += exec;
            remainingTime[index] -= exec;

            int end = currentTime;

            gantt.add(new int[]{index, start, end});

            for (int i = 0; i < n; i++) {
                if (processes.get(i).getArrivalTime() > start && processes.get(i).getArrivalTime() <= currentTime 
                    && !queue.contains(i) && remainingTime[i] > 0) {
                    queue.add(i);
                }
            }

            if (remainingTime[index] == 0) {
                completionTime[index] = currentTime;
                completed++;
            } else {
                queue.add(index);
            }
        }

        
        List<RoundRobinResult> results = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            int tat = completionTime[i] - processes.get(i).getArrivalTime();
            int wt = tat - processes.get(i).getBurstTime();

            results.add(new RoundRobinResult(
                    processes.get(i).getProcessID(),
                    processes.get(i).getArrivalTime(),
                    processes.get(i).getBurstTime(),
                    completionTime[i],
                    tat,
                    wt,
                    responseTime[i]
            ));
        }

        return new RRFinalResult(results, gantt);
    }
}