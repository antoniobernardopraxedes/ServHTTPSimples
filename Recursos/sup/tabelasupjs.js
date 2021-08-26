    //******************************************************************************************************************
    //                                                                                                                 *
    //                                          Programa Principal Javascript                                          *
    //                                                                                                                 *
    //******************************************************************************************************************
    //	  
    ContAtualAuto = 0;
    ModoComando = "Local";
    loadXMLDoc("local001.xml");
    setInterval(loadXMLDoc, 3000, "local001.xml");
			
    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome da Funcao Javascript: loadXMLDoc()                                                                         *
    //                                                                                                                 *
    // Função: solicita ao Servidor um Recurso pelo Metodo GET. A resposta do servidor eh sempre o arquivo XML         *
    //          com o valor atualizado de todas as variaveis de supervisao                                             *
    //                                                                                                                 *
    // Entrada: String com o nome do Recurso. Se nao for inserida a string com o nome do recurso (valor = undefined),  *
    //          solicita apenas a atualizacao dos valores das variaveis.                                               *
    //                                                                                                                 *
    //******************************************************************************************************************
    //	   
    function loadXMLDoc(recurso) { 
  	 
  	  var Metodo = 0;
  	  if ((recurso.charAt(0) == "c") && (recurso.length == 8)) {
        Metodo = 1;
      }
     
  	  if (Metodo == 1) {
  	    if (ModoComando == "Remoto") {
					ContAtualAuto = 4;
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
					ContAtualAuto = 4;
  	    }
  	  }
  	  
  	  var xhttp = new XMLHttpRequest();
  	  
  	  if (Metodo === 0) {
        xhttp.open("GET", recurso, false);
      }
      if (Metodo == 1) {
        xhttp.open("POST", recurso, false);
      }
    
      try {
        xhttp.send(); 
		    if (xhttp.status != 200) { 
          document.getElementById("comsrv").innerHTML = "Falha Arq.";
          document.getElementById("comsrv").style.color = "red";
        } 
        else { // Se foi lido corretamente um arquivo HTTP formato XML,
          document.getElementById("comsrv").innerHTML = "Normal";
          document.getElementById("comsrv").style.color = "blue";
          var xmlRec = xhttp.responseXML;
		      CarregaVariaveis_GERAL(xmlRec);
		      CarregaVariaveis_AGUA(xmlRec);
		      CarregaVariaveis_GERCONS(xmlRec);
		      CarregaVariaveis_INV(xmlRec);
		      document.getElementById("erro").innerHTML = " ";
					if (ContAtualAuto > 0) {
						ContAtualAuto--;	
					}
					else {
						document.getElementById("cmdex").innerHTML = "Atualização Automática";
					}
				} // else if (xhttp.status != 200) {
      } catch(err) {
        document.getElementById("comsrv").innerHTML = "Falha";
        document.getElementById("comsrv").style.color = "red";
        document.getElementById("erro").innerHTML = err;
      }
    } // Fim da Funcao loadXMLDoc
    
    
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
      valor = geral[i].getElementsByTagName("SDBAT")[0].childNodes[0].nodeValue;
      document.getElementById("sdbat").innerHTML = valor;
    
    } // Fim da Rotina CarregaVariaveis_GERAL
    
    //******************************************************************************************************************
    //                                                                                                                 *
    // Nome da Funcao Javascript: CorFonte1                                                                            *
    //                                                                                                                 *
    // Função: muda a cor da fonte do texto: Normal: azul ou Alerta: vermelho                                          *
    //                                                                                                                 *
    // Entrada: String "Normal" muda o texto para azul. Caso contrário muda o texto para vermelho                      *
    //                                                                                                                 *
    //******************************************************************************************************************
    //
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
      valor = gercons[i].getElementsByTagName("SDCC1")[0].childNodes[0].nodeValue;
      document.getElementById("sdcc1").innerHTML = valor;
		        
      // Controlador de Carga 2 (CC2)
      valor = gercons[i].getElementsByTagName("VP34")[0].childNodes[0].nodeValue;
      document.getElementById("vp34").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("IS34")[0].childNodes[0].nodeValue;
      document.getElementById("is34").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("ISCC2")[0].childNodes[0].nodeValue;
      document.getElementById("iscc2").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("WSCC2")[0].childNodes[0].nodeValue;
      document.getElementById("wscc2").innerHTML = valor;
      valor = gercons[i].getElementsByTagName("SDCC2")[0].childNodes[0].nodeValue;
      document.getElementById("sdcc2").innerHTML = valor;
		        
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
          valor = inv[i].getElementsByTagName("EFIV2")[0].childNodes[0].nodeValue;
          document.getElementById("efiv2").innerHTML = valor;
          valor = inv[i].getElementsByTagName("SDIV2")[0].childNodes[0].nodeValue;
          document.getElementById("sdiv2").innerHTML = valor;
		        
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
          valor = inv[i].getElementsByTagName("EFIV1")[0].childNodes[0].nodeValue;
          document.getElementById("efiv1").innerHTML = valor;
          valor = inv[i].getElementsByTagName("SDIV1")[0].childNodes[0].nodeValue;
          document.getElementById("sdiv1").innerHTML = valor;
      
    } // Fim da Rotina CarregaVariaveis_INV