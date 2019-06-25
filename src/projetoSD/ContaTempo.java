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

   
    private static int count;
    private int aux;
    
    public ContaTempo(int count) {
        this.count = count;
        
    }
    
    
    public void run(){
      setCount(getCount());
      aux = getCount();
       // System.out.println("entra run");
        while(true){
            //int aux = getCount();
            try{ 
                ServidorGUI.timerLabel.setText(getCount() + "");
                //System.out.println(aux);
                if(getCount() > 0){
                    
                    Thread.sleep(1000);
                    setCount(getCount()-1);
                    aux = getCount() - 1;
                    
                }else{// if (aux == 0 || getCount()==-1){
                    setCount(-1);
                }
             
                }catch (InterruptedException e){
                    System.out.println(e);
                }
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