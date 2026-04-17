PROYECTO JCLOUD
J-Cloud: Secure Binary Object Storage

J-Cloud es un sistema de almacenamiento de objetos binarios diseñado para simular el comportamiento de servicios como Amazon S3, priorizando la seguridad y la eficiencia mediante un protocolo de comunicación propietario en la capa de aplicación. El sistema no solo almacena datos, sino que garantiza su privacidad mediante un flujo de recepción, cifrado y persistencia controlada.


Filosofía del Proyecto:

La motivación principal de J-Cloud es la implementación de un protocolo de comunicación personalizado que prescinde de HTTP. Al evitar los estándares web tradicionales, el sistema reduce drásticamente la superficie de ataque frente a vulnerabilidades comunes (como inyecciones de headers o ataques de denegación de servicio específicos de HTTP). Solo los clientes que implementen la estructura exacta de la trama de bytes pueden interactuar con el servidor, estableciendo una capa de seguridad por diseño.
Especificación del Protocolo (Binary Frame)

Para que el sistema procese una solicitud, el cliente debe transmitir una trama de bytes con un formato estricto. Cualquier desviación en esta estructura resulta en el rechazo inmediato de la conexión por parte del servidor.
Estructura de la Trama:

    Operation ID (1 byte): Identifica la acción solicitada (ej. 0x01 para Upload, 0x02 para Download).

    User ID (4 bytes): Identificador único del usuario que realiza la operación.

    File Size (8 bytes): Tamaño total del contenido del archivo en bytes.

    Name Size (4 bytes): Longitud del nombre del archivo.

    File Name (Variable): Cadena de caracteres que representa el nombre del objeto.

    Payload (Variable): El cuerpo del archivo a encriptar y almacenar.


Arquitectura del Sistema:

El proyecto se organiza en dos módulos principales que separan la lógica de procesamiento de la visualización:
1. Módulo Core (Server-Side)

Es el motor principal encargado de la escucha y procesamiento de peticiones. Se subdivide en tres componentes internos:

    Entidad Receptora (ServerSocket): Gestiona las conexiones TCP entrantes y valida la integridad inicial de la trama.

    Servicio de Encriptación: Aplica algoritmos de cifrado al payload antes de su escritura en disco, asegurando que los datos en reposo sean inaccesibles sin las llaves correspondientes.

    Servicio de Almacenamiento: Administra la persistencia física de los objetos binarios en el sistema de archivos del servidor.

2. Módulo de Visualización (Dashboard)

Un componente secundario encargado de auditar la carpeta de almacenamiento. Este módulo actúa como un lector independiente que permite visualizar de forma gráfica los objetos gestionados por el proceso principal, facilitando el control sobre el estado del repositorio.
