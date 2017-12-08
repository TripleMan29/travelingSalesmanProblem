import java.util.Arrays;
import java.util.Random;

class TSP {

    private static final int alpha = 2;
    private static final int beta  = 3;
    private static final double rho = 0.01;
    private static final double Q  = 2.0;
    private static int numCities;
    private static int numAnts;
    private static int[][] distance;

    TSP(int numCities, int numAnts, int[][] distance){
        TSP.distance = distance;
        TSP.numAnts = numAnts;
        TSP.numCities = numCities;
    }

     int Ant(){
        int[][] ants = InitAnts(numAnts, numCities);
        double[][] pheromones =  InitPheromones();


        int[] bestTrail = BestTrail(ants);
        int bestPathLength = PathLength(bestTrail);

        int time = 0;
         int maxTime = 25;
         while(time < maxTime){
            UpdateAnts(ants, pheromones);
            UpdatePheromones(pheromones, ants);

            int[] newBestTrail = BestTrail(ants);
            int newBestPathLength = PathLength(newBestTrail);

            if(newBestPathLength < bestPathLength){
                bestPathLength = newBestPathLength;
                bestTrail = newBestTrail;
            }
            ++time;
        }
         System.out.println(Arrays.toString(bestTrail));
        return bestPathLength;
    }

    static int randomWithRange(int min, int max){
        Random rg = new Random();
        return rg.nextInt(max-min) + min;
    }


    private static int Distance(int cityX, int cityY){
        return distance[cityX][cityY];
    }

    private static int[][] InitAnts(int numAnts, int numCities){
        int[][] ants = new int[numAnts][numCities];
        for (int k = 0; k < numAnts; k++) {
            int start = randomWithRange(0, numCities);
            ants[k] = RandomTrail(start, numCities);
        }
        return ants;
    }

    private static int[] RandomTrail(int start, int numCities){
        int[] trail = new int[numCities];
        for (int i = 0; i < numCities; i++) {
            trail[i] = i;
        }

        for (int i = 0; i < numCities; i++) {
            int r = randomWithRange(i, numCities);
            int temp = trail[r];
            trail[r] = trail[i];
            trail[i] = temp;
        }

        int index = IndexOfTarget(trail, start);
        int temp = trail[0];
        trail[0] = trail[index];
        trail[index] = temp;

        return trail;
    }

    private static int IndexOfTarget(int[] trail, int elem){
        int n = 0;
        for (int i = 0; i < trail.length; i++) {
            if (elem == trail[i]) {
                n = i;
            }
        }
        return n;
    }

    private static double[][] InitPheromones(){
        double[][] pheromones = new double[numCities][numCities];

        for (int i = 0; i < numCities; i++){
            for (int j = 0; j < numCities; j++){
                pheromones[i][j] = 0.01;
            }
        }
        return pheromones;
    }

    private static void UpdatePheromones(double[][] pheromones, int[][] ants){
        for (int[] ant : ants) {
            int antLength = ant.length;
            double length = Distance(ant[0], ant[antLength - 1]);

            for (int j = 0; j < pheromones.length; j++) {
                for (int k = 0; k < pheromones.length; k++) {
                    double decrease = (1.0 - rho) * pheromones[j][k];
                    double increase = 0.0;
                    if (EdgesInTrail(j, k, ant)) {
                        increase = Q / length;
                    }
                }
            }
        }
    }


    private static boolean EdgesInTrail(int j, int k, int[] ant){
        boolean a = false;
        boolean b = false;
        for (int anAnt : ant) {
            if (j == anAnt) {
                a = true;
            }

            if (k == anAnt) {
                b = true;
            }
        }
        return a && b;
    }

    private static void UpdateAnts(int[][] ants, double[][] pheromones){
        for (int i = 0; i < ants.length; i++){
            int start = randomWithRange(0, numCities);
            int[] newTrail = BuildTrail(start, pheromones);
            ants[i] = newTrail;
        }
    }

    private static int[] BuildTrail(int start, double[][] pheromones){
        int[] trail = new int[numCities];
        boolean[] visited = new boolean[numCities];

        trail[0] = start;
        visited[start] = true;

        for (int k = 0; k < numCities - 1; k++){
            int cityX = trail[k];
            int next = NextCity(cityX, visited, pheromones, start);
            trail[k+1] = next;
            visited[next] = true;
        }
        return trail;
    }

    private static int NextCity(int cityX, boolean[] visited, double[][] pheromones, int start){
        double[] n = new double[numCities];
        double[] m = new double[numCities];
        double sum = 0.0;

        for (int i = 0; i < numCities; ++i){
            if(i == cityX){
                m[i] = 0.0;
            } else if(visited[i]){
                m[i] = 0.0;
            } else{
                m[i] = Math.pow(pheromones[cityX][i], alpha) * Math.pow((1.0 / Distance(cityX, i)), beta);
            }

            if(m[i] < 0.0001){
                m[i] = 0.0001;
            }

            sum += m[i];
        }
        for (int i = 0; i < numCities; ++i){
            n[i] = m[i] / sum;
        }

        double[] cumul = new double[numCities + 1];

        cumul[0] = 0;
        for (int i = 0; i < numCities; i++){
            cumul[i + 1] = cumul[i] + n[i];
        }
        int nxtCity = start;
        while(visited[nxtCity])
        {
            double p = Math.random();
            for (int i = 0; i < cumul.length -1 ; ++i){
                if(p >= cumul[i] && p < cumul[i + 1]){
                    nxtCity = i;
                }
            }
        }
        return nxtCity;
    }

    private static int[] BestTrail(int[][] ants){
        int[] pathLengths = new int[ants.length];
        for (int i = 0; i < ants.length; i++){
            pathLengths[i] = PathLength(ants[i]);
        }
        int minIndex = 0;
        for (int j = 0; j < pathLengths.length - 1; j++){
            if(pathLengths[j + 1] < pathLengths[minIndex]){
                minIndex = j + 1;
            }
        }
        return ants[minIndex];
    }

    private static int PathLength(int[] ant){
        int pathLength = 0;
        for (int i = 0; i < numCities - 1; i++){
            pathLength += Distance(ant[i], ant[i + 1]);
        }
        return pathLength;
    }
}