import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestForTSP {
    @Test
    void randomGenerator() {
        int numAnts = 20;
        int numCities = 39;
        int[][] distance = new int[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i == j) {
                    distance[i][j] = 0;
                } else {
                    int d = TSP.randomWithRange(1, 40);
                    distance[i][j] = d;
                    distance[j][i] = d;
                }
            }
        }
        System.out.println(Arrays.deepToString(distance));
        int tsp = new TSP(numAnts, numCities, distance).Ant();
        System.out.println(tsp);


        int there = 0;
        int greedy = 0;
        ArrayList was = new ArrayList();
        was.add(0);
        for (int i = 0; i < numCities; i++) {
            int lowPriority = 40;
            if (was.size() != numCities) {
                for (int j = 0; j < numCities; j++) {
                    if (!was.contains(j)) {
                        if ((distance[i][j] < lowPriority)&&(distance[i][j] != 0)) {
                            lowPriority = distance[i][j];
                            there = j;
                        }
                    }
                }
                greedy += distance[i][there];
                was.add(there);
                i = there - 1;
            }
            else {
                break;
            }
        }
        System.out.println(was);
        System.out.println(greedy);
        assertTrue(tsp <= greedy);
    }
}
