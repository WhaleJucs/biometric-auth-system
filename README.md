# Sistema de Autenticação Biométrica

Sistema de Identificação e Autenticação Biométrica para Controle de Acesso ao banco de dados do Ministério do Meio Ambiente.

## Descrição

Este sistema foi desenvolvido como parte de uma atividade prática supervisionada (APS) para a disciplina de **Processamento de Imagem e Visão Computacional (PIVC)**.
O sistema utiliza reconhecimento facial para controlar o acesso a informações estratégicas sobre propriedades rurais que utilizam agrotóxicos proibidos.

## Visão Geral Rápida

Se você já tem Java 11+ e Maven instalados:

```powershell
git clone <URL_DO_REPOSITORIO>
cd biometric-auth-system
mvn clean package
java --enable-native-access=ALL-UNNAMED -jar target/biometric-auth-system-1.0.0-jar-with-dependencies.jar
```

Depois que a aplicação abrir:

1. Vá em "Usuários > Gerenciar Usuários" e cadastre pelo menos 1 usuário com imagens faciais.
2. Vá em "Sistema > Autenticação" e teste o login com uma das imagens cadastradas.
3. Consulte "Relatórios > Logs de Acesso" e "Relatórios > Dashboard".

## Funcionalidades

### Regras de Negócio

O sistema implementa **3 níveis de acesso**:

- **Nível 1**: Acesso público - todos os usuários podem acessar
- **Nível 2**: Acesso restrito - apenas diretores de divisões
- **Nível 3**: Acesso exclusivo - apenas o ministro do meio ambiente

### Funcionalidades Principais

- **Autenticação Biométrica**: Reconhecimento facial usando OpenCV com comparação de histogramas
- **Gerenciamento de Usuários**: CRUD completo de usuários com níveis de acesso
- **Cadastro Biométrico**: Múltiplas imagens por usuário para treinamento
- **Logs de Acesso**: Registro completo de todas as tentativas de acesso
- **Dashboard Administrativo**: Estatísticas e resumo do sistema
- **Relatórios**: Visualização de logs com filtros por status

## Tecnologias Utilizadas

- **Java 11+**: Linguagem de programação
- **Swing**: Interface gráfica
- **OpenCV 4.9.0**: Biblioteca para reconhecimento facial (versão básica, sem módulo contrib)
- **Gson 2.10.1**: Persistência em JSON
- **Maven 3.6+**: Gerenciamento de dependências
- **SLF4J**: Sistema de logging

### Sobre OpenCV e Carregamento Nativo

O projeto usa a dependência `org.openpnp:opencv` que embala automaticamente as bibliotecas nativas. O carregamento é feito por `nu.pattern.OpenCV.loadLocally()`. Em versões mais novas do JDK (21+), você pode ver warnings sobre acesso nativo restrito. Use a flag:

```powershell
java --enable-native-access=ALL-UNNAMED -jar target/biometric-auth-system-1.0.0-jar-with-dependencies.jar
```

Isso elimina o warning: `Restricted methods will be blocked...`.

## Estrutura do Projeto

```
biometric-auth-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── br/edu/biometric/
│   │   │   │   ├── Main.java                    # Classe principal
│   │   │   │   ├── model/                        # Classes de domínio
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── AccessLevel.java
│   │   │   │   │   ├── AccessLog.java
│   │   │   │   │   └── AccessStatus.java
│   │   │   │   ├── repository/                  # Persistência
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── AccessLogRepository.java
│   │   │   │   │   └── LocalDateTimeAdapter.java
│   │   │   │   ├── service/                     # Lógica de negócio
│   │   │   │   │   ├── AuthenticationService.java
│   │   │   │   │   ├── FacialRecognitionService.java
│   │   │   │   │   └── AuthenticationResult.java
│   │   │   │   ├── view/                        # Interface gráfica
│   │   │   │   │   ├── MainFrame.java
│   │   │   │   │   ├── LoginPanel.java
│   │   │   │   │   ├── UserManagementPanel.java
│   │   │   │   │   ├── DashboardPanel.java
│   │   │   │   │   └── LogsPanel.java
│   │   │   │   └── util/                       # Utilitários
│   │   │   │       ├── ImageUtils.java
│   │   │   │       └── Validator.java
│   │   │   └── resources/
│   │   │       └── haarcascades/                # Classificadores Haar Cascade
│   │   │           └── haarcascade_frontalface_default.xml
├── data/                                        # Dados persistidos (criado em runtime)
│   ├── users.json
│   ├── access_logs.json
│   └── biometric/                              # Imagens biométricas
├── target/                                      # Arquivos compilados (criado em runtime)
│   └── biometric-auth-system-1.0.0-jar-with-dependencies.jar
├── pom.xml                                      # Configuração Maven
└── README.md                                    # Este arquivo
```

## Instalação e Configuração

### Pré-requisitos

- **Java JDK 11 ou superior** - [Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [Eclipse Temurin (OpenJDK)](https://adoptium.net/)
- **Maven 3.6+** - [Download Maven](https://maven.apache.org/download.cgi)
- **OpenCV 4.9.0** (será baixado automaticamente via Maven)

### Configuração do Ambiente

#### 1. Instalar Java JDK 11+

**Windows:**

1. Baixe o JDK 11 ou superior do site oficial
2. Execute o instalador e siga as instruções
3. Configure a variável de ambiente `JAVA_HOME`:
   - Abra "Variáveis de Ambiente" (Windows + R → `sysdm.cpl` → Aba "Avançado")
   - Crie uma nova variável do sistema: `JAVA_HOME` = caminho do JDK (ex: `C:\Program Files\Java\jdk-11`)
   - Adicione `%JAVA_HOME%\bin` à variável `Path`
   - Reinicie o terminal/PowerShell

**Verificar instalação:**

```bash
java -version
```

#### 2. Instalar Maven 3.6+

**Windows:**

1. Baixe o Maven do site oficial (arquivo `.zip`)
2. Extraia para um diretório (ex: `C:\Program Files\Apache\maven`)
3. Configure as variáveis de ambiente:
   - Crie `MAVEN_HOME` = caminho do Maven (ex: `C:\Program Files\Apache\maven\apache-maven-3.9.5`)
   - Adicione `%MAVEN_HOME%\bin` à variável `Path`
   - Reinicie o terminal/PowerShell

**Verificar instalação:**

```bash
mvn -version
```

### Passos para Instalação

1. **Clone o repositório** (ou baixe o projeto):

   ```bash
   git clone <url-do-repositório>
   cd biometric-auth-system
   ```

2. **Baixe o arquivo Haar Cascade**:

   - Baixe o arquivo `haarcascade_frontalface_default.xml` do [repositório oficial do OpenCV](https://github.com/opencv/opencv/blob/master/data/haarcascades/haarcascade_frontalface_default.xml)
   - Coloque-o em `src/main/resources/haarcascades/`
   - O arquivo é necessário para a detecção de faces

3. **Compile o projeto**:

   ```bash
   mvn clean compile
   ```

4. **Execute o projeto**:

   ```bash
   mvn exec:java -Dexec.mainClass="br.edu.biometric.Main"
   ```

   Ou compile um JAR executável:

   ```bash
   mvn clean package
   java -jar target/biometric-auth-system-1.0.0-jar-with-dependencies.jar
   ```

#### Execução via IDE (IntelliJ ou VS Code)

1. Importe o projeto Maven.
2. Garanta que o SDK configurado é Java 11+.
3. Marque `src/main/java` como Source Root (se necessário).
4. Rode a classe `br.edu.biometric.Main`.
5. (Opcional) Para remover warnings OpenCV: adicione em Run Configuration / VM Options: `--enable-native-access=ALL-UNNAMED`.

#### Estrutura dos Arquivos de Dados (JSON)

`data/users.json` cada usuário contém (campos principais):

```json
{
  "id": "<uuid>",
  "name": "Nome Completo",
  "cpf": "123.456.789-09",
  "email": "email@dominio.com",
  "accessLevel": "ADMIN",
  "biometricDataPaths": ["c:/caminho/face1.jpg", "c:/caminho/face2.jpg"],
  "active": true,
  "createdAt": "2025-11-24T22:18:34.123",
  "updatedAt": "2025-11-24T22:19:10.456"
}
```

`data/access_logs.json` cada log contém:

```json
{
  "id": "<uuid>",
  "userId": "<uuid-ou-null>",
  "userName": "Nome ou Desconhecido",
  "accessLevel": "ADVANCED", // nível solicitado
  "timestamp": "2025-11-24T22:25:11.987",
  "status": "SUCCESS", // ou DENIED_* / ERROR
  "details": "Bem-vindo(a), Nome! Confiança: 92.00%",
  "confidenceScore": 92.0
}
```

## Fluxo de Uso Recomendado

1. Cadastrar usuários (mínimo 1) com 2–3 imagens cada.
2. Confirmar no Dashboard que o modelo está "Treinado".
3. Realizar autenticações com imagens originais ou similares.
4. Consultar logs e ajustar threshold se necessário.

## Validações Implementadas

| Campo   | Regra                                                      |
| ------- | ---------------------------------------------------------- |
| Nome    | Mínimo 3 caracteres; apenas letras e espaços (acentos OK)  |
| CPF     | 11 dígitos válidos com verificação dos dígitos (módulo 11) |
| Email   | Padrão simples `usuario@dominio`                           |
| Imagens | Pelo menos uma imagem para permitir treinamento            |

## Ajustes de Configuração Importantes

| Configuração                | Local                      | Padrão  | Observação                                 |
| --------------------------- | -------------------------- | ------- | ------------------------------------------ |
| Threshold de confiança      | `FacialRecognitionService` | 70      | Menor = mais rigoroso (distâncias menores) |
| Tamanho normalizado da face | `FACE_SIZE`                | 200x200 | Uniformiza histogramas                     |

Para calibrar: reduza o threshold se muitas falsas aprovações ocorrerem; aumente se estiver barrando usuários legítimos.

## Boas Práticas para Melhora do Reconhecimento

- Use imagens nítidas, boa iluminação e face de frente.
- Evite fundos extremamente complexos ou sombras fortes.
- Cadastre múltiplas imagens com pequenas variações (ângulo leve, expressão neutra).
- Não misture resoluções muito diferentes; mantenha qualidade similar.

## Suprimindo Warnings do OpenCV (JDK 21+)

Se aparecer:

```
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load...
```

Execute com:

```powershell
java --enable-native-access=ALL-UNNAMED -jar target/biometric-auth-system-1.0.0-jar-with-dependencies.jar
```

Isso informa à JVM que o carregamento nativo é intencional.

## Backup e Restauração dos Dados

Para backup, copie a pasta `data/` inteira (incluindo `biometric/`).

```powershell
Compress-Archive -Path data -DestinationPath backup-data.zip
```

Para restaurar, extraia sobre o diretório do projeto antes de iniciar a aplicação.

## Limitações Conhecidas

- Comparação de histogramas é menos robusta que LBPH/EigenFaces/Deep Learning.
- Sensível a variações fortes de iluminação ou pose.
- Não faz detecção multi-usuário simultânea (uma face por autenticação).
- Não há verificação de qualidade automática (ruído, blur).

## Estratégias Futuras (Sugestões)

- Migrar para LBPH (requer opencv-contrib) ou embeddings faciais (FaceNet/Dlib).
- Adicionar captura direta por webcam (já existe referência mas não integrada aqui por escopo).
- Criptografar caminhos ou imagens para maior privacidade.
- Implementar testes automatizados de validação.

## Solução de Problemas (Resumo Rápido)

| Problema                                   | Causa Provável                      | Ação                                            |
| ------------------------------------------ | ----------------------------------- | ----------------------------------------------- |
| "Serviço de reconhecimento não disponível" | Falha ao carregar OpenCV            | Verificar dependência e usar flag native access |
| "Nenhuma face detectada"                   | Imagem inadequada / cascade ausente | Verificar haarcascade e qualidade da imagem     |
| Modelo não treina                          | Usuário sem imagens válidas         | Adicionar imagens claras com face frontal       |
| Baixa confiança em usuários válidos        | Threshold alto ou poucas imagens    | Reduzir threshold / adicionar mais imagens      |
| Falsos positivos                           | Threshold baixo demais              | Aumentar threshold gradualmente                 |

## Testes Manuais Recomendados

1. Cadastrar 2 usuários distintos (3 imagens cada).
2. Autenticar imagem correta → sucesso.
3. Autenticar imagem de outro usuário → negado.
4. Ajustar nível de acesso para um usuário e testar restrição.
5. Limpar logs e gerar novos para confirmar persistência.

## Aviso de Privacidade / Ética

Este sistema manipula dados biométricos (imagens faciais). Recomenda-se:

- Obter consentimento explícito dos usuários.
- Limitar acesso às imagens e arquivos JSON.
- Avaliar anonimização ou criptografia em ambientes de produção.

## Comandos Úteis (PowerShell)

```powershell
# Build completo
mvn clean package

# Execução com suprimir warnings nativos
java --enable-native-access=ALL-UNNAMED -jar target/biometric-auth-system-1.0.0-jar-with-dependencies.jar

# Limpar diretório de dados (cuidado: remove tudo)
Remove-Item -Recurse -Force data
```

## Perguntas Frequentes (FAQ)

**1. Posso usar Java 17 ou 21?** Sim, desde que mantenha a flag de native access para evitar warnings.
**2. Preciso instalar OpenCV manualmente?** Não, a dependência Maven já inclui as libs nativas.
**3. Como melhorar a precisão?** Mais imagens variadas e ajuste no threshold.
**4. Posso trocar o classificador?** Sim, substitua o XML em `resources/haarcascades/`.
**5. O que acontece se mover as imagens?** Caminhos quebrados exigem recadastro do usuário.

---

## Como Usar

### 1. Cadastro de Usuários

1. Acesse o menu **Usuários > Gerenciar Usuários**
2. Clique em **Novo Usuário**
3. Preencha os dados:
   - Nome
   - CPF
   - Email
   - Nível de Acesso (1, 2 ou 3)
4. Adicione **pelo menos uma imagem biométrica** do usuário
5. Clique em **Salvar**

### 2. Autenticação

1. Acesse o menu **Sistema > Autenticação**
2. Selecione o **Nível de Acesso** desejado
3. Clique em **Selecionar Imagem** e escolha uma foto do usuário
4. Clique em **Autenticar**
5. O sistema irá:
   - Detectar a face na imagem
   - Reconhecer o usuário usando comparação de histogramas
   - Verificar o nível de acesso
   - Conceder ou negar o acesso

### 3. Visualizar Logs

1. Acesse o menu **Relatórios > Logs de Acesso**
2. Use o filtro para visualizar logs por status
3. Clique em **Atualizar** para ver os logs mais recentes

### 4. Dashboard

1. Acesse o menu **Relatórios > Dashboard**
2. Visualize estatísticas do sistema:
   - Total de usuários
   - Acessos bem-sucedidos/negados
   - Status do modelo de reconhecimento

## Configurações

### Threshold de Confiança

O sistema usa um threshold de confiança de **70** (quanto menor, mais confiança). Este valor pode ser ajustado em `FacialRecognitionService.java`:

```java
private static final int CONFIDENCE_THRESHOLD = 70;
```

**Nota:** Como o sistema usa comparação de histogramas (não LBPH), você pode precisar ajustar este valor com base nos resultados obtidos. Valores menores = maior exigência de similaridade.

### Tamanho da Face Normalizada

As faces são normalizadas para **200x200 pixels**. Este valor pode ser ajustado em `FacialRecognitionService.java`:

```java
private static final Size FACE_SIZE = new Size(200, 200);
```

## Implementação do Reconhecimento Facial

O sistema utiliza uma **implementação alternativa** baseada em comparação de histogramas, ao invés do módulo `opencv-contrib` (que não está disponível via Maven Central). Esta abordagem:

- **Funciona apenas com OpenCV básico** (sem necessidade de módulos adicionais)
- **Mantém a mesma interface** do código original
- **É compatível com todas as funcionalidades** do sistema
- **Pode ter precisão ligeiramente inferior** ao LBPH, mas ainda é eficaz para reconhecimento facial básico

### Como Funciona

1. **Detecção de Faces**: Usa o classificador Haar Cascade para detectar faces nas imagens
2. **Normalização**: Extrai e normaliza as faces para 200x200 pixels em escala de cinza
3. **Treinamento**: Armazena as faces normalizadas de cada usuário
4. **Reconhecimento**: Compara o histograma da face a ser reconhecida com todas as faces treinadas usando correlação
5. **Resultado**: Retorna o usuário com maior similaridade (menor distância)

## Observações Importantes

1. **Imagens Biométricas**:

   - Use imagens com boa iluminação
   - Face frontal e visível
   - Múltiplas imagens melhoram a precisão
   - Imagens similares (mesmo ângulo, iluminação) tendem a ter melhor reconhecimento

2. **Treinamento do Modelo**:

   - O modelo é treinado automaticamente ao salvar usuários
   - É necessário retreinar após adicionar novos usuários
   - O sistema armazena as faces normalizadas em memória durante a execução

3. **Persistência**:
   - Dados são salvos em arquivos JSON na pasta `data/`
   - Imagens biométricas são referenciadas por caminho
   - Certifique-se de que os caminhos das imagens permaneçam válidos

## Solução de Problemas

### Erro ao carregar OpenCV

Se você encontrar erros relacionados ao OpenCV:

1. Verifique se a biblioteca foi baixada corretamente via Maven (`mvn clean compile`)
2. Certifique-se de que está usando Java 11+
3. Verifique se o arquivo Haar Cascade está presente em `src/main/resources/haarcascades/`

### Nenhuma face detectada

- Verifique se a imagem contém uma face visível e frontal
- Tente usar imagens com melhor iluminação
- Certifique-se de que o arquivo `haarcascade_frontalface_default.xml` está presente
- Imagens muito pequenas ou com baixa qualidade podem não ser detectadas

### Modelo não treinado

- Certifique-se de que há usuários cadastrados com imagens biométricas
- Verifique se as imagens são válidas e contêm faces detectáveis
- O sistema precisa de pelo menos uma face extraída para cada usuário

### Warnings durante a compilação

Os seguintes warnings podem aparecer durante a compilação e podem ser **ignorados com segurança**:

- **WARNING sobre `sun.misc.Unsafe`**: Aviso de depreciação do Maven/Guice, não afeta o projeto
- **WARNING sobre `location of system modules`**: Recomendação para usar `--release 11`, mas não impede a execução

Estes warnings não impedem a compilação ou execução do projeto.

### Baixa precisão no reconhecimento

Se o sistema não estiver reconhecendo corretamente:

1. **Ajuste o threshold de confiança** em `FacialRecognitionService.java`
2. **Use mais imagens de treinamento** por usuário (diferentes ângulos, iluminações)
3. **Certifique-se de que as imagens de teste são similares às de treinamento**
4. **Verifique se as faces estão sendo detectadas corretamente** (use imagens com boa qualidade)

## Licença

Este projeto foi desenvolvido para fins acadêmicos como parte de uma atividade prática supervisionada.

## Autores

- [João Pedro Rezende] - RA: [G86FDC9]
- [Bryan Verza Pinaffo] - RA: [G8594E1]
- [Julia Cardoso Poszar] - RA: [N284JD6]
- [Renato Massayuki Souza Eto] - RA: [G825489]

## Referências

- OpenCV Documentation: https://docs.opencv.org/
- Maven Documentation: https://maven.apache.org/
- Java Swing Tutorial: https://docs.oracle.com/javase/tutorial/uiswing/
- OpenCV Haar Cascades: https://github.com/opencv/opencv/tree/master/data/haarcascades

---

**Desenvolvido para a disciplina de Processamento de Imagem e Visão Computacional (PIVC)**
