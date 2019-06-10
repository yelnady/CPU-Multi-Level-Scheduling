public class CPU {
    public static void main(String[] args) {
        Scheduler s = new Scheduler();
        s.add(new Process("p1", 17, 0, 4, 7));
        s.add(new Process("p2", 6, 2, 7, 9));
        s.add(new Process("p3", 11, 5, 3, 4));
        s.add(new Process("p4", 4, 15, 6, 6));
        s.Start();
        s.Statistics();
    }
}
