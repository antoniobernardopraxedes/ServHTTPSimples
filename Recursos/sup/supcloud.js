//******************************************************************************************************************
//                                                                                                                 *
//                   Programa Principal Javascript da Página de Supervisão do Servidor em Nuvem                    *                  *
//                                                                                                                 *
//******************************************************************************************************************
//
console.log("Programa Javascript supcloud.js ");
var ContAtualAuto = 0;
var ModoComando = "Local";
//var Comando = "00";
loadXMLDoc("local001.xml");
setInterval(loadXMLDoc, 3000, "local001.xml");

//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: loadXMLDoc()                                                                         *
//                                                                                                                 *
// Função: solicita ao Servidor um Recurso ou envia um Comando.                                                    *
//                                                                                                                 *
// Entrada: String com o nome do Recurso. Se nao for inserida a string com o nome do recurso (valor = undefined),  *
//          solicita apenas a atualizacao dos valores das variaveis.                                               *
//                                                                                                                 *
//******************************************************************************************************************
//
function loadXMLDoc(recurso) {

    ContAtualAuto = ContAtualAuto + 1;
    
    ImprimeComando(recurso);
    var xhttp = new XMLHttpRequest();
    if ((recurso.charAt(0) == "c") && (recurso.charAt(1) == "m") && (recurso.charAt(2) == "d")) {
        xhttp.open("POST", recurso, false);
    }
    else {
        xhttp.open("GET", recurso, false);
    }

    try {
        xhttp.send();
        if (xhttp.status != 200) {
            document.getElementById("comsrv").innerHTML = "Arq. Inv.";
            document.getElementById("comsrv").style.color = "red";
        }
        else {
            console.log("Arquivo XML Recebido OK - " + ContAtualAuto);
            document.getElementById("comsrv").innerHTML = "Normal";
            document.getElementById("comsrv").style.color = "blue";
            
            var xmlRec = xhttp.responseXML;
                        
		          CarregaVariaveis_GERAL(xmlRec);
		          CarregaVariaveis_AGUA(xmlRec);
		          CarregaVariaveis_GERCONS(xmlRec);
		          CarregaVariaveis_INV(xmlRec);
            document.getElementById("erro").innerHTML = "     ";
        }
	   } catch(err) {
        console.log("Falha no recebimento do arquivo XML - " + ContAtualAuto + " - Erro: " + err);
        document.getElementById("comsrv").innerHTML = "Falha";
        document.getElementById("comsrv").style.color = "red";
        document.getElementById("erro").innerHTML = err;
        CarregaVariaveis_GERAL_Falha();
        CarregaVariaveis_AGUA_Falha();
        CarregaVariaveis_GERCONS_Falha();
        CarregaVariaveis_INV_Falha();
    }
}

//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: ImprimeComando(recurso)                                                              *
//                                                                                                                 *
// Função: solicita ao Servidor um Recurso ou envia um Comando.                                                    *
//                                                                                                                 *
// Entrada: String com o nome do Recurso (recurso)                                                                 *
//                                                                                                                 *
//******************************************************************************************************************
//
function ImprimeComando(recurso) {

    if (ModoComando == "Remoto") {
              
        if (recurso == "cmd=0004") {
            document.getElementById("cmdex").innerHTML = "Comando Modo Normal";
    	   }
        if (recurso == "cmd=0016") {
      	     document.getElementById("cmdex").innerHTML = "Comando Modo Manual Carga 1";
    	   }
         if (recurso == "cmd=0005") {
    	        document.getElementById("cmdex").innerHTML = "Comando Modo Manual Cargas 234";
    	   }
        if (recurso == "cmd=0002") {
            document.getElementById("cmdex").innerHTML = "Comando de Acerto de Relogio";
    	   }
        if (recurso == "cmd=0007") {
            document.getElementById("cmdex").innerHTML = "Comando Habilita Carga 1";
    	   }
        if (recurso == "cmd=0009") {
            document.getElementById("cmdex").innerHTML = "Comando Habilita Carga 2";
    	   }
        if (recurso == "cmd=0011") {
            document.getElementById("cmdex").innerHTML = "Comando Habilita Carga 3";
    	   }
        if (recurso == "cmd=0013") {
            document.getElementById("cmdex").innerHTML = "Comando Habilita Carga 4";
    	   }
        if (recurso == "cmd=0003") {
            document.getElementById("cmdex").innerHTML = "Comando Modo Economia";
    	   }
        if (recurso == "cmd=0017") {
            document.getElementById("cmdex").innerHTML = "Comando Modo Auto Carga 1";
    	   }
        if (recurso == "cmd=0006") {
            document.getElementById("cmdex").innerHTML = "Comando Modo Auto Cargas 234";
    	   }
        if (recurso == "cmd=0015") {
            document.getElementById("cmdex").innerHTML = "Comando Apaga Indicadores de Falha";
    	   }
        if (recurso == "cmd=0008") {
            document.getElementById("cmdex").innerHTML = "Comando Desabilita Carga 1";
    	   }
        if (recurso == "cmd=0010") {
            document.getElementById("cmdex").innerHTML = "Comando Desabilita Carga 2";
    	   }
        if (recurso == "cmd=0012") {
            document.getElementById("cmdex").innerHTML = "Comando Desabilita Carga 3";
    	   }
        if (recurso == "cmd=0014") {
            document.getElementById("cmdex").innerHTML = "Comando Desabilita Carga 4";
    	   }
    }
    else {
        document.getElementById("cmdex").innerHTML = "Comando Não Autorizado (Local)";
    }
}


//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: CarregaVariaveis_GERAL                                                               *
//                                                                                                                 *
// Função: carrega na tabela HTML as variaveis de supervisao da seção GERAL lidas do arquivo XML                   *
//                                                                                                                 *
// Entrada: variavel com o arquivo XML recebido do Servidor                                                        *
//                                                                                                                 *
//******************************************************************************************************************
//
function CarregaVariaveis_GERAL(ArqVarXML) {
    var i = 0;
    var geral = ArqVarXML.getElementsByTagName("GERAL");
     
    valor = geral[i].getElementsByTagName("COMCNV")[0].childNodes[0].nodeValue;
    document.getElementById("comcnv").innerHTML = valor;
    document.getElementById("comcnv").style.color = CorFonte1(valor);
      
    valor = geral[i].getElementsByTagName("COMCNC")[0].childNodes[0].nodeValue;
    document.getElementById("comcnc").innerHTML = valor;
    document.getElementById("comcnc").style.color = CorFonte1(valor);
      
    valor = geral[i].getElementsByTagName("COMUTR")[0].childNodes[0].nodeValue;
    document.getElementById("comutr").innerHTML = valor;
    document.getElementById("comutr").style.color = CorFonte1(valor);
      
    valor = geral[i].getElementsByTagName("COMCC1")[0].childNodes[0].nodeValue;
    document.getElementById("comcc1").innerHTML = valor;
    document.getElementById("comcc1").style.color = CorFonte1(valor);
      
    valor = geral[i].getElementsByTagName("COMCC2")[0].childNodes[0].nodeValue;
    document.getElementById("comcc2").innerHTML = valor;
    document.getElementById("comcc2").style.color = CorFonte1(valor);
      
    valor = geral[i].getElementsByTagName("CLK")[0].childNodes[0].nodeValue;
    document.getElementById("clk").innerHTML = valor;
    valor = geral[i].getElementsByTagName("DATA")[0].childNodes[0].nodeValue;
    document.getElementById("data").innerHTML = valor;

    valor = geral[i].getElementsByTagName("CMDEX")[0].childNodes[0].nodeValue;
    document.getElementById("cmdex").innerHTML = valor;

    valor = geral[i].getElementsByTagName("MDOP")[0].childNodes[0].nodeValue;
    document.getElementById("mdop").innerHTML = valor;
    ModoComando = geral[i].getElementsByTagName("MDCOM")[0].childNodes[0].nodeValue;
    document.getElementById("mdcom").innerHTML = ModoComando;
    valor = geral[i].getElementsByTagName("MDCT1")[0].childNodes[0].nodeValue;
    document.getElementById("mdct1").innerHTML = valor;
    valor = geral[i].getElementsByTagName("MDCT234")[0].childNodes[0].nodeValue;
    document.getElementById("mdct234").innerHTML = valor;
    valor = geral[i].getElementsByTagName("ENCG1")[0].childNodes[0].nodeValue;
    document.getElementById("encg1").innerHTML = valor;
    valor = geral[i].getElementsByTagName("ENCG2")[0].childNodes[0].nodeValue;
    document.getElementById("encg2").innerHTML = valor;
    valor = geral[i].getElementsByTagName("ENCG3")[0].childNodes[0].nodeValue;
    document.getElementById("encg3").innerHTML = valor;
    valor = geral[i].getElementsByTagName("ICG3")[0].childNodes[0].nodeValue;
    document.getElementById("icg3").innerHTML = valor;
    valor = geral[i].getElementsByTagName("VBAT")[0].childNodes[0].nodeValue;
    document.getElementById("vbat").innerHTML = valor;
    valor = geral[i].getElementsByTagName("VREDE")[0].childNodes[0].nodeValue;
    document.getElementById("vrede").innerHTML = valor;
    valor = geral[i].getElementsByTagName("ESTVRD")[0].childNodes[0].nodeValue;
    document.getElementById("estvrd").innerHTML = valor;
    valor = geral[i].getElementsByTagName("TBAT")[0].childNodes[0].nodeValue;
    document.getElementById("tbat").innerHTML = valor;
    //valor = geral[i].getElementsByTagName("SDBAT")[0].childNodes[0].nodeValue;
    //document.getElementById("sdbat").innerHTML = valor;
    
} // Fim da Rotina CarregaVariaveis_GERAL
    
    
 function CorFonte1(val) {
    
    if (val == "Normal") {
        return("blue");
    }
    else {
        return("red");
    }
 }
    

 //******************************************************************************************************************
 //                                                                                                                 *
 // Nome da Funcao Javascript: CarregaVariaveis_GERAL_Falha                                                         *
 //                                                                                                                 *
 // Função: carrega na tabela HTML ---------- nas variaveis de supervisao da seção GERAL                            *
 //         para indicar falha de conexao                                                                           *
 //                                                                                                                 *
 // Entrada: nao tem                                                                                                *
 //                                                                                                                 *
 //******************************************************************************************************************
 //
 function CarregaVariaveis_GERAL_Falha() {
     
    document.getElementById("comcnv").innerHTML = "----------";
    document.getElementById("comcnc").innerHTML = "----------";
    document.getElementById("comutr").innerHTML = "----------";
    document.getElementById("comcc1").innerHTML = "----------";
    document.getElementById("comcc2").innerHTML = "----------";
    document.getElementById("clk").innerHTML = "----------";
    document.getElementById("data").innerHTML = "----------";
    document.getElementById("mdop").innerHTML = "----------";
    document.getElementById("mdcom").innerHTML = "----------";
    document.getElementById("mdct1").innerHTML = "----------";
    document.getElementById("mdct234").innerHTML = "----------";
    document.getElementById("encg1").innerHTML = "----------";
    document.getElementById("encg2").innerHTML = "----------";
    document.getElementById("encg3").innerHTML = "----------";
    document.getElementById("icg3").innerHTML = "----------";
    document.getElementById("vbat").innerHTML = "----------";
    document.getElementById("vrede").innerHTML = "----------";
    document.getElementById("estvrd").innerHTML = "----------";
    document.getElementById("tbat").innerHTML = "----------";
    //document.getElementById("sdbat").innerHTML = "----------";
    
 } // Fim da Rotina CarregaVariaveis_GERAL_Falha

    
 //******************************************************************************************************************
 //                                                                                                                 *
 // Nome da Funcao Javascript: CarregaVariaveis_AGUA                                                                *
 //                                                                                                                 *
 // Função: carrega na tabela HTML as variaveis de supervisao da seção AGUA lidas do arquivo XML                    *
 //                                                                                                                 *
 // Entrada: variavel com o arquivo XML recebido do Servidor                                                        *
 //                                                                                                                 *
 //******************************************************************************************************************
 //
 function CarregaVariaveis_AGUA(ArqVarXML) {
    var i = 0;
    var agua = ArqVarXML.getElementsByTagName("AGUA");
    valor = agua[i].getElementsByTagName("ESTCXAZ")[0].childNodes[0].nodeValue;
    document.getElementById("estcxaz").innerHTML = valor;
    valor = agua[i].getElementsByTagName("NIVCXAZ")[0].childNodes[0].nodeValue;
    document.getElementById("nivcxaz").innerHTML = valor;
    valor = agua[i].getElementsByTagName("ESTBMB")[0].childNodes[0].nodeValue;
    document.getElementById("estbmb").innerHTML = valor;
    valor = agua[i].getElementsByTagName("ESTDJB")[0].childNodes[0].nodeValue;
    document.getElementById("estdjb").innerHTML = valor;
    valor = agua[i].getElementsByTagName("ESTDJRB")[0].childNodes[0].nodeValue;
    document.getElementById("estdjrb").innerHTML = valor;
    valor = agua[i].getElementsByTagName("ENBMB")[0].childNodes[0].nodeValue;
    document.getElementById("enbmb").innerHTML = valor;
    valor = agua[i].getElementsByTagName("TMPBL")[0].childNodes[0].nodeValue;
    document.getElementById("tmpbl").innerHTML = valor;
      
} // Fim da Rotina CarregaVariaveis_AGUA


//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: CarregaVariaveis_AGUA_Falha                                                          *
//                                                                                                                 *
// Função: carrega na tabela HTML ---------- nas variaveis de supervisao da seção GERAL                            *
//         para indicar falha de conexao                                                                           *
//                                                                                                                 *
// Entrada: nao tem                                                                                                *
//                                                                                                                 *
//******************************************************************************************************************
//
function CarregaVariaveis_AGUA_Falha() {
    document.getElementById("estcxaz").innerHTML = "----------";
    document.getElementById("nivcxaz").innerHTML = "----------";
    document.getElementById("estbmb").innerHTML = "----------";
    document.getElementById("estdjb").innerHTML = "----------";
    document.getElementById("estdjrb").innerHTML = "----------";
    document.getElementById("enbmb").innerHTML = "----------";
    document.getElementById("tmpbl").innerHTML = "----------";
      
} // Fim da Rotina CarregaVariaveis_AGUA_Falha


//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: CarregaVariaveis_GERCONS                                                             *
//                                                                                                                 *
// Função: carrega na tabela HTML as variaveis de supervisao da seção GERCONS lidas do arquivo XML                 *
//                                                                                                                 *
// Entrada: variavel com o arquivo XML recebido do Servidor                                                        *
//                                                                                                                 *
//******************************************************************************************************************
//
function CarregaVariaveis_GERCONS(ArqVarXML) {
    var i = 0;
      var gercons = ArqVarXML.getElementsByTagName("GERCONS");
		
      // Controlador de Carga 1 (CC1)
      valor = gercons[i].getElementsByTagName("VP12")[0].childNodes[0].nodeValue;
      document.getElementById("vp12").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("IS12")[0].childNodes[0].nodeValue;
      document.getElementById("is12").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ISCC1")[0].childNodes[0].nodeValue;
      document.getElementById("iscc1").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("WSCC1")[0].childNodes[0].nodeValue;
      document.getElementById("wscc1").innerHTML = valor;
      //valor = gercons[i].getElementsByTagName("SDCC1")[0].childNodes[0].nodeValue;
      //document.getElementById("sdcc1").innerHTML = valor;
		        
      // Controlador de Carga 2 (CC2)
      valor = gercons[i].getElementsByTagName("VP34")[0].childNodes[0].nodeValue;
      document.getElementById("vp34").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("IS34")[0].childNodes[0].nodeValue;
      document.getElementById("is34").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ISCC2")[0].childNodes[0].nodeValue;
      document.getElementById("iscc2").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("WSCC2")[0].childNodes[0].nodeValue;
      document.getElementById("wscc2").innerHTML = valor;
      //valor = gercons[i].getElementsByTagName("SDCC2")[0].childNodes[0].nodeValue;
      //document.getElementById("sdcc2").innerHTML = valor;
		        
      // Geração e Consumo Totais e Cargas CC
      valor = gercons[i].getElementsByTagName("ITOTGER")[0].childNodes[0].nodeValue;
      document.getElementById("itotger").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("WTOTGER")[0].childNodes[0].nodeValue;
      document.getElementById("wtotger").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ITOTCG")[0].childNodes[0].nodeValue;
      document.getElementById("itotcg").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("WTOTCG")[0].childNodes[0].nodeValue;
      document.getElementById("wtotcg").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ESTFT1")[0].childNodes[0].nodeValue;
      document.getElementById("estft1").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ESTFT2")[0].childNodes[0].nodeValue;
      document.getElementById("estft2").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ICIRCC")[0].childNodes[0].nodeValue;
      document.getElementById("icircc").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("WCIRCC")[0].childNodes[0].nodeValue;
      document.getElementById("wcircc").innerHTML = valor;
      
} // Fim da Rotina CarregaVariaveis_GERCONS
    

//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: CarregaVariaveis_GERCONS_Falha                                                       *
//                                                                                                                 *
// Função: carrega na tabela HTML ---------- nas variaveis de supervisao da seção GERAL                            *
//         para indicar falha de conexao                                                                           *
//                                                                                                                 *
// Entrada: nao tem                                                                                                *
//                                                                                                                 *
//******************************************************************************************************************
//
function CarregaVariaveis_GERCONS_Falha() {
      	
    // Controlador de Carga 1 (CC1)
    document.getElementById("vp12").innerHTML = "----------";
    document.getElementById("is12").innerHTML = "----------";
    document.getElementById("iscc1").innerHTML = "----------";
    document.getElementById("wscc1").innerHTML = "----------";
    //document.getElementById("sdcc1").innerHTML = "----------";
		        
    // Controlador de Carga 2 (CC2)
    document.getElementById("vp34").innerHTML = "----------";
    document.getElementById("is34").innerHTML = "----------";
    document.getElementById("iscc2").innerHTML = "----------";
    document.getElementById("wscc2").innerHTML = "----------";
    //document.getElementById("sdcc2").innerHTML = "----------";
		        
    // Geração e Consumo Totais e Cargas CC
    document.getElementById("itotger").innerHTML = "----------";
    document.getElementById("wtotger").innerHTML = "----------";
    document.getElementById("itotcg").innerHTML = "----------";
    document.getElementById("wtotcg").innerHTML = "----------";
    document.getElementById("estft1").innerHTML = "----------";
    document.getElementById("estft2").innerHTML = "----------";
    document.getElementById("icircc").innerHTML = "----------";
    document.getElementById("wcircc").innerHTML = "----------";
      
} // Fim da Rotina CarregaVariaveis_GERCONS_Falha
    

//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: CarregaVariaveis_INV                                                                 *
//                                                                                                                 *
// Função: carrega na tabela HTML as variaveis de supervisao da seção INV lidas do arquivo XML                     *
//                                                                                                                 *
// Entrada: variavel com o arquivo XML recebido do Servidor                                                        *
//                                                                                                                 *
//******************************************************************************************************************
//
function CarregaVariaveis_INV(ArqVarXML) {
    var i = 0;
    var inv = ArqVarXML.getElementsByTagName("INV");
		
    // Inversor 2
    valor = inv[i].getElementsByTagName("ESTIV2")[0].childNodes[0].nodeValue;
    document.getElementById("estiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("IEIV2")[0].childNodes[0].nodeValue;
    document.getElementById("ieiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("WEIV2")[0].childNodes[0].nodeValue;
    document.getElementById("weiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("VSIV2")[0].childNodes[0].nodeValue;
    document.getElementById("vsiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("ISIV2")[0].childNodes[0].nodeValue;
    document.getElementById("isiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("WSIV2")[0].childNodes[0].nodeValue;
    document.getElementById("wsiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("TDIV2")[0].childNodes[0].nodeValue;
    document.getElementById("tdiv2").innerHTML = valor;
    valor = inv[i].getElementsByTagName("TTIV2")[0].childNodes[0].nodeValue;
    document.getElementById("ttiv2").innerHTML = valor;
    //valor = inv[i].getElementsByTagName("EFIV2")[0].childNodes[0].nodeValue;
    //document.getElementById("efiv2").innerHTML = valor;
    //valor = inv[i].getElementsByTagName("SDIV2")[0].childNodes[0].nodeValue;
    //document.getElementById("sdiv2").innerHTML = valor;
		        
    // Inversor 1
    valor = inv[i].getElementsByTagName("ESTIV1")[0].childNodes[0].nodeValue;
    document.getElementById("estiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("IEIV1")[0].childNodes[0].nodeValue;
    document.getElementById("ieiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("WEIV1")[0].childNodes[0].nodeValue;
    document.getElementById("weiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("VSIV1")[0].childNodes[0].nodeValue;
    document.getElementById("vsiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("ISIV1")[0].childNodes[0].nodeValue;
    document.getElementById("isiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("WSIV1")[0].childNodes[0].nodeValue;
    document.getElementById("wsiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("TDIV1")[0].childNodes[0].nodeValue;
    document.getElementById("tdiv1").innerHTML = valor;
    valor = inv[i].getElementsByTagName("TTIV1")[0].childNodes[0].nodeValue;
    document.getElementById("ttiv1").innerHTML = valor;
    //valor = inv[i].getElementsByTagName("EFIV1")[0].childNodes[0].nodeValue;
    //document.getElementById("efiv1").innerHTML = valor;
    //valor = inv[i].getElementsByTagName("SDIV1")[0].childNodes[0].nodeValue;
    //document.getElementById("sdiv1").innerHTML = valor;
      
} // Fim da Rotina CarregaVariaveis_INV


//******************************************************************************************************************
//                                                                                                                 *
// Nome da Funcao Javascript: CarregaVariaveis_INV_Falha                                                           *
//                                                                                                                 *
// Função: carrega na tabela HTML ---------- nas variaveis de supervisao da seção GERAL                            *
//         para indicar falha de conexao                                                                           *
//                                                                                                                 *
// Entrada: nao tem                                                                                                *
//                                                                                                                 *
//******************************************************************************************************************
//
function CarregaVariaveis_INV_Falha() {
    	
    // Inversor 2
    document.getElementById("estiv2").innerHTML = "----------";
    document.getElementById("ieiv2").innerHTML = "----------";
    document.getElementById("weiv2").innerHTML = "----------";
    document.getElementById("vsiv2").innerHTML = "----------";
    document.getElementById("isiv2").innerHTML = "----------";
    document.getElementById("wsiv2").innerHTML = "----------";
    document.getElementById("tdiv2").innerHTML = "----------";
    document.getElementById("ttiv2").innerHTML = "----------";
    //document.getElementById("efiv2").innerHTML = "----------";
    //document.getElementById("sdiv2").innerHTML = "----------";
		        
    // Inversor 1
    document.getElementById("estiv1").innerHTML = "----------";
    document.getElementById("ieiv1").innerHTML = "----------";
    document.getElementById("weiv1").innerHTML = "----------";
    document.getElementById("vsiv1").innerHTML = "----------";
    document.getElementById("isiv1").innerHTML = "----------";
    document.getElementById("wsiv1").innerHTML = "----------";
    document.getElementById("tdiv1").innerHTML = "----------";
    document.getElementById("ttiv1").innerHTML = "----------";
    //document.getElementById("efiv1").innerHTML = "----------";
    //document.getElementById("sdiv1").innerHTML = "----------";
      
} // Fim da Rotina CarregaVariaveis_INV_Falha