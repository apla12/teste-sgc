Se a classe ClientSelectionService_ClientSelectionServicePortImpl tiver erros de compila��o verificar a 
inclus�o do jar InterfaceSubsistema_wsdl.jar, nas lib externas -> sgc

Visto que esta classe � autom�ticamente gerada e de forma a evitar problemas foi criada a classe ResponseServiceImpl que cont�m a real implementa��o dos m�todos.

Assim existindo a necessidade de gerar novamente a classe ClientSelectionService_ClientSelectionServicePortImpl os m�todos devem ser delegados para a classe ResponseServiceImpl

Todas as classes aqui inclu�das s�o colocas no response war a ser gerado

