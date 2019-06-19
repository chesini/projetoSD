package servidor;

// servidor de eco
// recebe uma linha e ecoa a linha recebida.

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.JSONArray;

public class Servidor
{
    static ServidorGUI gui;
    
    private static int serverPort = 0;// porta usada para conexao.
    private static ServerSocket echoServer;// cria o socket do servidor
    
    
    public static boolean initServer(){
        
        try
        {
            System.out.println("serverPort: " + serverPort);
            echoServer = new ServerSocket(serverPort);  // *** socket() + bind()  // instancia o socket do servidor na porta definida. 
            return true;

        } catch (IOException e)
        {
            System.out.println("Port error: "+e);
            return false;
        }
    }
    
   public static void main(String args[])
   {
        gui = new ServidorGUI();
        gui.setVisible(true);
       
        serverPort = gui.serverPort;
        initServer();

        Socket clientSocket = null;         // cria o socket do cliente
        ArrayList<Socket> socketArray = new ArrayList();
        JSONArray cliArray = new JSONArray();
        //JSONArray = readyArray = new JSONArray();
        int i = 0;

        while (true)
        {
            try {
                System.out.println("Aguardando conexao na porta " + echoServer.getLocalPort());
                // Aguarda conexão na porta de entrada
                clientSocket = echoServer.accept();        // *** listen() + accept() // aguarda conexão do cliente
                
                // Adiciona o cliente atual na lista de sockets do servidor
                socketArray.add(clientSocket);
                i = socketArray.size() - 1; // indexa o SocketArray e cliArray
                
                Connection c = new Connection(gui, i, clientSocket, cliArray, socketArray);
                System.out.println("Conectado com " + clientSocket.getRemoteSocketAddress());            
                //echoServer.close();

            } catch (IOException e) {
               System.out.println(e);
            }
        }
   } // main
} // classe