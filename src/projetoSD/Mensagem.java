/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoSD;

import org.json.*;

/**
 *
 * @author Leandro
 */
public class Mensagem {
    // Valores Chave
    public String COD;
    public String NOME;
    public String MSG;
    public JSONArray LISTACLIENTE;
    public String STATUS;
    public JSONArray CARTELA;
    
    public JSONObject JSON;
    
    // Inicializar as variaveis com null
    public Mensagem(){
        this.COD = null;
        this.NOME = null;
        this.MSG = null;
        this.STATUS = null;
        this.LISTACLIENTE = null;
        this.CARTELA = null;
        
        this.JSON = new JSONObject();
    }
    
    public Mensagem(String str){
        this.COD = null;
        this.NOME = null;
        this.MSG = null;
        this.STATUS = null;
        this.LISTACLIENTE = null;
        this.CARTELA = null;
        
        this.JSON = new JSONObject(str);
        
        fromStr(str);
    }   
    
    public Mensagem(JSONObject obj){
        this.COD = null;
        this.NOME = null;
        this.MSG = null;
        this.STATUS = null;
        this.LISTACLIENTE = null;
        this.CARTELA = null;
        
        this.JSON = obj;
        
        fromJSON(obj);
    }
    
    public String toStr(){
        try{
            if(this.COD != null){
                this.JSON.put("COD", this.COD);
            }else{
                this.JSON.put("COD", JSONObject.NULL);
            }
            
            if(this.NOME != null){
                this.JSON.put("NOME", this.NOME);
            }else{
                this.JSON.put("NOME", JSONObject.NULL);
            }
            
            if(this.MSG != null){
                this.JSON.put("MSG", this.MSG);
            }else{
                this.JSON.put("MSG", JSONObject.NULL);
            }
            
            if(this.STATUS != null){
                this.JSON.put("STATUS", this.STATUS);
            }else{
                this.JSON.put("STATUS", JSONObject.NULL);
            }
          
            if(this.LISTACLIENTE != null){
                this.JSON.put("LISTACLIENTE", this.LISTACLIENTE);
            }else{
                this.JSON.put("LISTACLIENTE", JSONObject.NULL);
            }
            
            if(this.CARTELA != null){
                this.JSON.put("CARTELA", this.CARTELA);
            }else{
                this.JSON.put("CARTELA", JSONObject.NULL);
            }
            
        }catch(JSONException e){
            System.out.println("Erro ao transformar objeto JSON para str: " + e);
        }
        
        return this.JSON.toString();
    }
    
    // Fazer com que os campos sejam tratados independemente, campos null aparecem na msg a ser enviada
    // usar obj.has(KEY) para testar os campos existentes
    public void fromStr(String str){
        //System.out.println("fromString to Mensagem");
        
        try{
            
            if( this.JSON.has("COD") ) {
                if(!this.JSON.get("COD").toString().equals("null")){
                    this.COD = this.JSON.getString("COD");
                    
                }else{
                    this.COD = "null";
                }
            }
            
            if( this.JSON.has("NOME") ) {
                if(!this.JSON.get("NOME").toString().equals("null")){
                    this.NOME = this.JSON.getString("NOME");
                
                }else{
                    this.NOME = "null";
                }
            }
            
            if( this.JSON.has("MSG") ) {
                if(!this.JSON.get("MSG").toString().equals("null")){
                    this.MSG = this.JSON.getString("MSG");
                    
                }else{
                    this.MSG = "null";
                }
            }
            
            if(this.JSON.has("STATUS") ) {
                if(!this.JSON.get("STATUS").toString().equals("null")){
                    this.STATUS = this.JSON.getString("STATUS");
                    
                }else{
                    this.STATUS = "null";
                }
            }
            
            if(this.JSON.has("LISTACLIENTE") ){
                
                if(!this.JSON.get("LISTACLIENTE").toString().equals("null")){
                    this.LISTACLIENTE = this.JSON.getJSONArray("LISTACLIENTE");
                
                }else{
                    this.LISTACLIENTE = null;
                }
            }
            
            if(this.JSON.has("CARTELA") ){
                
                if(!this.JSON.get("CARTELA").toString().equals("null")){
                    this.CARTELA = this.JSON.getJSONArray("CARTELA");
                
                }else{
                    this.CARTELA = null;
                }
            }
            
        }catch(JSONException e){
            System.out.println("Erro na leitura de string para objeto JSON: " + e);
        }
        
    }
    
    public void fromJSON(JSONObject obj){
        //System.out.println("fromJSON to Mensagem");
        
        try{
            
            if(this.JSON.has("COD"))
                this.COD = obj.getString("COD");

            if(this.JSON.has("NOME"))
                this.NOME = obj.getString("NOME");
            
            if(this.JSON.has("MSG"))
                this.MSG = obj.getString("MSG");
            
            if(this.JSON.has("STATUS"))        
                this.STATUS = obj.getString("STATUS");
            
            if(this.JSON.has("LISTACLIENTE"))
                this.LISTACLIENTE = obj.getJSONArray("LISTACLIENTE");
            
            if(this.JSON.has("CARTELA"))
                this.CARTELA = obj.getJSONArray("CARTELA");
            
        }catch(JSONException e){
            System.out.println("Erro na leitura de objeto JSON para objeto JSON.");
        }
        
    }
}
