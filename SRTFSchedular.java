import java.util.ArrayList;
import java.util.List;

public class SRTFSchedular {

    public List<SRTFResults> simulate(ArrayList<Process> processes) {
        int n = processes.size();

        int[] remainingTime = new int[n];
        int[] completionTime = new int[n];
        int[] responseTime = new int[n];
        boolean[] started = new boolean[n];
        boolean[] finished = new boolean[n];

        List<int[]> gantt = new ArrayList<>();

        int time = 0;
        int done = 0;

        for (int i = 0; i < n; i++) {
            remainingTime[i] = processes.get(i).getBurstTime();
        }

        int lastPicked = -1;
        int sliceStart = 0;

        while (done < n) {

            int picked = -1;
            int minRemaining = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (finished[i]) {
                    continue;
                }
                if (processes.get(i).getArrivalTime() > time) {
                    continue;
                }
                if (remainingTime[i] < minRemaining) {
                    minRemaining = remainingTime[i];
                    picked = i;
                }
            }

            if (picked == -1) {
                if (lastPicked != -1) {
                    gantt.add(new int[] { lastPicked, sliceStart, time });
                    lastPicked = -1;
                }

                gantt.add(new int[] { -1, time, time + 1 });
                time++;
            }

            if (picked != lastPicked) {
                if (lastPicked != -1) {
                    gantt.add(new int[] { lastPicked, sliceStart, time });
                }
                sliceStart = time;
                lastPicked = picked;
            }

            if (!started[picked]) {
                started[picked] = true;
                responseTime[picked] = time - processes.get(picked).getArrivalTime();
            }

            remainingTime[picked]--;
            time++;

            if (remainingTime[picked] == 0) {
                gantt.add(new int[] { picked, sliceStart, time });
                lastPicked = -1;
                completionTime[picked] = time;
                finished[picked] = true;
                done++;
            }
        }

        List<SRTFResults> results = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            Process p = processes.get(i);
            int TAT = completionTime[i] - p.getArrivalTime();
            int WT = TAT - p.getBurstTime();

            results.add(new SRTFResults(p.getProcessID(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    completionTime[i],
                    TAT, WT, responseTime[i]));
        }

        results.sort((a, b) -> a.processID.compareTo(b.processID));

        return results;
    }
}
