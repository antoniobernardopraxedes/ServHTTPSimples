package Package01;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

//**********************************************************************************************************************
//                                                                                                                     *
// Autor: Antonio Bernardo de Vasconcellos Praxedes                                                                    *
//                                                                                                                     *
// Data: 19/08/2021                                                                                                    *
//                                                                                                                     *
// Nome da Classe: Main - Projeto ServHTTPSimples                                                                      *
//                                                                                                                     *
// Funcao: Programa Principal Servidor HTTP para ser instalado no Servidor em Nuvem                                    *
//                                                                                                                     *
//**********************************************************************************************************************
//
public class Main implements Runnable {

    static boolean Verbose = true;
    static boolean Local = false;  // true = Servidor roda na Nuvem / false = Servidor roda na Intranet
    static String CaminhoNuvem = "/home/bernardo/Executavel/";               // Caminho no Computador na Nuvem
    static String CaminhoLocal1 = "/home/antonio/Workspace/Cloud/";          // Caminho no Computador Dell Vostro
    static String CaminhoLocal2 = "/home/antonio/ServHTTPSimples/Recursos/"; // Caminho no Computador Dell Inspiron
    static String CaminhoLocal3 = "/home/pi/Desktop/Programas/";  // Caminho no Computador Raspberry PI 3 (oficina)
    static int Porta = 8080;

    static String Caminho = "";
    static String MsgXML = "";
    private Socket connect;
    private static String ComRec;

    public Main(Socket c) {
        connect = c;
    }

    //******************************************************************************************************************
    //                                                                                                                 *
    // Método Executavel da ClasseHTTPSrvSup                                                                           *
    //                                                                                                                 *
    // Funcao: Servidor HTTP aguarda a conexão do Cliente                                                              *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    public static void main(String[] args) {
        Mensagem.IniciaVarGlobais();
        Mensagem.PrtMsg = false;
        ComRec = "cmd=0000";

        MsgXML = Mensagem.XML01Falha(0);

        try {
            ServerSocket serverConnect = new ServerSocket(Porta);
            InetAddress ip = InetAddress.getLocalHost();
            String NomeComputador = ip.getHostName();

            if (NomeComputador.equals("antonio-Vostro1510")) {
                Caminho = CaminhoLocal1;
            }
            else {
                if (NomeComputador.equals("BernardoLinux")) {
                    Caminho = CaminhoLocal2;
                }
                else {
                    Caminho = CaminhoNuvem;
                }
            }

            String MsgTrm = "Servidor Iniciado no Computador " + NomeComputador + " - Caminho: " + Caminho;
            Util.Terminal(MsgTrm, true, true);
            Util.Terminal("Esperando por Conexoes na Porta: " + Porta, false, Verbose);

            do {    // Espera a conexão do cliente
                Main myServer = new Main(serverConnect.accept());
                Util.Terminal("Conexao Aberta com o Cliente (" + new Date() + ")", false, Verbose);
                Thread thread = new Thread(myServer);      // Thread para gerenciar a conexão do cliente
                thread.start();
            } while (true);
        } catch (IOException e) {
            System.err.println("Erro na Conexao com o Servidor: " + e.getMessage());
        }
    } // Fim da Rotina public static void main(String[] args) {


    //******************************************************************************************************************
    //                                                                                                                 *
    // Rotina que Processa a Solicitação do Cliente da ClasseHTTPSrvSup                                                *
    //                                                                                                                 *
    // Funcao: processa a solicitação do Cliente HTTP                                                                  *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    //@Override
    public void run() {
        BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
        InputStreamReader dataIn; InputStream ByteIn = null; String fileRequested = null;
        try {
            ByteIn = connect.getInputStream();
            dataIn = new InputStreamReader(ByteIn); //
            in = new BufferedReader(dataIn);  //new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            String LinhaCab[] = new String[12];
            int CChar = 0;
            int CLin = 0;
            int ChRec = 0;
            int CR = 13;
            int LF = 10;
            boolean Leu_CRLF = false;
            boolean fim = false;
            String Requisicao = null;
            LinhaCab[0] = "";
            int Contador = 0;
            boolean mobile = false;

            while (!fim) {
                ChRec = ByteIn.read();
                CChar = CChar + 1;
                if (CChar > 2000) { fim = true; }
                if (ChRec == CR) {
                    ChRec = ByteIn.read();
                    if (ChRec == LF) {
                        if (Leu_CRLF) {
                            fim = true;
                        }
                        else {
                            LinhaCab[CLin] = LinhaCab[CLin] + "\n";
                            CLin = CLin + 1;
                            LinhaCab[CLin] = "";
                            Leu_CRLF = true;
                        }
                    }
                }
                else {
                    LinhaCab[CLin] = LinhaCab[CLin] + (char)ChRec;
                    Leu_CRLF = false;
                }
            }

            // Monta o Cabeçalho da Requisição na String CabHTTP
            String CabHTTP = "";
            for (int k = 0; k < CLin; k++){
                CabHTTP = CabHTTP + LinhaCab[k] + "\n";
                //System.out.println("k = " + k + " - Linha: " + LinhaCab[k]);
            }

            if (CabHTTP.toLowerCase().indexOf("mobile") >= 0) {
                mobile = true;
                System.out.println("Acesso por Dispositivo Móvel");
                Util.Terminal("Acesso por Dispositivo Móvel", false, Verbose);
            }
            else {
                mobile = false;
            }

            StringTokenizer parseLinha1 = new StringTokenizer(LinhaCab[0]);
            String method = parseLinha1.nextToken().toUpperCase();
            String ArquivoReq = "";
            int TamArquReq = 0;

            if (parseLinha1.hasMoreTokens()) {;
                Requisicao = parseLinha1.nextToken();
                ArquivoReq = Requisicao.substring(1);
                TamArquReq = ArquivoReq.length();
            }

            int TamArqReq = ArquivoReq.length();
            String ArqReq = "";
            String MsgTerm = "";

            boolean RecMetodoValido = false;
            boolean RecReqValida = false;
            Util.Terminal("Método: " + method + "  -  Arquivo Requisitado: " + ArquivoReq, false, Verbose);

            if (method.equals("GET")) {  // Trata o método GET
                RecMetodoValido = true;

                // Se não há requisição de arquivo, solicita arquivo index.html (página raiz)
                if (Requisicao.equals("/") || Requisicao.equals("/?")) {
                    RecReqValida = Mensagem.EnvArqTxt(connect, Caminho, "index.html", Verbose);
                }
                else { // Trata a requisição do método GET

                    // Trata requisições de arquivos texto de página HTML
                    if (ArquivoReq.endsWith(".html")) {

                        //Se a requisição for feita de um dispositivo móvel, usa o nome de arquivo com .m
                        if (mobile) {
                            ArqReq = ArquivoReq.substring(0, TamArqReq - 5);
                            ArqReq = ArqReq + ".m.html";
                        }
                        else {
                            ArqReq = ArquivoReq;
                        }
                        RecReqValida = Mensagem.EnvArqTxt(connect, Caminho, ArqReq, Verbose);
                    }

                    // Trata requisições de arquivos texto de estilos (CSS)
                    if (ArquivoReq.endsWith(".css")) {
                        if (mobile) {
                            ArqReq = ArquivoReq.substring(0, TamArqReq - 4);
                            ArqReq = ArqReq + ".m.css";
                        }
                        else {
                            ArqReq = ArquivoReq;
                        }
                        RecReqValida = Mensagem.EnvArqTxt(connect, Caminho, ArqReq, Verbose);
                    }

                    // Trata requisições de arquivos de programas Javascript
                    if (ArquivoReq.endsWith(".js")) {
                        if (mobile) {
                            ArqReq = ArquivoReq.substring(0, TamArqReq - 3);
                            ArqReq = ArqReq + ".m.js";
                        }
                        else {
                            ArqReq = ArquivoReq;
                        }
                        RecReqValida = Mensagem.EnvArqTxt(connect, Caminho, ArqReq, Verbose);
                    }

                    // Trata requisições de arquivos de imagem
                    if (ArquivoReq.endsWith(".ico") || ArquivoReq.endsWith(".jpg") || ArquivoReq.endsWith(".png")) {
                        RecReqValida = Mensagem.EnvArqByte(connect, Caminho, ArquivoReq, Verbose);
                    }

                    // Trata requisição de mensagem XML de Atualização dos Valores das Variáveis
                    if (ArquivoReq.endsWith("local001.xml")) {
                        RecReqValida = true;
                        Contador = Contador + 1;
                        if (Contador < 8) {
                            Mensagem.EnvString(connect, MsgXML, "text/xml", "200", Verbose);
                        }
                        else {
                            String MsgFalha = Mensagem.XML01Falha(0);
                            Mensagem.EnvString(connect, MsgFalha, "text/xml", "200", Verbose);
                        }
                    }
                } // else if (Requisicao.equals("/") || Requisicao.equals("/?")) {
            }  // if (method.equals("GET"))

            if (method.equals("POST")) {              // Se método = POST,
                RecMetodoValido = true;

                // Verifica se foi recebida mensagem binária de atualização (requisição = "atualiza")
                if (ArquivoReq.equals("atualiza")) {
                    RecReqValida = true;

                    String TamMsg = "";     // TamMsg = string com o número de caracteres/bytes da mensagem
                    String TipoMsg = "";    // TipoMsg = string com o tipo da mensagem (XML ou octet/stream"
                    int TamanhoMsg = 0;     // TamanhoMensagem = inteiro com o número de caracteres/bytes da mensagem
                    StringTokenizer parseLinha3 = new StringTokenizer(LinhaCab[2]); // Linha 3
                    String IdLinha3 =  parseLinha3.nextToken().toLowerCase();       // IdLinha3 minúsculo deve ser "Content-Length:"
                    StringTokenizer parseLinha4 = new StringTokenizer(LinhaCab[3]); // Linha 4
                    String IdLinha4 = parseLinha4.nextToken().toLowerCase();        // IdLinha4 minúsculo deve ser "Content-Type:"

                    if (IdLinha3.equals("content-length:") && IdLinha4.equals("content-type:")) {
                        TamMsg = parseLinha3.nextToken();
                        TamanhoMsg = Util.StringToInt(TamMsg);
                        TipoMsg = parseLinha4.nextToken().toLowerCase();

                        // Se é mensagem do tipo application/octet-stream, recebe os bytes e carrega no buffer
                        if (TipoMsg.equals("application/octet-stream")) {
                            for (int i = 0; i < TamanhoMsg; i++){
                                Mensagem.receiveData1[i] = ByteIn.read();
                            }
                            int Byte0 = Mensagem.receiveData1[0];
                            int Byte1 = Mensagem.receiveData1[1];
                            boolean MsgBinOK = false;

                            // Se recebeu mensagem binária válida, sinaliza e lê as variaveis do buffer
                            if ((Byte0 == 0x60) && (Byte1 == 0x45)) {
                                MsgBinOK = true;
                                Mensagem.LeEstMedsPayload();
                                MsgTerm = "Recebida Msg Bin de Atualizacao com " + TamanhoMsg + " Bytes";
                                Util.Terminal(MsgTerm, false, Verbose);
                            }
                            //System.out.println("Mensagem.EstCom1 = " + Mensagem.EstCom1);

                            // Se a mensagem CoAP recebida é válida e se a comunicacao com o programa de atualização
                            // está OK, monta a mensagem XML com os valores atualizados
                            if (MsgBinOK) {
                                if (Mensagem.EstCom1 == 1) {
                                    MsgXML = Mensagem.XML01(ComRec) + " ";
                                    //System.out.println(MsgXML);
                                }
                                else {  // Se não há comunicacao com o Concentrador, monta a mensagem XML de falha
                                    Mensagem.XML01Falha(1);
                                }

                                // Responde com mensagem de XML de comando
                                String StrComando = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                                StrComando = StrComando + "<CMD>" + ComRec + "</CMD>";
                                Mensagem.EnvString(connect, StrComando, "text/xml", "200", Verbose);
                                ComRec = "cmd=0000";
                                Contador = 0;
                            }
                            else {
                                Util.Terminal("Recebida Mensagem de Atualizacao Invalida", false, Verbose);
                                String StrComando = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                                StrComando = StrComando + "<CMD>MsgInv</CMD>";
                                Mensagem.EnvString(connect, StrComando, "text/xml", "200", Verbose);
                            }
                        } // if (TipoMsg.equals("application/octet-stream"))
                    } // if ((IdLinha3 == "content-length:") && (IdLinha4 == "content-type:"))
                }  // if (Requisicao.equals("atualiza"))

                // Verifica se foi recebida mensagem de comando (requisição = "cmd=xxxx")
                if (ArquivoReq.substring(TamArquReq - 8, TamArquReq - 5).equals("cmd")) {
                    ComRec = ArquivoReq.substring(TamArquReq - 8, TamArquReq);
                    RecReqValida = true;
                    MsgTerm = "Recebida Mensagem de Comando " + ComRec;
                    Util.Terminal(MsgTerm, false, Verbose);
                    MsgXML = Mensagem.XML01(ComRec) + " ";        // monta e envia a mensagem XML
                    Mensagem.EnvString(connect, MsgXML, "text/xml", "200", Verbose);
                }

            } // if (method.equals("POST"))

            if (RecMetodoValido) {    // Se foi recebido um método válido,
                if (!RecReqValida) {  // e se não está disponível o recurso solicitado pelo método GET ou POST
                    Mensagem.EnvStringErro(connect, 404, Verbose);
                }
            }
            else {                    // Se não foi recebido um método válido,
                Mensagem.EnvStringErro(connect, 501, Verbose);
            }
        } catch (IOException ioe) {
            Util.Terminal("Erro no Servidor: " + ioe, false, Verbose);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close();
            } catch (Exception e) {
                Util.Terminal("Erro no fechamento do stream : " + e.getMessage(), false, Verbose);
            }
            Util.Terminal("Conexao com o Cliente Encerrada", false, Verbose);

        }
    }
}
