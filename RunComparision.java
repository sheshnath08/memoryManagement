import java.io.File;
import java.util.Scanner;

/**
 * Created by sheshnath on 12/12/2015.
 * Observations:
 * for PFF:
 *  By choosing large value of F page faults are reduced but the size of resident set increases.
 *
 * for VSWS:
 *  By choosing Large value of L & Q we can reduce the page fault but again size of resident set will increase.
 */
public class RunComparision {
    static int maxPages; //number of pages in a process
    static int F;
    static int M;
    static int L;
    static int Q;
    public static void main(String args[]){
	String path = args[0];
        File f = new File(path);
        try{
            Scanner in = new Scanner(f);
            maxPages = in.nextInt();
            F = maxPages/10;
            M = maxPages /10;
            L = maxPages / 5;
            Q = maxPages / 15;

            System.out.println("Running PFF for F : " + F);
            PageFrequencyFault pff = new PageFrequencyFault(maxPages,in,F);
            pff.CalculatePageFault();
            System.out.println("Page fault counted : " + pff.getPageFault());
            System.out.println("Fault Rate" + pff.getFaultRate()+"%");

            in = new Scanner(f);
            in.nextInt(); //skipping first integer in the file;

            System.out.println("Running VSWS for M: "+M +", L: "+ L +", Q: "+Q);
            VSWS vsws = new VSWS(maxPages,M,L,Q,in);
            vsws.calculatePageFault();
            System.out.println("Page fault counted : " + vsws.getPageFault());
            System.out.println("Fault Rate" + vsws.getFaultRate()+"%");

            // Running again.
	    System.out.println("Running again with different values on same dataset");

            in = new Scanner(f);
            in.nextInt(); //skipping first integer in the file;

            F = maxPages/2;
            M = maxPages /4;
            L = maxPages / 2;
            Q = maxPages / 5;

            System.out.println("Running PFF for F : " + F);
            PageFrequencyFault pff2 = new PageFrequencyFault(maxPages,in,F);
            pff2.CalculatePageFault();
            System.out.println("Page fault counted : " + pff2.getPageFault());
            System.out.println("Fault Rate" + pff2.getFaultRate()+"%");

            in = new Scanner(f);
            in.nextInt(); //skipping first integer in the file;

            System.out.println("Running VSWS for M: "+M +", L: "+ L +", Q: "+Q);
            VSWS vsws2 = new VSWS(maxPages,M,L,Q,in);
            vsws2.calculatePageFault();
            System.out.println("Page fault counted : " + vsws2.getPageFault());
            System.out.println("Fault Rate" + vsws2.getFaultRate()+"%");


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class PageFrequencyFault {
    int pages;
    int F;//threshold;
    Scanner in = null;
    Node VM[] = null;
    int pageFault = 0;
    int totalPage = 0;

    PageFrequencyFault(int pages,Scanner in,int f){
        this.pages = pages;
        this.in = in;
        this.F =f;
        VM = new Node[pages];
        for(int i = 0;i<pages;i++){
            VM[i] = new Node();
        }
        totalPage = 0;
        pageFault = 0;
    }

    void CalculatePageFault() {
        int currentFaultRecord = 0;
        while (in.hasNext()) {
            int page = in.nextInt();
            VM[page].usebit = 1;
            if (VM[page].resident == 0) {
                pageFault++;
                VM[page].resident = 1;
                if ((totalPage - currentFaultRecord) > F) {
                    removePages();
                }
                currentFaultRecord = totalPage;
                resetUsebit();
            }
            totalPage++;
        }
    }



    private void removePages() {
        for(int i = 0; i < VM.length; i++){
            if(VM[i].resident == 1 && VM[i].usebit == 0){
                VM[i].resident = 0;
            }
        }
    }


    private void resetUsebit() {
        for(int i = 0; i < VM.length; i++){
            VM[i].usebit = 0;
        }
    }


    int getPageFault(){
        return pageFault;
    }

    double getFaultRate(){
        return (double)pageFault/totalPage * 100;
    }

}

class VSWS {
     int M;
     int L;
     int Q;
    int pages;
    int pageFault;
    int totalPage;
    Node VM[] = null;
    Scanner in = null;

    VSWS(int pages, int M,int L,int Q, Scanner in){
        this.pages = pages;
        this.M = M;
        this.L = L;
        this.Q = Q;
        this.in = in;
        VM = new Node[pages];
        for(int i = 0;i<pages;i++){
            VM[i] = new Node();
        }

        totalPage = 0;
        pageFault = 0;

    }

    void calculatePageFault(){
        int currentFaultRecord = 0;
        int counter = 0; //helps in checking Q faults
        while (in.hasNext()) {
            int page = in.nextInt();
            VM[page].usebit = 1;
            if (VM[page].resident == 0)
            {
                pageFault++;
                counter++;
                VM[page].resident = 1;
                if (currentFaultRecord >= L)
                {
                    removePages();
                }
                if(counter >= Q){
                    if(currentFaultRecord >= M){
                        removePages();
                        counter = 0;
                    }
                    else {
                        counter ++;
                    }
                }
                currentFaultRecord = totalPage;
                resetUsebit();
            }
            totalPage++;
        }
    }

    private void removePages() {
        for(int i = 0; i < VM.length; i++){
            if(VM[i].resident == 1 && VM[i].usebit == 0){
                VM[i].resident = 0;
            }
        }
    }


    private void resetUsebit() {
        for(int i = 0; i < VM.length; i++){
            VM[i].usebit = 0;
        }
    }

    int getPageFault(){
        return pageFault;
    }

    double getFaultRate(){
        return (double)pageFault/totalPage * 100;
    }
}

class Node {
    int usebit;
    int resident; // if set, then page is in resident set
    Node(){
        usebit = 0;
        resident = 0;
    }
}