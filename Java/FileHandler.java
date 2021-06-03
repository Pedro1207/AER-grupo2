import java.io.*;

public class FileHandler {
	
	/**
	 * Variável que guarda o path para o ficheiro
	 * sob responsabilidade do filehandler
	 */
	private String path;
	
	/**
	 * Construtor para objetos da classe FileHandler
	 * @param path
	 */
	public FileHandler(String path){
		this.path = path;
	}
	
	/**
	 * Método que permite ler um conjunto específico
	 * de bytes de um ficheiro
	 * @param from
	 * @param to
	 * @return
	 */
	public byte[] readBytes(int from, int to)
			throws IOException {

		if(View.debug) System.out.println("Read from file from byte" + from + " to byte " + to);
		FileInputStream in = new FileInputStream(path);
		int diff = to-from, how;
		byte[] content = new byte[diff];
		in.skip(from);
		how = in.read(content,0,diff);
		in.close();
		return content;
	}
	
	/**
	 * Método que permite escrever um array de bytes
	 * para um ficheiro, com um determinado inicio e fim
	 * @param content
	 * @param from
	 * @param to
	 */
	public void writeBytes(byte[] content, int from, int to)
			throws IOException{

		if(View.debug) System.out.println("Writing to file from byte" + from + " to byte " + to);

		RandomAccessFile raf = new RandomAccessFile(path,"rw");
		int diff = to-from;
		raf.seek(from);
		raf.write(content,0,diff);
	}
	
}
