import java.util.Vector;

import static java.lang.Math.ceil;

public class Scheduler {
    private Vector <Process> AllProcesses = new Vector <>(); //while moving process btwn objects its saved here
    private Vector <Process> first = new Vector <>();// FCFS for just
    private Vector <Process> second = new Vector <>(); //Non-Preemptive Priority
    private Vector <Process> third = new Vector <>(); //   Preemptive SJF
    int Time = 0;

    void setWhole() {
        int count=0;
        for (Process process : first) {
            process.idxAtAll = 0;
            AllProcesses.add(process);
            count++;
        }
    }
    void add(Process p){
        first.add(p);
    }
    int foundMinArrival() {
        int min = 10000;

        /* First Queue */
        int count = 0;
        int target = -1;
        for (Process process : first) {
            if (process.getArrival_time() < min) {
                target = count;
                min = process.getArrival_time();
            }
            count++;
        }
        return target;
    }

    int foundMinPriority() {
        int min = 10000;

        /* second Queue */
        int count = 0;
        int target = 0;
        for (Process process : second) {
            if (process.getPriority() < min) {
                min = process.getPriority();
                target = count;
            }
            count++;
        }

        return target;
    }

    int foundMinBurstTime() {
        int min = 10000;

        /* Third Queue */
        int count = 0;
        int target = -1;
        for (Process process : third) {
            if (process.getBurst_time() < min) {
                target = count;
                min = process.getBurst_time();
            }
            count++;
        }
        return target;
    }
    void TurnAround(){
        for (Process process : AllProcesses) {
            process.setTurnaround_Time(process.getTurnaround_Time()+process.waiting_Time);
        }
    }
    void Statistics(){
        double wait =0;double turns = 0 ;
        for (Process process : AllProcesses) {
            wait += process.waiting_Time;turns+=process.getTurnaround_Time();
            System.out.println(process.getName() +" : waiting time is "+process.waiting_Time +", TurnAroundTime is "+process.getTurnaround_Time());
        }
        System.out.println("The Average Waiting Time is "+wait/AllProcesses.size());
        System.out.println("The Average Turnaround Time is "+turns/AllProcesses.size());
    }
    void Start() {
        //while True there's a Process in the first branch
        //we'll try only now FCFS
        setWhole();
        Process selected;

        int target_idx = 0;
        while (true) {
            boolean flag = false;
            System.out.println("---------FirstQueue-----------");
            while (first.size() != 0) {
                target_idx = foundMinArrival();

                if (first.elementAt(target_idx).getArrival_time() > Time)
                    break; //it compares the min arrival time by currently time

                selected = first.remove(target_idx);//get the first arrival process

                selected.waiting_Time = (Time - selected.getArrival_time()); //calculate the waiting time
                int passedTime = (int) ceil(0.25 * selected.getQuantum_time()); // how much take in the first queue
                selected.setBurst_time(selected.getBurst_time() - passedTime); // reduce the burst time done
                selected.setQuantum_time(selected.getQuantum_time() + 2); //increase the quantum time
                //selected.setTurnaround_Time(selected.waiting_Time+selected.getBurst_time());
                System.out.println("The start time is " + Time);

                Time += passedTime; //how much time has passed
                selected.stoppedTime = Time;
                if (selected.getBurst_time() > 0) { //if still has time to do, add to the second
                    second.add(selected);
                } else {
                    selected.setQuantum_time(0); //all the burst time has finished
                }

                System.out.println(selected.getName() + " started, waited till now  " + selected.waiting_Time);
                System.out.println("It has worked for only "+passedTime);
                System.out.println("New Quantum is " + selected.getQuantum_time());
                System.out.println();

                AllProcesses.set(selected.idxAtAll, selected);


                //during second branch if found any in first its time has come
                //we should check each loop if anyone has arrived at the first one

            }
            System.out.println("---------SecondQueue----------");
            while (second.size() != 0) {
                target_idx = foundMinPriority();
                selected = second.remove(target_idx);//get the highest priority
                System.out.println("The start time is " + Time);

                selected.waiting_Time += (Time - selected.stoppedTime); //calculate the waiting time
                int passedTime = (int) ceil(0.25 * selected.getQuantum_time()); // how much take in the second queue
                selected.setBurst_time(selected.getBurst_time() - passedTime); // reduce the burst time done
                selected.setQuantum_time(selected.getQuantum_time() + (int) ceil(selected.getQuantum_time() / 2.0)); //increase the quantum time

                Time += passedTime; //how much time has passed
                selected.stoppedTime = Time;
                if (selected.getBurst_time() > 0) { //if still has time to do add to the second
                    third.add(selected);
                } else {
                    selected.setQuantum_time(0); //all the burst time has finished
                }
                AllProcesses.set(selected.idxAtAll, selected);


                System.out.println(selected.getName() + "  started, waited till now  " + selected.waiting_Time);
                System.out.println("It has worked for only "+passedTime);
                System.out.println("New Quantum is " + selected.getQuantum_time());
                System.out.println();

                //we here want to check if any have arrived to the first
                target_idx = foundMinArrival();
                if (target_idx != -1) {
                    if (first.elementAt(target_idx).getArrival_time() <= Time) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) continue;
            System.out.println("---------ThirdQueue----------");
            while (third.size() != 0) {
                System.out.println("The start time is " + Time);
                target_idx = foundMinBurstTime();
                selected = third.remove(target_idx);//get the shortest burst time and REMOVE IT
                target_idx = foundMinArrival();
                int passedTime =0;
                selected.waiting_Time += (Time - selected.stoppedTime); //calculate the waiting time


                if (target_idx!=-1){
                    //if found item in the first queue so check if it has been arrived
                    if (Time+selected.getBurst_time() > first.elementAt(target_idx).getArrival_time()){
                        passedTime =first.elementAt(target_idx).getArrival_time()-Time;
                        System.out.println("third will stop now "+passedTime);
                        selected.setBurst_time(selected.getBurst_time() - passedTime); // reduce the burst time done
                        selected.setQuantum_time(selected.getQuantum_time()*2-passedTime ); //increase the quantum time
                        third.add(selected);
                        Time+=passedTime;
                        flag = true;
                    }
                }
                else{
                    passedTime = selected.getBurst_time();
                    selected.setBurst_time(0);
                    selected.setQuantum_time(0);
                    Time+=passedTime;
                }

                AllProcesses.set(selected.idxAtAll, selected);
                System.out.println(selected.getName() + "  started, waited till now  " + selected.waiting_Time);
                System.out.println("It has worked for only "+passedTime);
                System.out.println("New Quantum is " + selected.getQuantum_time());
                System.out.println("Process finished at "+Time);
                System.out.println();
                if (flag==true)break;
            }
            if (flag) continue;

            break;
        }
        System.out.println("---------Finished----------");
        System.out.println();
        TurnAround();
    }



/*
For each scheduler output the following:
	Processes execution order
	Waiting Time for each process
	Turnaround  Time  for each process
	Average Waiting Time        //for the whole scheduler
	Average Turnaround  Time    //for the whole scheduler brdo
	Print all history update of quantum time for each process (AG  Scheduling)

*/

}
