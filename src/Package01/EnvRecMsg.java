package Package01;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.lang.String;

public class EnvRecMsg {

    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome da Rotina: BinSrv                                                                                          *
    //                                                                                                                 *
    // Funcao: envia uma mensagem de bytes recebida de um Buffer para um servidor na nuvem e recebe a resposta         *
    //                                                                                                                 *
    // Entrada: Endereço IP do servidor, porta de conexão, Mensagem a ser enviada, Método (POST ou PUT),               *
    //          recurso (query)                                                                                        *
    //                                                                                                                 *
    // Saida: String com a mensagem de resposta do servidor (se a mensagem é vazia, houve falha de comunicação         *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    static String BinSrv(String EndIP, int Porta, String IPHost, byte[] ByteBuf, String Metodo, String Recurso, boolean Verbose) {
        String MsgRec = "";
        PrintWriter EnvChar = null; BufferedOutputStream EnvByte = null;
        InputStreamReader RecByte = null; BufferedReader RecChar = null;

        try {
            Socket socket = new Socket(EndIP, Porta);  // Cria o socket de conexão no Servidor HTTP PraxServer
            EnvByte = new BufferedOutputStream(socket.getOutputStream());
            EnvChar = new PrintWriter(EnvByte, true);
            RecChar = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socket.setSoTimeout(3000);

            if (socket.isConnected()) {
                String MsgTerm = "Concentrador conectou ao: " + socket.toString();
                Util.Terminal(MsgTerm, false, Verbose);
            }

            int TamMsgBin = ByteBuf.length;
            String CabXML = Metodo + " /" + Recurso + " HTTP/1.1\r\n";
            CabXML = CabXML + "Host: " + IPHost + ":8080\r\n";
            CabXML = CabXML + "Content-Length: " + TamMsgBin + "\r\n";
            CabXML = CabXML + "Content-Type: application/octet-stream\r\n";
            CabXML = CabXML + "User-Agent: (Linux x86_64) PraxClient/1.0\r\n";
            CabXML = CabXML + "\r\n";

            // Transmite a mensagem para o Servidor
            EnvChar.print(CabXML);
            EnvChar.flush();
            EnvByte.write(ByteBuf, 0, TamMsgBin);
            EnvByte.flush();

            String MsgTerm = "Enviada Requisicao: " + Metodo + " /" + Recurso + " HTTP/1.1 com " + TamMsgBin + " Bytes";
            Util.Terminal(MsgTerm, false, Verbose);
            MsgTerm = " com " + TamMsgBin + " Bytes";

            try {
                String linha;
                while ((linha = RecChar.readLine()) != null) {
                    MsgRec = MsgRec + linha + "\n";
                }
            }
            catch(java.net.SocketTimeoutException tmo) {
                Util.Terminal("Timeout na resposta do Servidor", false, Verbose);
            }
            socket.close();
        }
        catch (IOException err) {
            Util.Terminal(" - Erro na Rotina EnvRecMsgSrv: " + err, false, Verbose);
        }
        return(MsgRec);
    }


    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome do Método: CoAPUDP                                                                                         *
    //                                                                                                                 *
    // Funcao: envia uma mensagem de requisição e recebe a mensagem de resposta do Controlador Arduino Mega            *
    //         em Protocolo CoAP                                                                                       *
    //                                                                                                                 *
    // Byte |           0         |      1       |      2      |        3        |        4        |        5        | *
    // Bit  | 7 6 | 5 4 | 3 2 1 0 |  7654  3210  |  7654 3210  | 7 6 5 4 3 2 1 0 | 7 6 5 4 3 2 1 0 | 7 6 5 4 3 2 1 0 | *
    //      | Ver |Tipo |  Token  | Código (c.m) | Message ID  |      Option     |   Payload ID    |                   *
    //                                                                                                                 *
    // Ver (Versão) = 01 (O número da versão do protocolo CoAP é fixo)  / TKL (Token) = 0000 (não é usado)             *
    // Tipo (de Mensagem): 00 Confirmável (CON) / 01 Não-Confirmável (NON) / 10 Reconhecimento (ACK) / 11 Reset (RST)  *
    //                                                                                                                 *
    // Códigos de Solicitação: 0000 0000 EMPTY / 0000 0001 GET   / 0000 0010 POST / 0000 0011 PUT / 0000 0100 DELETE   *
    //                                                                                                                 *
    // Códigos de Resposta (Sucesso): 0100 0001 Created / 0100 0010 Deleted / 0100 0011 Valid / 0100 0100 Changed      *
    //                                0100 0101 Content                                                                *
    //                                                                                                                 *
    // Códigos de Erro Cliente: 1000 0000 Bad Request / 1000 0001 Unauthorized / 1000 0010 Bad Option                  *
    //                          1000 0011 Forbidden / 1000 0100 Not Found / 1000 0101 Method Not Allowed               *
    //                          1000 0110 Not Acceptable / 1000 1100 Request Entity Incomplete                         *
    //                                                                                                                 *
    // Códigos de Erro Servidor: 1010 0000 Internal Server Error / 1010 0001 Not Implemented / 1010 0010 Bad Gateway   *
    //                           1010 0011 Service Unavailable / 1010 0100 Gateway Timeout                             *
    //                           1010 0101 Proxying Not Supported                                                      *
    //                                                                                                                 *
    // Message ID (Identificação da mensagem): inteiro de 16 bits sem sinal Mensagem Enviada e Mensagem de Resposta    *
    //                                         com mesmo ID                                                            *
    //                                                                                                                 *
    // Option (Opções) = 0000 0000 (não é usado) / Identificador de Início do Payload: 1111 1111                       *
    //******************************************************************************************************************
    //
    static byte[] CoAPUDP(String EndIP, int Porta, String URI, int ContMsgCoAP, int Comando, boolean Verbose) {
        int TamMsgRspCoAP = 320;
        int TamMsgSrv = 320;
        byte [] MsgRecCoAP = new byte[TamMsgRspCoAP];
        byte [] MsgEnvSrv = new byte[TamMsgSrv];

        try {
            byte[] MsgReqCoAP = new byte[32];

            int TamURI = URI.length();
            byte DH[] = new byte[6];
            DH = Util.LeDataHora();

            MsgReqCoAP[0] = 0x40;                       // Versão = 01 / Tipo = 00 / Token = 0000
            MsgReqCoAP[1] = 0x01;                       // Código de Solicitação: 0.01 GET
            ContMsgCoAP = ContMsgCoAP + 1;              // Incrementa o Identificador de mensagens
            MsgReqCoAP[2] = Util.ByteHigh(ContMsgCoAP); // Byte Mais Significativo do Identificador da Mensagem
            MsgReqCoAP[3] = Util.ByteLow(ContMsgCoAP);  // Byte Menos Significativo do Identificador da Mensagem
            MsgReqCoAP[4] = (byte) (0xB0 + TamURI);     // Delta: 11 - Primeira Opcao 11: Uri-path e Núm. Bytes da URI
            int j = 5;
            for (int i = 0; i < TamURI; i++) {          // Carrega os codigos ASCII da URI
                char Char = URI.charAt(i);
                int ASCII = (int) Char;
                MsgReqCoAP[i + 5] = (byte) ASCII;
                j = j + 1;
            }
            MsgReqCoAP[j] = (byte) 0x11;    // Delta: 1 - Segunda Opcao (11 + 1 = 12): Content-format e Núm. Bytes (1)
            j = j + 1;
            MsgReqCoAP[j] = 42;             // Codigo da Opcao Content-format: application/octet-stream
            j = j + 1;
            MsgReqCoAP[j] = -1;             // Identificador de Inicio do Payload (255)
            j = j + 1;
            MsgReqCoAP[j] = (byte)Comando;  // Carrega o Código do Comando no Payload
            j = j + 1;
            MsgReqCoAP[j] = DH[0];          // Carrega a Hora do Computador no Payload
            j = j + 1;
            MsgReqCoAP[j] = DH[1];          // Carrega a Minuto do Computador no Payload
            j = j + 1;
            MsgReqCoAP[j] = DH[2];          // Carrega a Segundo do Computador no Payload
            j = j + 1;
            MsgReqCoAP[j] = DH[3];          // Carrega a Dia do Computador no Payload
            j = j + 1;
            MsgReqCoAP[j] = DH[4];          // Carrega a Mes do Computador no Payload
            j = j + 1;
            MsgReqCoAP[j] = DH[5];          // Carrega a Ano do Computador no Payload
            j = j + 1;
            int TamCab = j;                 // Carrega o número de bytes do cabeçalho

            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(EndIP);
            clientSocket.setSoTimeout(1000);
            DatagramPacket sendPacket = new DatagramPacket(MsgReqCoAP, TamCab, IPAddress, Porta);
            DatagramPacket receivePacket = new DatagramPacket(MsgRecCoAP, TamMsgRspCoAP);

            clientSocket.send(sendPacket);
            Util.Terminal("Enviada Requisicao CoAP para o Controlador", false, Verbose);

            // Espera a Mensagem CoAP de Resposta. Se a mensagem de resposta  for recebida, carrega nas variáveis.
            try {
                clientSocket.receive(receivePacket);
                MsgRecCoAP[30] = 1;
                MsgEnvSrv = LeEstMedsPayload(MsgRecCoAP, TamMsgRspCoAP);
                Util.Terminal("Recebida Mensagem CoAP do Controlador", false, Verbose);
            } catch (java.net.SocketTimeoutException e) {
                MsgRecCoAP[0] = 0x40;
                MsgRecCoAP[1] = 1;
                MsgRecCoAP[30] = 0;
                Util.Terminal(" - Erro: o Dispositivo nao Respondeu " + MsgRecCoAP[14], false, Verbose);
            }
            clientSocket.close();
        } catch (IOException err) {
            Util.Terminal("Erro na Rotina EnvRecMsgSrv: " + err, false, Verbose);
        }
        return (MsgEnvSrv);
    }

    //*****************************************************************************************************************
    // Nome do Método: LeEstMedsPayload()                                                                             *
    //                                                                                                                *
    // Funcao: lê as informações dos estados, saídas digitais e medidas recebidas de uma mensagem binária em          *
    //         protocolo CoAP recebida do equipamento Concentrador. Este método é usado no acesso local pelo          *
    //         método EnvRecMsg.CoAPUDP                                                                               *
    //                                                                                                                *
    // Medidas (64): bytes 160 a 288 - 2 bytes por medida                                                             *
    //                                                                                                                *
    // Entrada: array de bytes com a mensagem binária em protocolo CoAP recebida e o núm. de bytes da mensagem        *
    //                                                                                                                *
    // Saida: array de bytes com a mensagem binária a ser enviada para o Servidor HTTP                                *
    //                                                                                                                *
    //*****************************************************************************************************************
    //
    static byte[] LeEstMedsPayload(byte[] MsgRecCoAP, int TamMsgSrv) {

        byte[] MsgEnvSrv = new byte[TamMsgSrv];  // Array com a mensagem que vai ser enviada para o Servidor HTTP

        // Carrega as Informações de Estado nas Variáveis
        byte Hora = MsgRecCoAP[21];
        byte Minuto = MsgRecCoAP[22];
        byte Segundo = MsgRecCoAP[23];
        byte Dia = MsgRecCoAP[24];
        byte Mes = MsgRecCoAP[25];
        byte Ano = MsgRecCoAP[26];

        // Carrega as Informações de Estado de Comunicação nas Variáveis
        byte EstComUTR = MsgRecCoAP[27];
        byte EstComCC1 = MsgRecCoAP[28];
        byte EstComCC2 = MsgRecCoAP[29];
        byte EstCom1 = MsgRecCoAP[30];

        // Carrega as Informações de Estado nas Variáveis
        byte DJEINV1 = MsgRecCoAP[37];
        byte CircBoia = MsgRecCoAP[38];
        byte BoiaCxAzul = MsgRecCoAP[39];
        byte CircBomba = MsgRecCoAP[40];
        byte AlRedeBomba = MsgRecCoAP[41];
        byte EstRede = MsgRecCoAP[42];
        byte MdOp = MsgRecCoAP[43];
        byte MdCom = MsgRecCoAP[44];
        byte MdCtrl1 = MsgRecCoAP[55];
        byte MdCtrl = MsgRecCoAP[45];
        byte Carga1 = MsgRecCoAP[46];
        byte Carga2 = MsgRecCoAP[47];
        byte Carga3 = MsgRecCoAP[48];
        byte Carga4 = MsgRecCoAP[49];
        byte HabCom = MsgRecCoAP[50];
        byte EstadoInversor1 = MsgRecCoAP[51];
        byte EstadoInversor2 = MsgRecCoAP[52];
        byte EstadoCarga3 = MsgRecCoAP[53];
        byte BombaLigada = MsgRecCoAP[54];

        // Carrega as Informações de Alarme nas Variáveis
        byte FalhaIv1 = MsgRecCoAP[56];
        byte SubTensaoInv1 = MsgRecCoAP[57];
        byte SobreTensaoInv1 = MsgRecCoAP[58];
        byte SobreTempDrInv1 = MsgRecCoAP[59];
        byte SobreTempTrInv1 = MsgRecCoAP[60];
        byte DjAbIv1 = MsgRecCoAP[61];
        byte FalhaIv2 = MsgRecCoAP[62];
        byte SubTensaoInv2 = MsgRecCoAP[63];
        byte SobreTensaoInv2 = MsgRecCoAP[64];
        byte SobreTempDrInv2 = MsgRecCoAP[65];
        byte SobreTempTrInv2 = MsgRecCoAP[66];
        byte DjAbIv2 = MsgRecCoAP[67];

        byte CDBat = MsgRecCoAP[68];
        byte CxAzNvBx = MsgRecCoAP[69];
        byte EdCxAzCheia = MsgRecCoAP[70];
        byte FonteCC2Ligada = MsgRecCoAP[71];
        byte EstadoCxAz = MsgRecCoAP[72];
        byte FonteCC1Ligada = MsgRecCoAP[73];

        byte SobreCorrInv1 = MsgRecCoAP[74];
        byte SobreCorrInv2 = MsgRecCoAP[75];

        // Le o estado das saidas digitais
        int k = 112;
        byte[] SD = new byte[128];
        for (int i = 0; i < 32; i++){
            SD[i] = MsgRecCoAP[k];
            k = k + 1;
        }

        // Carrega as variaveis com os valores das saidas digitais do Concentrador Arduino Mega
        byte Iv1Lig = SD[1];
        byte CT2Inv = SD[17];
        byte CT1Inv = SD[0];
        byte CT3Inv = SD[2];
        byte Iv2Lig = SD[10];
        byte EstFonteCC = SD[16];

        // Le as Medidas da mensagem recebida do Concentrador Arduino Mega (medidas 0 a 47)
        k = 160;
        int[] Med = new int[256];
        for (byte i = 0; i < 48; i++){
            Med[i] = Util.DoisBytesInt(MsgRecCoAP[k], MsgRecCoAP[k + 1]);
            k = k + 2;
        }

        // Carrega as medidas lidas do Concentrador Arduino Mega nas variaveis
        int VBat = Med[0];           // Tensão do Banco de Baterias
        int VMBat = Med[16];         // Tensão Média Estendida do Banco de Baterias
        int VRede = Med[5];          // Tensão da Rede
        int Icarga3 = Med[14];       // Corrente Carga 3 (Geladeira)
        int ICircCC = Med[3];        // Corrente Total dos Circuitos CC
        int IFonteCC = Med[11];      // Corrente de Saída da Fonte CC


        int TmpBmbLig = Med[17];     // Tempo da Bomba Ligada
        int TmpCxAzNvBx = Med[46];   // Tempo da Caixa Azul em Nivel Baixo

        // Leitura e Cálculo das Medidas referentes à Geração e Consumo
        int VP12 = Med[18];          // 0x3100 - PV array voltage 1
        int IS12 = Med[19];          // 0x3101 - PV array current 1
        int WS12 = Med[20];          // 0x3102 - PV array power 1
        int VBat1 = Med[21];         // 0x3104 - Battery voltage 1
        int ISCC1 = Med[22];         // 0x3105 - Battery charging current 1
        int WSCC1 = Med[23];         // 0x3106 - Battery charging power 1
        int TBat =  Med[24];         // 0x3110 - Battery Temperature 1

        int VP34 = Med[26];          // 0x3100 - PV array voltage 2
        int IS34 = Med[27];          // 0x3101 - PV array current 2
        int WS34 = Med[28];          // 0x3102 - PV array power 2
        int VBat2 = Med[29];         // 0x3104 - Battery voltage 2
        int ISCC2 = Med[30];         // 0x3105 - Battery charging current 2
        int WSCC2 = Med[31];         // 0x3106 - Battery charging power 2 (VG.Med[45])

        // Leitura e Cálculo das Medidas referentes ao Inversor 1
        int IEIv1 = Med[12];         				// Corrente de Entrada do Inversor 1 (15)
        int WEIv1 = (VBat * IEIv1) / 100;			// Potência de Entrada do Inversor 1 (VG.Med[41])
        int VSIv1 = Med[4];          				// Tensão de Saída do Inversor 1
        int ISInv1 = (7 * Med[10]) / 10;        	// Corrente de Saída do Inversor 1 (13)
        int WSInv1 = (VSIv1 * ISInv1) / 1000;		// Potencia de Saida do Inversor 1 (VG.Med[42])
        int TDInv1 = Med[8];         				// Temperatura do Driver do Inversor 1 (2)
        int TTInv1 = Med[9];         				// Temperatura do Transformador do Inversor 1 (7)
        int EfIv1 = 0;
        if (WEIv1 > 2000) {                         // Se o Inversor 1 está ligado,
            EfIv1 = (100*WSInv1)/WEIv1;		        // calcula a Eficiência do Inversor 1
        }
        else {
            EfIv1 = 0;
        }
        int SDIv1 = 0;

        // Leitura e Cálculo das Medidas referentes ao Inversor 2
        double IEInversor2 = 838 * Med[15];         //  838
        int IEIv2 = (int)(IEInversor2 / 1000); 		// Corrente de Entrada do Inversor 2 (12)
        int WEIv2 = (VBat * IEIv2) / 100;         	// Potencia de Entrada do Inversor 2 (VG.Med[38])
        int VSIv2 = Med[6];          				// Tensão de Saída do Inversor 2
        int ISInv2 = Med[13];
        int WSInv2 = (VSIv2 * ISInv2) / 1000;       // Potencia de Saida do Inversor 2 (VG.Med[39])
        int TDInv2 = Med[2];         				// Temperatura do Driver do Inversor 2 (8)
        int TTInv2 = Med[7];         				// Temperatura do Transformador do Inversor 2 (9)
        int EfIv2 = 0;
        if (WEIv2 > 2000) {                         // Se o Inversor 2 está ligado,
            EfIv2 = (100*WSInv2) / WEIv2;		    // calcula a Eficiência do Inversor 2
        }
        else {
            EfIv2 = 0;
        }
        int SDIv2 = 0;

        int ITotGer = Med[33];       				// Corrente Total Gerada
        int WCircCC = Med[35];       				// Potencia Consumida pelos Circuitos de 24Vcc
        int WFonteCC = Med[36];      				// Potencia Fornecida pela Fonte 24Vcc
        int IBat = Med[37];          				// Corrente de Carga ou Descarga do Banco de Baterias
        int WBat = (VBat * IBat) / 100;				// Potência de Carga/Descarga do Banco de Baterias
        ITotGer = ISCC1 + ISCC2;				    // Corrente Total Gerada
        int WTotGer = WSCC1 + WSCC2;				// Potência Total Gerada
        int ITotCg = IEIv1 + IEIv2 + (ICircCC/10);	// Corrente Total Consumida pelas Cargas
        int WTotCg =  WEIv1 + WEIv2 + WCircCC;		// Potência Total Consumida pelas Cargas

        // Cálculo da Saude do Controlador de Carga 1
        int SDCC1 = 0;
        if (WSCC1 > 0) {
            SDCC1 = 100* (WS12 / WSCC1);
        }
        else {
            if (WS12 == 0) {
                SDCC1 = 100;
            }
            else {
                SDCC1 = 0;
            }
        }

        // Cálculo da Saude do Controlador de Carga 2
        int SDCC2 = 0;
        if (WSCC2 > 0) {
            SDCC2 = 100 * (WS34 / WSCC2);
        }
        else {
            if (WS34 == 0) {
                SDCC2 = 100;
            }
            else {
                SDCC2 = 0;
            }
        }

        int SDBat = 95;

        System.arraycopy(MsgRecCoAP, 0, MsgEnvSrv, 0, MsgRecCoAP.length);

        // As seguintes medidas são calculadas e carregadas no buffer para o servidor em nuvem
        MsgEnvSrv[226] = Util.ByteLow(ITotGer);   // Corrente Total Gerada
        MsgEnvSrv[227] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[228] = Util.ByteLow(ITotGer);   //  CB2Bytes(WTotGer, 34)  Potência Total Gerada
        MsgEnvSrv[229] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[248] = Util.ByteLow(ITotGer);   // CB2Bytes(ITotCg, 44)    Corrente Total Cargas
        MsgEnvSrv[249] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[250] = Util.ByteLow(ITotGer);   // CB2Bytes(WTotCg, 45);   Potência Total Cargas
        MsgEnvSrv[251] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[256] = Util.ByteLow(ITotGer);   // CB2Bytes(TemperaturaBoiler, 48);
        MsgEnvSrv[257] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[258] = Util.ByteLow(ITotGer);   // CB2Bytes(TemperaturaPlaca, 49);
        MsgEnvSrv[259] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[260] = Util.ByteLow(ITotGer);   // CB2Bytes(TempoBmbLigada, 50);
        MsgEnvSrv[261] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[262] = Util.ByteLow(ITotGer);   // CB2Bytes(TempoBmbDesligada, 51);
        MsgEnvSrv[263] = Util.ByteHigh(ITotGer);

        MsgEnvSrv[144] = (byte)EfIv1;  	          // Eficiência do Inversor 1
        //InferenciaFuzzyInv1();                  // Calcula a Saude do Inversor 1
        MsgEnvSrv[145] = (byte)SDIv1;  	          // Carrega a Saude do Inversor 1 no Buffer
        //InferenciaFuzzyInv2();                  // Calcula a Saude do Inversor 2
        MsgEnvSrv[146] = (byte)SDIv2;  	          // Carrega a Saude do Inversor 2 no Buffer
        MsgEnvSrv[147] = (byte)EfIv2;  	          // Eficiência do Inversor 2
        MsgEnvSrv[148] = (byte)SDCC1;  	          // Saude do Controlador de Carga 1
        MsgEnvSrv[149] = (byte)SDCC2;  	          // Saude do Controlador de Carga 2
        MsgEnvSrv[150] = (byte)SDBat;  	          // Saude do Banco de Baterias

        return MsgEnvSrv;

    } // Fim da Rotina LeEstMedsPayload()


    //*****************************************************************************************************************
    //                                                                                                                *
    // Nome do Método: EnvArqTxt                                                                                      *
    //	                                                                                                              *
    // Funcao: envia para o cliente conectado uma mensagem HTTP lida de um arquivo texto ( sequência de caracteres)   *
    //                                                                                                                *
    // Entrada: Socket de conexão, String com o caminho do arquivo (diretório), String com o nome do arquivo,         *
    //          boolean Verbose (habilita envio de mensagens para o terminal)                                         *
    //                                                                                                                *
    // Saida: se o arquivo foi lido corretamente retorna true                                                         *
    //	                                                                                                              *
    //*****************************************************************************************************************
    //
    public static boolean EnvArqTxt(Socket connect, String Caminho, String NomeArquivo, boolean Verbose) {
        PrintWriter out = null;
        boolean ArquivoLido = false;

        try {
            out = new PrintWriter(connect.getOutputStream());
            Arquivo Arq = new Arquivo();


            if (Arq.Existe(Caminho, NomeArquivo)) {
                int TamArquivo = Arq.Tamanho(Caminho, NomeArquivo);
                String TipoArquivo = Arq.Tipo(NomeArquivo);
                String DadosArquivo = Arq.LeTexto(Caminho, NomeArquivo);
                ArquivoLido = true;
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server from PraxServer : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + TipoArquivo);
                out.println("Content-length: " + TamArquivo);
                out.println();
                out.print(DadosArquivo);
                out.flush();

                Util.Terminal("Lido Arquivo " + TipoArquivo + ": " + NomeArquivo, false, Verbose);
                Util.Terminal("Enviada Mensagem HTTP (Texto) " + TipoArquivo + " com " + TamArquivo + " Caracteres", false, Verbose);
            }
            else {
                Util.Terminal("Erro na leitura do arquivo: " + NomeArquivo, false, Verbose);
            }
            return(ArquivoLido);
        }
        catch (IOException ioe) {
            Util.Terminal("Erro na Rotina EnvMsgArquivoTxt", false, Verbose);
            return(false);
        }
    } // Fim do Método


    //*****************************************************************************************************************
    //                                                                                                                *
    // Nome do Método: EnvArqByte                                                                                     *
    //	                                                                                                              *
    // Funcao: envia para o cliente conectado uma mensagem HTTP lida de um arquivo em Bytes                           *
    //                                                                                                                *
    // Entrada: Socket de conexão, String com o caminho do arquivo (diretório), String com o nome do arquivo,         *
    //          boolean Verbose (habilita envio de mensagens para o terminal)                                         *
    //                                                                                                                *
    // Saida: se o arquivo foi lido corretamente retorna true                                                         *
    //	                                                                                                              *
    //*****************************************************************************************************************
    //
    public static boolean EnvArqByte(Socket connect, String Caminho, String NomeArquivo, boolean Verbose) {
        PrintWriter out = null; BufferedOutputStream dataOut = null;
        try {
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            Arquivo Arq = new Arquivo();
            int TamArquivo = Arq.Tamanho(Caminho, NomeArquivo);
            String tipo = Arq.Tipo(NomeArquivo);
            byte[] MsgDados = Arq.LeByte(Caminho, NomeArquivo);

            out.println("HTTP/1.1 200 OK");
            out.println("Server: Java HTTP Server from PraxServer : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: " + tipo);
            out.println("Content-length: " + TamArquivo);
            out.println();
            out.flush();
            dataOut.write(MsgDados, 0, TamArquivo);
            dataOut.flush();

            Util.Terminal("Lido Arquivo " + tipo + " : " + NomeArquivo, false, Verbose);
            Util.Terminal("Enviada Mensagem HTTP (Byte) do tipo " + tipo + " com " + TamArquivo + " Caracteres", false, Verbose);

            return(true);
        }
        catch (IOException ioe) {
            if (Verbose) {
                System.out.println("Erro na Rotina EnvMsgArquivoByte");
            }
            return(false);
        }
    } // Fim do Método


    //*****************************************************************************************************************
    //                                                                                                                *
    // Nome do Método: EnvString                                                                                      *
    //	                                                                                                              *
    // Funcao: envia para o cliente conectado uma mensagem HTTP lida de uma String                                    *
    //                                                                                                                *
    // Entrada: Socket de conexão, String com a Mensagem a ser Enviada; String com o Tipo da Mensagem,                *
    //          boolean Verbose (habilita envio de mensagens para o terminal)                                         *
    //                                                                                                                *
    // Saida: se a mensagem foi enviada corretamente, retorna true                                                    *
    //	                                                                                                              *
    //*****************************************************************************************************************
    //
    public static boolean EnvString(Socket connect, String Msg, String Tipo, String CodRsp, boolean Verbose) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(connect.getOutputStream());
            int TamMsg = Msg.length();
            out.println("HTTP/1.1 " + CodRsp + " OK");
            out.println("Server: Java HTTP Server from PraxServer : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: " + Tipo);
            out.println("Content-length: " + TamMsg);
            out.println();
            out.print(Msg);
            out.flush();

            Util.Terminal("Enviada Mensagem HTTP do tipo " + Tipo + " com " + TamMsg + " Caracteres", false, Verbose);

            return(true);
        }
        catch (IOException ioe) {
            if (Verbose) {
                System.out.println("Erro ao enviar a mensagem HTTP lida de uma string");
            }
            return(false);
        }
    } // Fim do Método


    //*****************************************************************************************************************
    //                                                                                                                *
    // Nome do Método: EnvStringErro                                                                                  *
    //	                                                                                                              *
    // Funcao: envia para o cliente conectado uma mensagem de erro HTTP lida de uma String                            *
    //                                                                                                                *
    // Entrada: Socket de conexão, int com o código do erro (404 ou 501), boolean Verbose                             *
    //          (habilita envio de mensagens para o terminal)                                                         *
    //                                                                                                                *
    // Saida: se a mensagem foi enviada corretamente, retorna true                                                    *
    //	                                                                                                              *
    //*****************************************************************************************************************
    //
    public static boolean EnvStringErro(Socket connect, int Erro, boolean Verbose) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(connect.getOutputStream());
            String LinhaInicial = "";
            String MsgErro = "";
            String Tipo = "text/html";
            if (Erro == 404) {
                LinhaInicial = "HTTP/1.1 404 File Not Found";
                MsgErro = "<h2>404 File Not Found</h2><h3>HTTP/1.1 PraxServer</h3>";
            }

            if (Erro == 501) {
                LinhaInicial = "HTTP/1.1 501 Not Implemented";
                MsgErro = "<h2>501 Not Implemented</h2><h3>HTTP/1.1 PraxServer</h3>";
            }
            int TamMsg = MsgErro.length();
            out.println(LinhaInicial);
            out.println("Server: Java HTTP Server from PraxServer : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: " + Tipo);
            out.println("Content-length: " + TamMsg);
            out.println();
            out.print(MsgErro);
            out.flush();

            Util.Terminal("Enviada Mensagem de Erro: " + LinhaInicial, false, Verbose);

            return(true);
        }
        catch (IOException ioe) {
            Util.Terminal("Erro ao enviar a mensagem de erro lida de uma string", false, Verbose);
            return(false);
        }
    }  // Fim do Método


    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome do Método: UBytetoInt                                                                                      *
    //                                                                                                                 *
    // Funcao: converte um byte sem sinal para inteiro (0 a 255)                                                       *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    private int UBytetoInt(byte valor) {
        int res = valor;
        if (valor < 0) {
            res = 256 + valor;
        }
        return res;
    }


    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome do Método: BinUDP1                                                                                         *
    //                                                                                                                 *
    // Funcao: envia uma mensagem de requisição e recebe a mensagem de resposta do Controlador de Água Quente          *
    //         em formato binário                                                                                      *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    static int BinUDP1(String EndIP, int Porta, boolean Verbose) {
        int TamMsgRsp = 84;

        try {
            byte[] MsgReq = new byte[16]; 	                // Define o Buffer de Transmissao
            byte[] MsgBinRec = new byte[TamMsgRsp];
            int TamCab = 8;
            MsgReq[0]= 1;

            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(EndIP);
            clientSocket.setSoTimeout(2000);
            DatagramPacket sendPacket = new DatagramPacket(MsgReq, TamCab, IPAddress, Porta);

            clientSocket.send(sendPacket);
            String MsgTerm = "Enviada Requisicao Binaria para o Controlador de Água Quente";
            Util.Terminal(MsgTerm, false, Verbose);
            int EstUTRAQ = 0;
            // Espera a Mensagem Binária de Resposta. Se a mensagem de resposta  for recebida, carrega nas variáveis
            try {
                DatagramPacket receivePacket = new DatagramPacket(MsgBinRec, TamMsgRsp);
                clientSocket.receive(receivePacket);

                LeEstMeds1(MsgBinRec);  // Carrega as informações recebidas nas variáveis
                MsgTerm = "Recebida Mensagem Binaria do Controlador de Água Quente";
                Util.Terminal(MsgTerm, false, Verbose);
                EstUTRAQ = 1;
            }
            catch(java.net.SocketTimeoutException e) {
                MsgBinRec[0] = 0x40;
                MsgBinRec[1] = 1;
                MsgBinRec[30] = 0;

                MsgTerm = "Erro: o Controlador de Água Quente nao Respondeu";
                Util.Terminal(MsgTerm, false, Verbose);
            }
            clientSocket.close();
        }
        catch (IOException err) {
            Util.Terminal("Erro na Rotina EnvRecMsgSrv: " + err, false, Verbose);
        }
        return(TamMsgRsp);
    }


    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome do Método: LeEstMeds1()                                                                                    *
    //                                                                                                                 *
    // Funcao: carrega as informações recebidas do Controlador de Água Quente nas variáveis                            *
    //                                                                                                                 *
    // Entrada: array com a mensagem recebida                                                                          *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    static void LeEstMeds1(byte[] MsgBinRec) {

        byte EstComUTR = MsgBinRec[27];
        byte EstCom1 = MsgBinRec[30];

        // Le os Estados Digitais da mensagem recebida
        int EstadoBombaAQ = Util.BytetoInt(MsgBinRec[73]);		// Estado da Bomba de Água Quente
        int EstadoAquecedor = Util.BytetoInt(MsgBinRec[72]);	    // Estado do Aquecedor do Boiler

        // Le as Medidas da mensagem recebida
        int TemperaturaBoiler = Util.TwoBytetoInt(MsgBinRec[48], MsgBinRec[49]); 	// Temperatura do Boiler
        int TemperaturaPlaca = Util.TwoBytetoInt(MsgBinRec[51], MsgBinRec[52]); 	// Temperatura da Placa Solar
        int TempoBmbLigada = Util.TwoBytetoInt(MsgBinRec[66], MsgBinRec[67]); 	// Tempo da Bomba Ligada
        int TempoBmbDesligada = Util.TwoBytetoInt(MsgBinRec[69], MsgBinRec[70]); 	// Tempo da Bomba Desligada

    }
/*
    //*****************************************************************************************************************
    // Nome do Método: BytetoInt                                                                                      *
    //                                                                                                                *
    // Funcao: converte um valor byte para inteiro (conversao sem sinal)                                              *
    // Entrada: valor byte sem sinal de 0 a 255                                                                       *
    // Saida: valor (inteiro) sempre positivo de 0 a 255                                                              *
    //                                                                                                                *
    //*****************************************************************************************************************
    //
    static int BytetoInt(byte valor) {
        if (valor < 0) {
            return(256 + valor);
        }
        else {
            return(valor);
        }
    }

    //*****************************************************************************************************************
    // Nome do Método: TwoBytetoInt                                                                                   *
    //                                                                                                                *
    // Funcao: converte dois bytes em sequencia de um array para inteiro (conversao sem sinal)                        *
    // Entrada: dois bytes consecutivos (LSB e MSB) sem sinal de 0 a 255                                              *
    // Saida: valor (inteiro) sempre positivo de 0 a 65535                                                            *
    //                                                                                                                *
    //*****************************************************************************************************************
    //
    static int TwoBytetoInt(byte LSByte, byte MSByte) {
        int lsb;
        int msb;
        if (LSByte < 0) { lsb = LSByte + 256; }
        else { lsb = LSByte; }
        if (MSByte < 0) { msb = MSByte + 256; }
        else { msb = MSByte; }
        return (lsb + 256*msb);
    }
*/
        /*
    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome da Rotina: InferenciaFuzzyInv1()                                                                           *
    //                                                                                                                 *
    // Funcao: calcula as variáveis de saída dos procedimentos de Inferência Fuzzy do Inversor 1                       *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    static void InferenciaFuzzyInv1() {
        double Ve = VBat/100;
        double We = WEIv1/100;
        double Vs = VSIv1/100;
        double Ws = WSInv1/100;
        double Ei = EfIv1;
        double Td = TDInv1/100;
        double Tt = TTInv1/100;

        // Entrar com o valor das entradas
        fis.setVariable("Ve", Ve);
        fis.setVariable("Vs", Vs);
        fis.setVariable("We", We);
        fis.setVariable("Ws", Ws);
        fis.setVariable("Ei", Ei);
        fis.setVariable("Td", Td);
        fis.setVariable("Tt", Tt);

        // Executar o arquivo FCL
        fis.evaluate();
        String StrEstresse = "" + fis.getVariable("EstresseInversor");
        String StrSaude = "" + fis.getVariable("SaudeInversor");
        String Ponto = ".";

        int Indice = StrEstresse.indexOf("value") + 7;
        EstrIv1 = Util.CharToByte(StrEstresse.charAt(Indice));
        Indice = Indice + 1;
        if (StrEstresse.charAt(Indice) == Ponto.charAt(0)) {
            Indice = Indice + 1;
        }
        else {
            EstrIv1 = 10*EstrIv1 + Util.CharToByte(StrEstresse.charAt(Indice));
            Indice = Indice + 1;
        }

        Indice = StrSaude.indexOf("value") + 7;
        SDIv1 = Util.CharToByte(StrSaude.charAt(Indice));
        Indice = Indice + 1;
        if (StrSaude.charAt(Indice) == Ponto.charAt(0)) {
            Indice = Indice + 1;
        }
        else {
            SDIv1 = 10 * SDIv1 + Util.CharToByte(StrEstresse.charAt(Indice));
            Indice = Indice + 1;
        }

    } // Fim da Rotina InferenciaFuzzyInv1


    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome do Método: InferenciaFuzzyInv2()                                                                           *
    //                                                                                                                 *
    // Funcao: calcula as variáveis de saída dos procedimentos de Inferência Fuzzy do Inversor 2                       *
    //                                                                                                                 *
    // Entrada: nao tem                                                                                                *
    //                                                                                                                 *
    // Saida: nao tem                                                                                                  *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
    static void InferenciaFuzzyInv2() {
        double Ve = Bat/100;
        double We = WEIv2/100;
        double Vs = VSIv2/100;
        double Ws = WSInv2/100;
        double Ei = EfIv2;
        double Td = TDInv2/100;
        double Tt = TTInv2/100;

        // Entrar com o valor das entradas
        fis.setVariable("Ve", Ve);
        fis.setVariable("Vs", Vs);
        fis.setVariable("We", We);
        fis.setVariable("Ws", Ws);
        fis.setVariable("Ei", Ei);
        fis.setVariable("Td", Td);
        fis.setVariable("Tt", Tt);

        // Executar o arquivo FCL
        fis.evaluate();
        String StrSaude = "" + fis.getVariable("SaudeInversor");
        String Ponto = ".";

        //System.out.println(StrSaude);
        int Indice = StrSaude.indexOf("value") + 7;
        int SaudeInv2 = Util.CharToByte(StrSaude.charAt(Indice));
        Indice = Indice + 1;
        if (StrSaude.charAt(Indice) == Ponto.charAt(0)) {
            SDIv2 = SaudeInv2;
        }
        else {
            SDIv2 = 10*SaudeInv2 + Util.CharToByte(StrSaude.charAt(Indice));
        }
    } // Fim da Rotina
    */


}
