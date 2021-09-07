# ServHTTPSimples

**Este repositório contém basicamente três itens:**

**1) Software Servidor HTTP**

Descrição: este servidor HTTP pode ser configurado para dois tipos de operação: Intranet e Nuvem.

No caso de operação local, o software efetua diretamente a comunicação com o equipamento concentrador da usina para realizar a atualização da sua base de dados (a base de dados é composta por variáveis de supervisão e controle).

No caso de operação em nuvem, o servidor deve receber mensagens HTTP binárias (tipo Octet-Stream) para atualizar a sua base de dados. A mensagem binária de atualização é feita pelo software Atualizador que também está neste repositório.

**2) Software Atualizador**

Descrição: este software efetua periodicamente a comunicação com o equipamento concentrador da usina, enviando em seguida, para o servidor HTTP, a mensagem binária de atualização da base de dados.

**3) Recursos HTML, CSS e Javascript para carga no Navegador do Cliente HTTP**

Descrição: o arquivo HTML e o arquivo CSS montam uma tabela com os identificadores e os valores de cada campo de supervisão ou controle. O programa Javascript tem por função principal solicitar periodicamente ao servidor uma mensagem em formato XML com os valores atualizados de todas as variáveis.

Observação: , os softwares dos itens 1 e 2 fazem parte do mesmo projeto Java, pois, compartilham algumas classes.
