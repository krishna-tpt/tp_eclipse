package tptTest;

import java.util.*;

public class CourseScheduleDFS {

    public boolean canFinish(int numCourses, int[][] prerequisites) {

        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++)
            graph.add(new ArrayList<>());

        for (int[] p : prerequisites) {
            graph.get(p[1]).add(p[0]);
        }

        int[] state = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) {
                if (!dfs(i, graph, state))
                    return false;
            }
        }
        return true;
    }

    private boolean dfs(int node, List<List<Integer>> graph, int[] state) {

        state[node] = 1; // visiting

        for (int neighbor : graph.get(node)) {
            if (state[neighbor] == 1)
                return false; // cycle
            if (state[neighbor] == 0) {
                if (!dfs(neighbor, graph, state))
                    return false;
            }
        }

        state[node] = 2; // visited
        return true;
    }
}
