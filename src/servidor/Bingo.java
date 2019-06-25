/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import projetoSD.Mensagem;

/**
 *
 * @author Leandro
 */
public class Bingo extends Thread {
    ArrayList<Socket> sktArray;
    JSONArray players;
    
    private int running = 0;
    
    public Bingo(ArrayList<Socket> socketArray, JSONArray readyArray){
        sktArray = socketArray;
        players = readyArray;
        this.start();
    }
    
    @Override
    public void run(){
        try{

            while (true) {
                if(getRunning() == 0){ // Aguardando inicio do jogo
                    
                }
                
                if(getRunning() == 1){ // Preparando jogo: sorteando cartelas
                    try {
                        sortearCartelas();
                        setRunning(2);
                    } catch (IOException ex) {
                        System.out.println("Erro IOException ao sortear cartelas: " + ex);
                    } catch (JSONException ex) {
                        System.out.println("Erro JSONException ao sortear cartelas: " + ex);
                    }
                    
                }
                
                if(getRunning() == 2){ // Jogo rolando: sorteio de numeros
                    // Se timer == 0 sorteia e timer = 10
                }
                
                
               Thread.sleep(200);
            }
        }catch (InterruptedException ex) {
            Logger.getLogger(Bingo.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            
        }
    }
    
    public void sortearCartelas() throws IOException, JSONException{ // Gera cartelas aleatorias e as envia para os clientes do JSONArray players
        JSONArray cartela = new JSONArray();
        
        int a, b, r;
        a = b = r = 0; // Variaveis para a faixa de valores das colunas
        
        boolean unique = false;
        
        Random rGen = new Random();
        
        for(int i = 0; i < players.length(); i++){
            for(int j = 0; j < 25; j++){
                unique = false;

                if(j <= 4){
                    a = 1;
                    b = 15;
                }else if(j <= 9){
                    a = 16;
                    b = 30;
                }else if(j <= 14){
                    a = 31;
                    b = 45;
                }else if(j <= 19){
                    a = 46;
                    b = 60;
                }else if(j <= 24){
                    a = 61;
                    b = 75;
                }
                
                if(j == 12){
                    cartela.put(j, 0);
                
                }else{
                    // Garante que nao haverao numeros repetidos
                    while(!unique){
                        // gera num aleatorio dentro da faixa pre estabelecida
                        r = rGen.nextInt((b - a) + 1) + a;
                        unique = true;
                        // verifica se nao existe nenhum numero repetido na cartela
                        for(int k = 0; k < cartela.length(); k++){
                            if(cartela.getInt(k) == r)
                                unique = false;
                        }
                    }
                    // Insere o valor gerado na cartela
                    cartela.put(j, r);
                }
            }
            
            //System.out.println(cartela.toString(2));
            // Envia a cartela para o jogador players(i)
            Mensagem msg = new Mensagem();
            // Procura em sktArray o socket correspondente ao players(i)
            int k = 0;
            while(k < sktArray.size() &&
                  !sktArray.get(k).getInetAddress().getHostAddress().equals(players.getJSONObject(i).getString("IP")) &&
                  sktArray.get(k).getPort() != Integer.parseInt(players.getJSONObject(i).getString("PORTA"))
            )k++;
            
            msg.COD = "cartela";
            msg.CARTELA = cartela;

            if(k < sktArray.size()){
                OutputStream osAux = sktArray.get(k).getOutputStream();
                Writer osWriterAux = new OutputStreamWriter(osAux);
                BufferedWriter buffWriterAux = new BufferedWriter(osWriterAux);

                System.out.println("SERVIDOR -> " + sktArray.get(k).getRemoteSocketAddress() + " :" + msg.toStr());                    
                buffWriterAux.write(msg.toStr() + "\r\n");
                buffWriterAux.flush();
                //gui.refreshGUI('o', msg.toStr());
            }
            
            // Armazena a cartela do jogador no servidor
            
            // Limpa a cartela auxiliar
            for(int j = 0; j < 24; j++)
                cartela.remove(j);
        }
        
    }

    /**
     * @return the running
     */
    public int getRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(int running) {
        this.running = running;
    }
}