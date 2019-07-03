/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import org.json.JSONArray;

/**
 *
 * @author Leandro
 */
public class Player {
    String IP;
    String PORTA;
    String NOME;
    JSONArray CARTELA;
    boolean[] marcados;
    
    public Player(){
        IP = new String();
        PORTA = new String();
        NOME = new String();
        //CARTELA = new JSONArray();
        
        marcados = new boolean[75];
        
    }

}
