package tptThreadPrograms;

import java.util.concurrent.*;

public class TimeoutExample {

    public static void main(String[] args) {
    	
    	Integer i=Integer.valueOf(10);
    	
    	Class c = TimeoutExample.class;
    	System.out.println(c.getSimpleName());

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<String> task = () -> {
            Thread.sleep(5001);  // simulate long running task
            return "Task Completed";
        };

        Future<String> future = executor.submit(task);

        try {
            String result = future.get(5, TimeUnit.SECONDS); // 5 sec timeout
            System.out.println(result);
        } catch (TimeoutException e) {
            System.out.println("Time exceeded! Task cancelled.");
            future.cancel(true); // interrupt thread
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
