package lab6;

public interface Monitor {
    public void Add(int howMany) throws InterruptedException;

    public void Sub(int howMany) throws InterruptedException;

    public void Get() throws InterruptedException;
}