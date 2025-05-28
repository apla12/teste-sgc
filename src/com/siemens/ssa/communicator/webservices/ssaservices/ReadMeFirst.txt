Se a classe SelectionServiceSoap_SelectionServicePortImpl tiver erros de compilação verificar a 
inclusão do jar InterfaceSeleccao_wsdl.jar, nas lib externas -> sgc

Visto que esta classe é automáticamente gerada e de forma a evitar problemas foi criada a classe RequestServiceImpl que contém a real implementação dos métodos.

Assim existindo a necessidade de gerar novamente a classe SelectionServiceSoap_SelectionServicePortImpl os métodos devem ser delegados para a classe RequestServiceImpl

Todas as classes aqui incluídas são colocas no response war a ser gerado

