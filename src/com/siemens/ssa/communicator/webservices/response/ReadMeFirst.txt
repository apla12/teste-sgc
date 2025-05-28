Se a classe ClientSelectionService_ClientSelectionServicePortImpl tiver erros de compilação verificar a 
inclusão do jar InterfaceSubsistema_wsdl.jar, nas lib externas -> sgc

Visto que esta classe é automáticamente gerada e de forma a evitar problemas foi criada a classe ResponseServiceImpl que contém a real implementação dos métodos.

Assim existindo a necessidade de gerar novamente a classe ClientSelectionService_ClientSelectionServicePortImpl os métodos devem ser delegados para a classe ResponseServiceImpl

Todas as classes aqui incluídas são colocas no response war a ser gerado

