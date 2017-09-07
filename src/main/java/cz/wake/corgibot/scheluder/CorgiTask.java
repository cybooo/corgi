package cz.wake.corgibot.scheluder;

public abstract class CorgiTask implements Runnable {

    private String taskName;

    private CorgiTask(){
    }

    public CorgiTask(String taskName) {
        this.taskName = taskName;
    }

    public boolean repeat(long delay, long interval) {
        return Scheduler.scheduleRepeating(this, taskName, delay, interval);
    }

    public void delay(long delay) {
        Scheduler.delayTask(this, delay);
    }

    public boolean cancel() {
        return Scheduler.cancelTask(taskName);
    }
}
