package edu.uci.ics.junyanj1.service.api_gateway.threadpool;

import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;

public class ThreadPool {
    private int numWorkers;
    private Worker[] workers;
    private ClientRequestQueue queue;

    public ThreadPool(int numWorkers) {
        this.numWorkers = numWorkers;
        queue = new ClientRequestQueue();
        workers = new Worker[this.numWorkers];
        for (int i=0; i < numWorkers; ++i) {
            workers[i] = Worker.CreateWorker(i,this);
            workers[i].start();
        }
    }

    public synchronized void add(ClientRequest clientRequest) {
        queue.enqueue(clientRequest);
        this.notify();
    }

    public synchronized ClientRequest remove() {
        try {
            while (queue.isEmpty()) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ServiceLogger.LOGGER.info("Received interrupted exception.");
            e.printStackTrace();
        }
        return queue.dequeue();
    }

    public ClientRequestQueue getQueue() {
        return queue;
    }
}
