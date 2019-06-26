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

   
    private int count;
    private int aux;
    public boolean counting = false;
    
    public ContaTempo(int count) {
        this.count = count;
        
    }
    
    
    public void run(){
        aux = getCount();
        System.out.println("entra run");
        
        try{ 
            //ServidorGUI.timerLabel.setText(getCount() + "");
            while(true){
                //int aux = getCount();
                if(counting && getCount() > -1){
                    System.out.println("timer: " + count);

                    sleep(1000);
                    count = count - 1;
                }
            
                sleep(5);
            }

        }catch (InterruptedException e){
            System.out.println(e);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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