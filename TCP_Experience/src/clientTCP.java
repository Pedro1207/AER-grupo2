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
public class clientTCP {

	public static void error(String msg)
	{
		System.err.println("ERRO: " + msg);
		System.exit(1);
	}

	public static void main(String[] argv) throws IOException
	{
		Socket socket = null;
		PrintWriter out = null;
                BufferedReader in = null;
		BufferedReader Stdin = new BufferedReader(new InputStreamReader(System.in));
		String hostname = "2001:0::1";
		int port = 9999;

		if (argv.length < 2) {
			System.err.println("Usage: client <servidor> <porta> ");
			System.exit(1);
		}
		hostname = argv[0];
		try {
			port = Integer.parseInt(argv[1]);
		} catch(NumberFormatException e) {
			System.err.println(" <porta> inteiro invalido: " + e);
			System.exit(1);
		}

		// Abre o socket
		try {
			socket = new Socket(hostname, port);
			out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	} catch (UnknownHostException e) {
			error("Host '" + hostname + "' desconhecido");
		} catch (IOException e) {
			error("Falha na conexao");
		}

		// Envia mensagem e recebe resposta...
		System.out.print("Introduza as mensagens (byeCliente para " +
                        "terminar cliente, byeServidor para terminar servidor)\n");
		String mensagem = Stdin.readLine();
		while (!mensagem.equals("byeCliente") && !mensagem.equals("byeServidor")) {
	    		out.println(mensagem);
	    		System.out.println(in.readLine());
	    		mensagem=Stdin.readLine();
		}
                out.println(mensagem);
		out.close();
		in.close();
		Stdin.close();
		socket.close();
    	}
}
