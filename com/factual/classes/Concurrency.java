package com.factual.classes;

public class Concurrency {
    public int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        final int threadCount = Integer.parseInt(args[0]);
        final int actionsPerThread = Integer.parseInt(args[1]);
        final Concurrency c = new Concurrency();
        final Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++)  {
            threads[i] = new Thread() {
                    public void run() {
                        for (int i = 0; i < actionsPerThread; i++) {
                            c.counter++;
                        }
                    }
                };
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println(c.counter);
    }
}

/*
  $ java com.factual.classes.Concurrency 1 10000000
  10000000
  $ java com.factual.classes.Concurrency 10 10000000
  65701713
*/
