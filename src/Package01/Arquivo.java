package Package01;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//**********************************************************************************************************************
//                                                                                                                     *
// Nome da Classe: Arquivo                                                                                             *
//                                                                                                                     *
// Atributos: Se o Arquivo Existe (boolean), Tamanho em Caracteres ou Bytes (int), Tipo do Arquivo (código MIME)       *
//                                                                                                                     *
// Métodos: Lê arquivo texto (sequência de caracteres), Lê arquivo binário (sequência de bytes), escreve               *
//          arquivo texto                                                                                              *
//                                                                                                                     *
//**********************************************************************************************************************
//
public class Arquivo {
    boolean Existe(String Caminho, String NomeArquivo) {
        File Arquivo = new File(Caminho + NomeArquivo);
        return (Arquivo.exists());
    }

    int Tamanho(String Caminho, String NomeArquivo) {
        File Arquivo = new File(Caminho + NomeArquivo);
        return ((int)Arquivo.length());
    }

    String LeTexto(String Caminho, String NomeArquivo) {
        File Arquivo = new File(Caminho + NomeArquivo);
        String Arq = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(Arquivo));

            String st;
            while ((st = br.readLine()) != null) {
                Arq = Arq + st + "\n";
            }
        } catch (IOException e) {
            return("erro");
        }
        return(Arq);
    }

    byte[] LeByte(String Caminho, String NomeArquivo) throws IOException {
        File Arquivo = new File(Caminho + NomeArquivo);
        FileInputStream ArquivoLido = null;
        int TamArquivo = (int)Arquivo.length();
        byte[] DadosArquivo = new byte[TamArquivo];

        try {
            ArquivoLido = new FileInputStream(Arquivo);
            ArquivoLido.read(DadosArquivo);
        } finally {
            if (ArquivoLido != null)
                ArquivoLido.close();
        }
        return DadosArquivo;
    }

    //******************************************************************************************************************
    // Nome do Método: EscreveArqTxt()                                                                                 *
    //	                                                                                                               *
    // Funcao: escreve um arquivo texto                                                                                *
    //                                                                                                                 *
    // Entrada: string com o nome do caminho e do arquivo e texto a ser escrito no arquivo                             *
    //                                                                                                                 *
    // Saida: = 1 leu arquivo / = 0 falha ao ler o arquivo                                                             *
    //	                                                                                                               *
    //******************************************************************************************************************
    //
    public static boolean EscreveArqTxt(String Caminho, String NomeArquivo, String Texto, boolean Verbose) {

        try {
            PrintWriter out = new PrintWriter(new FileWriter(Caminho + NomeArquivo));
            out.write(Texto);
            out.close();
            return (true);
        } catch (IOException e) {
            if (Verbose) {
                System.out.print("Erro ao escrever arquivo texto ");
            }
            return (false);
        }
    }

    String Tipo(String NomeArquivo) {
        String tipo = "";

        if (NomeArquivo.endsWith(".htm")  ||  NomeArquivo.endsWith(".html")) {
            tipo = "text/html";
        }

        if (NomeArquivo.endsWith(".js")) {
            tipo = "text/javascript";
        }

        if (NomeArquivo.endsWith(".css")) {
            tipo = "text/css";
        }

        if (NomeArquivo.endsWith(".jpg")  ||  NomeArquivo.endsWith(".jpeg")) {
            tipo = "image/jpeg";
        }

        if (NomeArquivo.endsWith(".gif")) {
            tipo = "image/gif";
        }

        if (NomeArquivo.endsWith(".png")) {
            tipo = "image/png";
        }

        if (NomeArquivo.endsWith(".bmp")) {
            tipo = "image/bmp";
        }

        if (NomeArquivo.endsWith(".txt")) {
            tipo = "text/plain";
        }

        return(tipo);
    }
}
