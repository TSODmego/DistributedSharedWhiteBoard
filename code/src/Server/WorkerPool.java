//Zhiyuan Liu 1071288
package Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerPool {
    private BlockingQueue<Socket> taskQueue;
    private List<Worker> workers;
    private Server server;

    public WorkerPool(int numWorkers, Server server) {
        this.server = server;
        taskQueue = new LinkedBlockingQueue<>();
        workers = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker(taskQueue, server);
            workers.add(worker);
            worker.start();
        }
    }

    public void addTask(Socket clientSocket) {
        try {
            taskQueue.put(clientSocket);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}