import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class View {

    public static int menu() throws IOException {

        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        System.out.println("\n***********************************************************");
        System.out.println("************************P2P-NETWORK************************");
        System.out.println("\n***************************MENU**************************\n");

        System.out.println("[1] -> Pesquisa por ficheiros ");
        System.out.println("[2] -> Sair \n");

        while(true){
            System.out.print("Insira a opção: ");
            String line = bufferedReader.readLine();
            try{
                int opcao = Integer.parseInt(line);
                if(opcao != 1 && opcao != 2){
                    System.out.println("Opção inválida.");
                }
                else{
                    return opcao;
                }
            } catch (NumberFormatException e){
                System.out.println("Opção inválida.");
            }
        }
    }

    public static String menuPesquisa() throws IOException {
        InputStreamReader streamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(streamReader);


        System.out.println("\n***********************************************************");
        System.out.println("************************P2P-NETWORK************************");
        System.out.println("\n*************************PESQUISA*************************\n");

        System.out.print("Insira o termo a pesquisar: ");

        return bufferedReader.readLine();
    }
}
