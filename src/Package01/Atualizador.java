package Package01;
import java.io.IOException;
import java.net.InetAddress;

//*********************************************************************************************************************
//                                                                                                                    *                                                     *
// Projeto: Atualizador do Banco de Dados do Servidor na Nuvem                                                        *
//                                                                                                                    *
// Nome da Classe: Atualizador (executável)                                                                           *
//                                                                                                                    *
// Funcao: efetua a comunicação com a UTR em protocolo CoAP e envia as informações para o Servidor na Nuvem           *
//                                                                                                                    *
//*********************************************************************************************************************
//
public class Atualizador {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        // TODO Auto-generated method stub

        boolean Verbose = true;

        String EndIPSrv = "200.98.140.180";
        //String EndIPSrv = "192.168.0.50";
        int PortaSrv = 8080;
        String Metodo = "POST";
        String Recurso = "atualiza";

        String IPConcArd = "192.168.0.150";
        int PortaUDP = 5683;
        int ContMsgCoAP = 0;

        String Caminho = "";
        String IPHost = "192.168.0.170";
        int Comando = 0;

        byte DH [] = new byte[6];
        byte Segundo;
        byte SegundoAnterior;

        DH = Util.LeDataHora();
        Segundo = DH[2];
        SegundoAnterior = Segundo;

        InetAddress ip = InetAddress.getLocalHost();
        String NomeComputador = "";
        NomeComputador = ip.getHostName();
        if (NomeComputador.equals("raspberrypi")) {
            Caminho = "/home/pi/Desktop/Programas/";
            IPHost = "192.168.0.170";
            Verbose = false;

            Util.Terminal("Atualizador Iniciado no Computador Raspberry PI 3", true, true);
        }
        if (NomeComputador.equals("BernardoLinux")) {
            Caminho = "/home/antonio/ExecJava/";
            IPHost = "192.168.0.49";
            Verbose = true;
            Util.Terminal("Atualizador Iniciado no Computador BernardoLinux", true, true);
        }
        if (NomeComputador.equals("antonio-Vostro1510")) {
            Caminho = "/home/antonio/Workspace/Recursos";
            IPHost = "192.168.0.50";
            Verbose = true;
            Util.Terminal("Atualizador Iniciado no Computador antonio-Vostro1510 IP = " + IPHost, true, true);
        }
        Util.Terminal("Enviar Mensagens de Atualização para o Servidor no IP = " + EndIPSrv, true, true);

        int cont = 0;
        boolean fim = false;
        while (!fim) {
            DH = Util.LeDataHora();
            Segundo = DH[2];
            if (Segundo != SegundoAnterior) {
                cont = cont + 1;
                SegundoAnterior = Segundo;
            }
            if (cont >= 4) {

                byte[] MsgEnvSrv = EnvRecMsg.CoAPUDP(IPConcArd, PortaUDP, "estados", ContMsgCoAP, Comando, Verbose);

                String MsgRec = EnvRecMsg.BinSrv(EndIPSrv, PortaSrv, IPHost, MsgEnvSrv, Metodo, Recurso, Verbose);
                if (!MsgRec.isEmpty()) {
                    boolean Tk1OK = MsgRec.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", 0) >= 0;
                    boolean Tk2OK = MsgRec.indexOf("<CMD>", 0) >= 0;
                    boolean Tk3OK = MsgRec.indexOf("</CMD>", 0) >= 0;
                    String Cmd = "";
                    int indice1 = 0;
                    int indice2 = 0;


                    if (Tk1OK && Tk2OK && Tk3OK) {
                        indice1 = MsgRec.indexOf("<CMD>") + 5;
                        indice2 = MsgRec.indexOf("</CMD>");
                        Cmd = MsgRec.substring(indice1, indice2);
                        if (!(Cmd.isEmpty())) {
                            Comando = ExecComandoHTTP(Cmd, Verbose);
                            Util.Terminal("Recebida Mensagem de Comando do Servidor: " + Cmd, false, Verbose);
                        }
                        else {
                            Util.Terminal("Recebida Mensagem de Reconhecimento do Servidor", false, Verbose);
                        }
                    } else {
                        Util.Terminal("Recebida Mensagem de Resposta Inválida", false, Verbose);
                    }
                    cont = 0;
                } // if (!MsgRec.isEmpty())
            } // if (cont >= 4)
        } // while (true)
    } // public static void main(String[] args)



    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome do Método: ExecComandoHTTP                                                                                 *
    //	                                                                                                               *
    // Funcao: obtém o número do comando referente à string de comando recebida do Servidor HTTP                       *
    //                                                                                                                 *
    // Entrada: string com o código do comando recebido do Servidor HTTP e a habilitação de impressão de mensagem      *
    //                                                                                                                 *
    // Saida: o número inteiro refetente à string de comando recebida do Servidor HTTP                                 *
    //	                                                                                                               *
    //******************************************************************************************************************
    //
    private static int ExecComandoHTTP(String comrechttp, boolean Verbose) {
        int Comando = 0;
        if (comrechttp.equals("cmd=0002")) {
            Comando = 2;
            Util.Terminal("Comando: Acerto Relogio", false, Verbose);
        }
        if (comrechttp.equals("cmd=0003")) {
            Comando = 3;
            Util.Terminal("Comando: Modo Economia", false, Verbose);
        }
        if (comrechttp.equals("cmd=0004")) {
            Comando = 4;
            Util.Terminal("Comando: Modo Normal", false, Verbose);
        }
        if (comrechttp.equals("cmd=0016")) {
            Comando = 16;
            Util.Terminal("Comando: Manual Carga 1", false, Verbose);
        }
        if (comrechttp.equals("cmd=0017")) {
            Comando = 17;
            Util.Terminal("Comando: Automatico Carga 1", false, Verbose);
        }
        if (comrechttp.equals("cmd=0005")) {
            Comando = 5;
            Util.Terminal("Comando: Manual Cargas 234", false, Verbose);
        }
        if (comrechttp.equals("cmd=0006")) {
            Comando = 6;
            Util.Terminal("Comando: Automatico Cargas 234", false, Verbose);
        }
        if (comrechttp.equals("cmd=0007")) {
            Comando = 7;
            Util.Terminal("Comando: Habilita Carga 1", false, Verbose);
        }
        if (comrechttp.equals("cmd=0008")) {
            Comando = 8;
            Util.Terminal("Comando: Desabilita Carga 1", false, Verbose);
        }
        if (comrechttp.equals("cmd=0009")) {
            Comando = 9;
            Util.Terminal("Comando: Habilita Carga 2", false, Verbose);
        }
        if (comrechttp.equals("cmd=0010")) {
            Comando = 10;
            Util.Terminal("Comando: Desabilita Carga 2", false, Verbose);
        }
        if (comrechttp.equals("cmd=0011")) {
            Comando = 11;
            Util.Terminal("Comando: Habilita Carga 3", false, Verbose);
        }
        if (comrechttp.equals("cmd=0012")) {
            Comando = 12;
            Util.Terminal("Comando: Desabilita Carga 3", false, Verbose);
        }
        if (comrechttp.equals("cmd=0013")) {
            Comando = 13;
            Util.Terminal("Comando: Habilita Carga 4", false, Verbose);
        }
        if (comrechttp.equals("cmd=0014")) {
            Comando = 14;
            Util.Terminal("Comando: Desabilita Carga 4", false, Verbose);
        }
        if (comrechttp.equals("cmd=0015")) {
            Comando = 15;
            Util.Terminal("Comando: Apaga Indicadores de Falha", false, Verbose);
        }
        return(Comando);
    }

}
