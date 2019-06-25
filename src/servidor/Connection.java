package servidor;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.TimerTask;
import projetoSD.*;

        
/**
 *
 * @author Leandro
 * Classe Connection instancia uma conexao com Cliente via socket TCP/IP
 * 
 */
public class Connection extends Thread {
    ServidorGUI gui;
    Socket clientSocket;
    String line;                        // string para conter informações transferidas
    InputStream is;
    InputStreamReader isReader;
    BufferedReader buffReader;
    OutputStream os;
    Writer osWriter;
    BufferedWriter buffWriter;

    JSONArray objArray;
    ArrayList<Socket> sktArray;
    JSONArray readyArray;
    JSONArray CliOrigem;
  
    ContaTempo ct = new ContaTempo(-1);
    public Thread t = new Thread (ct);
    //public Thread t = new ContaTempo(-1);
    //public static Thread timex = new ContaTempo(-1);

    
    // Variaveis para o jogo
    Bingo game;
    
    
    public Connection( ServidorGUI gui, Socket aClientSocket, JSONArray cliArray, ArrayList<Socket> socketArray, JSONArray readyArray, Bingo mainGame ) {
        this.gui = gui;
        clientSocket = aClientSocket;
        objArray = cliArray;
        sktArray = socketArray;
        this.readyArray = readyArray;
        t.start();
        try{
               
            is = aClientSocket.getInputStream();
            isReader = new InputStreamReader(is);
            buffReader = new BufferedReader(isReader);

            os = aClientSocket.getOutputStream();
            osWriter = new OutputStreamWriter(os);
            buffWriter = new BufferedWriter(osWriter);
            
            this.start();            
            
        }catch(IOException e){
            System.out.println("Connection IOException: " + e);
        }
    }

    @Override
    public void run(){
        
        try{
            //System.out.println("try");
            while (true)
            {
                //System.out.println("ready: " + is.ready());
                if(buffReader.ready() == true){
                    line = buffReader.readLine(); // *** recv()  // recebe dados do cliente
                    gui.refreshGUI('i', line);
                    
                    Mensagem msgRec = new Mensagem(line);
                    parse(msgRec);
                    
                    
               }
               Thread.sleep(200);
               
               // Operacoes relacionadas ao jogo
               /*
               if(isPlaying){
               }else{
               }
               */
               
            }
        }catch(IOException e){
            System.out.println("Connection IOException: " + e);
        }catch(Exception e){
            System.out.println("Connection Exception: " + e);
        }finally{
            System.out.println("Connection finally");
            try{
                clientSocket.close();
            }catch(IOException e){
                System.out.println("Connection finally" + e);
                
            }
        }
    }
        
    /*
     * Faz a identificacao das mensagem recebida e o devido retorno
     */
    private Mensagem parse(Mensagem msgRec) throws IOException{
        Mensagem retorno = new Mensagem();
        
        if(msgRec.COD.equals("login")){
            if(msgRec.NOME.length() > 0){
                retorno.COD = "rlogin";
                retorno.STATUS = "sucesso";

                buffWriter.write(retorno.toStr() + "\r\n");
                buffWriter.flush();
                gui.refreshGUI('o', retorno.toStr());
                
                MandarListaCli(msgRec, 1); // adiciona cliente na lista de clintes e envia broadcast
            }else{
                // retorna para o cliente com falha no login
                retorno.COD = "rlogin";
                retorno.STATUS = "falha";
                buffWriter.write(retorno.toStr() + "\r\n");
                buffWriter.flush();
                gui.refreshGUI('o', retorno.toStr());
            }
        }
                    
        if(msgRec.COD.equals("logout")){
            retorno.COD = "rlogout";
            retorno.STATUS = "sucesso";

            buffWriter.write(retorno.toStr() + "\r\n");
            buffWriter.flush();
            gui.refreshGUI('o', retorno.toStr());
            
            MandarListaCli(msgRec, 2); //remove da lista de clientes e a envia broadcast
        }

        if (msgRec.COD.equals("chat")){
            retorno.COD = "chat";

            CliOrigem = new JSONArray();
            CliOrigem.put(
                new JSONObject()
                        .put("NOME",msgRec.NOME)
                        .put("IP", clientSocket.getInetAddress().getHostAddress())
                        .put("PORTA",String.valueOf(clientSocket.getPort()))
            );
            
            retorno.LISTACLIENTE = CliOrigem;

            if(msgRec.STATUS.equals("uni")){
                retorno.MSG = msgRec.MSG; 
                retorno.STATUS = msgRec.STATUS;
                sendUnicast(retorno, msgRec.LISTACLIENTE); //Fazer a funcao de enviar unicast

            }else if (msgRec.STATUS.equals("broad")){
                retorno.MSG = msgRec.MSG; 
                retorno.STATUS = msgRec.STATUS;
                sendBroadcast(retorno);                            
            }
        }
       
        if (msgRec.COD.equals("pronto")){
            int k = 0;
            System.out.println("entra pronto");
             
            // Atualiza a lista de prontos em Servidor
            if(msgRec.STATUS.equals("sucesso")){
                try{

                    retorno.COD = "rpronto";
                    retorno.STATUS = "sucesso";

                    buffWriter.write(retorno.toStr() + "\r\n");
                    buffWriter.flush();
                    gui.refreshGUI('o', retorno.toStr());
                  System.out.println("startou");
                ct.setCount(30);
                System.out.println(ct.getCount());

                }catch(JSONException e){
                    
                }

            }else if(msgRec.STATUS.equals("falha")){
                try{
                    k = 0;
                    while(k < this.readyArray.length() &&
                          !this.readyArray.getJSONObject(k).getString("NOME").equals(msgRec.NOME)
                    ) k++;

                    this.readyArray.remove(k);
                  
                  ct.setCount(-1);
                System.out.println(ct.getCount());
                System.out.println("sai falha");
                /*if(this.readyArray.length()>0){// se ainda tem jogador na lista de pronto, reseta o timer
                    //ContaTempo.setCount(30);
                    t.stop();
                    ct.setCount(30);
                    t.start();
                }else if(this.readyArray.length()==0){ // se nao tem mais jogador na lista de pronto, para o timer
                    t.interrupt();
                    ct.setCount(-1);
                    //t.start();
                }*/
                    
                    retorno.COD = "rpronto";
                    retorno.STATUS = "sucesso";

                    buffWriter.write(retorno.toStr() + "\r\n");
                    buffWriter.flush();
                    gui.refreshGUI('o', retorno.toStr());
                }catch(JSONException e){
                    
                }
            }
            
            // Atualiza a lista de prontos na GUI
            String aux = "";
            for(k = 0; k < readyArray.length(); k++){
                aux = aux.concat(this.readyArray.getJSONObject(k).getString("NOME").concat("\n"));
                
            }
            gui.refreshGUI('p', aux);
            
            // Manda a lista de prontos broadcast
            retorno.COD = "listapronto";
            retorno.STATUS = "null";
            retorno.LISTACLIENTE = readyArray;
            
            sendBroadcast(retorno);
            
            
            //System.out.println("terminou contagem");
            
        }
        
        if (msgRec.COD.equals("marca")){
        }
        
        if (msgRec.COD.equals("bingo")){
        }
        
        return retorno;
    }
    /*private int ContaTempo(int count){
        timer = new java.util.Timer(); //new timer
        task = new TimerTask() {
            int aux = count;
            public void run() {                
                System.out.println(Integer.toString(aux)); 
                //System.out.println(isIt);
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
       return 1;
    }*/
    private void MandarListaCli(Mensagem msgRec, int op){
        Mensagem msgSend = new Mensagem();
        int i = 0;
        
        
        try{
            //conectou
            if (op == 1){
               
                while(i < sktArray.size() && 
                      i < objArray.length() &&
                      objArray.getJSONObject(i).getString("IP").equals(sktArray.get(i).getInetAddress().getHostAddress())
                ) i++;
                    
                JSONObject newObj = new JSONObject()
                                .put("NOME",msgRec.NOME)
                                .put("IP", clientSocket.getInetAddress().getHostAddress())
                                .put("PORTA",String.valueOf(clientSocket.getPort()));

                objArray.put(i, newObj); 
            }
            
            //desconectou
            if(op == 2){
                System.out.println("Usuario " + msgRec.NOME + " desconectou!");
                
                // Remove usuario da lista de usuarios
                i = 0;
                while(i < objArray.length() && msgRec.NOME.equals(objArray.getJSONObject(i).getString("NOME")) != true)
                    i++;
                objArray.remove(i);
                
                // Remove usuario da lista de prontos no servidor
                i = 0;
                while(i < readyArray.length() && msgRec.NOME.equals(readyArray.getJSONObject(i).getString("NOME")) != true)
                    i++;
                readyArray.remove(i);
                
                // Remove cliente da lista de sockets
                i = 0;
                while(i < sktArray.size() && sktArray.get(i) != clientSocket)
                    i++;
                sktArray.remove(i);
                
            }
            
            String aux = "";
            for(i = 0; i < objArray.length(); i++){
                aux = aux.concat(objArray.getJSONObject(i).getString("NOME").concat("\n"));
            }
            gui.refreshGUI('c', aux);
            
        }catch(JSONException e){
            System.out.println("Erro ao alterar Lista de clientes: " + e);
        
        }catch(IndexOutOfBoundsException e){
            System.out.println("Erro ao remover cliente no servidor: " + e);
        }
        
        // Envia a lista de clientes broadcast
        try{
            msgSend.COD = "lista";
            msgSend.LISTACLIENTE = objArray;
            
            sendBroadcast(msgSend);
            
            msgSend = new Mensagem();
            msgSend.COD = "listapronto";
            msgSend.LISTACLIENTE = readyArray;
            
            sendBroadcast(msgSend);

        }catch (IOException e) {
                System.err.println("Erro ao enviar lista de clientes");
                System.exit(1);
        }
    }
    
    public void sendUnicast(Mensagem msg, JSONArray destino) throws IOException{
        System.out.println("Unicast");
        int k = 0;
        if (destino.length() == 1){
            System.out.println("DESTINO:: " + destino.get(0));
            try{
                
                // Encontra o indice do destino
                while(k < objArray.length() && 
                      !objArray.getJSONObject(k).getString("NOME").equals(destino.getJSONObject(0).getString("NOME"))
                )k++;
                
                OutputStream osAux = sktArray.get(k).getOutputStream();
                Writer osWriterAux = new OutputStreamWriter(osAux);
                BufferedWriter buffWriterAux = new BufferedWriter(osWriterAux);

                System.out.println("SERVIDOR -> " + sktArray.get(k).getRemoteSocketAddress() + " :" + msg.toStr());                    
                buffWriterAux.write(msg.toStr() + "\r\n");
                buffWriterAux.flush();
                gui.refreshGUI('o', msg.toStr());

            }catch(IOException e){
                // Mexer nisso aqui pra remover da lista quando da erro de conexao (cliente fecha errado)
                System.out.println("Erro ao enviar unicast: " + e);
                removeDisconnected(k);

                //System.out.println("Cliente com conexão interrompida, removendo..");
                //this.sktArray.remove(k);
                //this.objArray.remove(k);
            
            }catch(IndexOutOfBoundsException e){
                // Encontra o cliente que está desconectado
                System.out.println("Erro ao enviar unicast: " + e);
                removeDisconnected(k);

                //System.out.println("Cliente com conexão interrompida, removendo.. " + e);
                //MandarListaCli(null, 0);
            }
            
        }
    }
    public void sendBroadcast(Mensagem msg) throws IOException{
        System.out.println("Broadcast");
        
        if(sktArray.size() > 0){
            try{
                for(int i = 0; i < sktArray.size(); i++){
                    try{
                        
                        OutputStream osAux = sktArray.get(i).getOutputStream();
                        Writer osWriterAux = new OutputStreamWriter(osAux);
                        BufferedWriter buffWriterAux = new BufferedWriter(osWriterAux);       // aponta o duto de saída para o socket do cliente

                        System.out.println("SERVIDOR -> " + sktArray.get(i).getRemoteSocketAddress() + " :" + msg.toStr());                    
                        buffWriterAux.write(msg.toStr() + "\r\n");
                        buffWriterAux.flush();
                        gui.refreshGUI('o', msg.toStr());

                    }catch(IOException e){
                        // Mexer nisso aqui pra remover da lista quando da erro de conexao (cliente fecha errado)
                        System.out.println("Erro ao enviar broadcast: " + e);
                        //System.out.println("Cliente com conexão interrompida, removendo..");
                        removeDisconnected(i);
                    }
                }
            }catch(JSONException e){
                System.out.println("Erro ao enviar broadcast: " + e);
                
            }
        }
    }
    void removeDisconnected(int k){
        try{
            // Remove o cliente das listas de sockets, objetos JSON e prontos
            System.out.println("Removendo cliente desconectado");

            // Percorre a lista de prontos e procura um nome igual ao do
            // cliente que deu erro
            for(int j = 0; j < readyArray.length(); j++){
                if(readyArray.getJSONObject(j).getString("NOME")
                        .equals(objArray.getJSONObject(k).getString("NOME"))){

                    readyArray.remove(j);
                }
            }

            objArray.remove(k);
            sktArray.remove(k);
            

            MandarListaCli(null, 0);
            //this.interrupt();
            
        }catch(JSONException e){
            System.out.println("Erro na remocao de cliente desconectado: " + e);
            
        }catch(IndexOutOfBoundsException e){
            System.out.println("Erro na remocao de cliente desconectado: " + e);
            
        }
    }

}
