/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.*;
/**
 *
 * @author joao
 */
public class serverTCP {

    public static void error(String msg)
	{
		System.err.println("ERRO: " + msg);
		System.exit(1);
	}

	public static void main(String argv[]) throws IOException
	{
	ServerSocket server = null;
	Socket client = null;
	PrintWriter out = null;
        BufferedReader in = null;
        boolean listening = true;

	int port = 9999;

	if (argv.length < 1) {
		System.err.println("Usage: servidor <porta> ");
		System.exit(1);
	}
	try {
		port = Integer.parseInt(argv[0]);
	} catch(NumberFormatException e) {
		System.err.println(" <porta> inteiro invalido: " + e);
		System.exit(1);
	}

	// Criar o socket
	try {
		server = new ServerSocket(port);
	} catch (IOException e) {
		error(" Abrir o socket: " + e);
	}

	// Aceita um pedido...
	while (listening)
	{
            try{
                client = server.accept();
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(),true);
            } catch (IOException e) {
        	error(" Aceitar a ligação " + e);
            }
            String mensagem;
            mensagem = in.readLine();
            while (!mensagem.equals("byeServidor") && !mensagem.equals("byeCliente")) {
		System.out.println(mensagem);
		out.println("MENSAGEM RECEBIDA: " + mensagem);
		mensagem = in.readLine();
            }
            if (mensagem.equals("byeCliente")) {
                System.out.println(mensagem);
                out.close();
                in.close();
                client.close();
            }
            if (mensagem.equals("byeServidor")) {
                listening = false;
            }
        }
        out.close();
        in.close();
        client.close();
        server.close();
    }
}
