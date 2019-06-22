/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoSD;
/*
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
*/
import servidor.ServidorGUI;

/**
 *
 * @author joao_
 */
public class ContaTempo extends Thread implements Runnable  {

    //private Boolean isIt;
    //private java.util.Timer timer = new java.util.Timer(); //new timer
    //private TimerTask task;
    private static int count;
    
    
    public ContaTempo(int count) {
        System.out.println("instanciou");
        this.count = count;
    }
   
    public void run(){
        try{
            while(true){
                int aux = getCount();
                ServidorGUI.timerLabel.setText(getCount() + "");
                if(aux!= -1){
                    System.out.println();
                    Thread.sleep(1000);
                    aux--;
                    setCount(aux);
                }
                if(getCount() == -1 ){
                    System.out.println("Acabou");
    //                System.exit(0);
                }
                else{
                    System.out.println("parada interrompida");
                }
            }
        }catch (InterruptedException e){
                System.out.println(e);
            }
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        ContaTempo.count = count;
    }
    

}    
  



/*public void Comeca(int count){
        
        isIt = false;
        task = new TimerTask() {
            int aux = count;
            public void run() {                
                System.out.println(Integer.toString(aux)); 
                System.out.println(isIt);
                aux--;
                if (aux == -1){
                    timer.cancel();                                       
                }else if(isIt){
                    cancel();
                    timer.cancel();
                    //isIt = false;
                }
            }
        };
       // task.cancel();
       timer.scheduleAtFixedRate(task, 1000, 1000); // =  timer.scheduleAtFixedRate(task, delay, period
       
    } */