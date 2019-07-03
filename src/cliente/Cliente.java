package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

import org.json.*;
import projetoSD.*;

public class Cliente {
    static String serverIP;
    static int serverPort;
    
    static ClienteGUI gui;
    static String NOME;
    static JSONArray LISTACLIENTE;
    static JSONArray LISTAPRONTO;
    static JSONArray CARTELA;

    public static void main(String[] args) throws IOException {
        InputStream is;
        InputStreamReader isReader;
        BufferedReader buffReader;
        OutputStream os;
        Writer osWriter;
        BufferedWriter buffWriter;
        
        //Geração do socket
        Socket ClientSocket = null;
        
        ContaTempo timer = new ContaTempo(0);
        timer.start();

        //Rotina para entrada de dados via teclado     
        Scanner input = new Scanner(System.in);
        gui = new ClienteGUI();
        //gui.setAlwaysOnTop(true);
        gui.setVisible(true);
        
        try {

            serverIP = gui.serverIP;
            System.out.println("serverIP: " + serverIP); 

            serverPort = gui.serverPort;
            System.out.println("serverPort: " + serverPort);             
            
            ClientSocket = new Socket(serverIP, serverPort);
            /* associa um buffer de entrada e outro de saida ao socket */
            is = ClientSocket.getInputStream();
            isReader = new InputStreamReader(is);                    // aponta o duto de entrada para o socket do cliente
            buffReader = new BufferedReader(isReader);
            
            os = ClientSocket.getOutputStream();
            osWriter = new OutputStreamWriter(os);
            buffWriter = new BufferedWriter(osWriter);       // aponta o duto de saída para o socket do cliente

            //System.out.println("Buffers defined");
            //aguarda uma digitação pelo teclado para enviar ao servidor
            //System.out.println(in);

            
            System.out.print("\nPorta usada pelo CLIENTE: ");
            System.out.println(ClientSocket.getLocalPort()+"\n");
            
            
            while (true) {
                
                if(buffReader.ready() == true){
                    if(timer.getCount() <= 0){
                        timer.counting = false;
                    }
                    //System.out.println("in ready.");
                    String received = buffReader.readLine();
                    System.out.println("CLIENTE <- " + received);
                    try{
                        Mensagem msg = new Mensagem(received);
                        
                        switch(msg.COD){
                            case "rlogout": {
                                //System.out.print("rlogout");
                                buffWriter.close();
                                os.close();
                                buffReader.close();
                                is.close();
                                System.out.println("Cliente Encerrado.");
                                System.exit(0);
                                
                                break;
                            }
                            
                            case "rlogin": {
                                refreshGUI("rlogin", msg);
                                break;
                            }
                            
                            case "rpronto":{
                                refreshGUI("rpronto", msg);
                                break;
                            }
                            
                            case "rbingo":{
                                break;
                            }
                            
                            case "lista": {
                                LISTACLIENTE = msg.LISTACLIENTE;
                                refreshGUI("lista", msg);
                                break;
                            }
                            
                            case "listapronto":{
                                LISTAPRONTO = msg.LISTACLIENTE;
                                refreshGUI("listapronto", msg);
                                break;
                            }
                            
                            case "chat":{
                                refreshGUI("chat", msg);
                                break;
                            }
                            
                            case "tempo":{
                                timer.counting = true;
                                timer.setCount(30);
                                break;
                            }
                            
                            case "cartela":{
                                refreshGUI("cartela", msg);
                                break;
                            }
                            
                            case "sorteado":{
                                refreshGUI("sorteado", msg);
                                JOptionPane.showConfirmDialog(null, "Marca o Nr. " + msg.CARTELA.getInt(0) + "?");
                                gui.markTable(msg.CARTELA.getInt(0));
                                break;
                            }
                            
                            
                            default:
                                break;
                        }
                        
                    }catch(JSONException e){
                        System.out.println("Erro recebimento msg: " + e);
                    }
                }
                
                
                if(gui.getToSend() == true){
                    Mensagem msg = new Mensagem();
                    msg.COD = gui.getCOD();
                    
                    switch(gui.getCOD()){
                        case "logout":{
                            msg.NOME = NOME;
                            break;
                        }
                        case "login":{
                            NOME = gui.getMsgContent().getText();
                            msg.NOME = NOME;
                            break;
                        }
                        
                        case "chat":{
                            msg.NOME = NOME;
                            msg.MSG = gui.getMsgContent().getText();
                            msg.STATUS = gui.getSTATUS();
                            
                            // Corrige a lista de cliente (coloca endereco e porta)
                            if(msg.STATUS.equals("uni")){
                                int i = 0;

                                // Encontra o indice do cliente selecionado
                                while(i < LISTACLIENTE.length() && 
                                      !LISTACLIENTE.getJSONObject(i).getString("NOME")
                                              .equals(gui.getLISTACLIENTE().getJSONObject(0).getString("NOME"))
                                ) i++;
                                
                                msg.LISTACLIENTE = new JSONArray()
                                        .put(LISTACLIENTE.getJSONObject(i));
                            
                                gui.getChatArea().append(
                                        NOME + 
                                        " -> " + gui.getLISTACLIENTE().getJSONObject(0).getString("NOME") + 
                                        ": " + msg.MSG + "\n");
                            }
                            
                            gui.getMsgContent().setText("");
                            
                            break;
                        }
                        
                        case "pronto":{
                            msg.COD = gui.getCOD();
                            msg.NOME = NOME;
                            msg.STATUS = gui.getSTATUS();
                            
                            break;
                        }
                        
                        case "marca":{
                            msg.COD = gui.getCOD();
                            msg.NOME = NOME;
                            msg.STATUS = gui.getSTATUS();
                            msg.CARTELA = gui.getCARTELA();
                            
                            break;
                        }
                        
                        case "bingo":{
                            break;
                        }
                            
                            
                        
                        default:
                            break;
                    }
                    
                    System.out.println("CLIENTE -> " + msg.toStr());
                    buffWriter.write(msg.toStr() + "\r\n");
                    buffWriter.flush();
                    gui.setToSend(false);
                }
                Thread.sleep(100);
                gui.timerPane.setText(String.valueOf(timer.getCount()));
                
            }
        } catch (UnknownHostException e) {
            System.err.println("Host desconhecido: ");
            System.exit(1);
            
        } catch (IOException e) {
            System.err.println("IP|Porta|Comando não existe ");
            System.exit(1);
            
        } catch (Exception e) {
            System.out.println("Falha na conexão com o servidor: " + e);
        }

    }
    
    public static void refreshGUI(String COD, Mensagem msg){
        switch(COD){
            case "rlogin":{
                if(msg.JSON.has("STATUS") && msg.JSON.getString("STATUS").equals("sucesso")){
                    gui.getMsgLabel().setText("Digite abaixo o texto da mensagem:");
                    gui.getMsgContent().setText("");
                    gui.getSendMsg().setText("Enviar");
                    gui.getLoggedLabel().setText(NOME);
                    gui.statusLabel.setForeground(new java.awt.Color(3, 102, 0));
                    gui.sendGame.setText("Entrar no jogo");
                    
                    gui.setLogged(true);
                    gui.getClientList().setEnabled(true);
                    gui.getChatArea().setEnabled(true);
                    gui.getSendLogout().setEnabled(true);
                    
                }
                break;
            }
            
            case "lista":{
                System.out.println("lista");
                gui.getModelClientList().clear();
                
                try{
                    for(int i = 0; i < msg.LISTACLIENTE.length(); i++){
                        gui.getModelClientList().addElement(msg.LISTACLIENTE.getJSONObject(i).getString("NOME"));
                    }
                    gui.getClientList().setModel(gui.getModelClientList());
                }catch(JSONException e){
                    System.out.println("Erro ao listar clientes: " + e);
                }
                break;
            }
            
            case "chat":{
                if(msg.JSON.has("LISTACLIENTE") && msg.JSON.getJSONArray("LISTACLIENTE").length() > 0){
                    if(msg.JSON.getString("STATUS").equals("uni")){
                        gui.getChatArea().append(msg.LISTACLIENTE.getJSONObject(0).getString("NOME") + "(privado): " + msg.MSG + "\n");
                
                    }else{
                        gui.getChatArea().append(msg.LISTACLIENTE.getJSONObject(0).getString("NOME") + ": " + msg.MSG + "\n");
                
                    }
                    
                }
                break;
            }
            
            case "rpronto":{
                if(msg.STATUS != null && msg.STATUS.equals("falha")){
                    gui.sendGame.setText("Entrar no jogo");
                }
                break;
            }
            
            case "listapronto":{
                System.out.println("listapronto");
                String aux = "";
                
                if(LISTAPRONTO != null && LISTAPRONTO != JSONObject.NULL){
                    try{
                        for(int i = 0; i < LISTAPRONTO.length(); i++){
                            aux = aux.concat(LISTAPRONTO.getJSONObject(i).getString("NOME").concat("\n"));
                        }

                    }catch(JSONException e){
                        System.out.println("Erro ao listar prontos: " + e);
                    }
                }
                
                gui.readyList.setText(aux);
                
                break;
            }

            case "tempo":{
                
                break;
            }

            case "cartela":{
                CARTELA = msg.CARTELA;
                gui.refreshTable(CARTELA);
                
                break;
            }

            case "sorteado":{
                gui.lottery(msg);
                
                break;
            }
            
            default:
                break;
        }
    }
}
