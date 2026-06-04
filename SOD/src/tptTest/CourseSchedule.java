package tptTest;

import java.util.*;

public class CourseSchedule {

    public boolean canFinish(int numCourses, int[][] prerequisites) {

        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];

        // initialize graph
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        // build graph and indegree
        for (int[] p : prerequisites) {
            int course = p[0];
            int prereq = p[1];
            graph.get(prereq).add(course);
            indegree[course]++;
        }

        // queue for courses with no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0)
                queue.offer(i);
        }

        int completed = 0;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            completed++;

            for (int next : graph.get(curr)) {
                indegree[next]--;
                if (indegree[next] == 0)
                    queue.offer(next);
            }
        }

        return completed == numCourses;
    }
}

