Se a classe SelectionServiceSoap_SelectionServicePortImpl tiver erros de compila��o verificar a 
inclus�o do jar InterfaceSeleccao_wsdl.jar, nas lib externas -> sgc

Visto que esta classe � autom�ticamente gerada e de forma a evitar problemas foi criada a classe RequestServiceImpl que cont�m a real implementa��o dos m�todos.

Assim existindo a necessidade de gerar novamente a classe SelectionServiceSoap_SelectionServicePortImpl os m�todos devem ser delegados para a classe RequestServiceImpl

Todas as classes aqui inclu�das s�o colocas no response war a ser gerado

